package trex.examples;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import trex.common.Attribute;
import trex.packets.PubPkt;
import trexengine.ResultListener;
import trexengine.TRexEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

import static trex.common.Consts.ValType.INT;
import static trex.common.Consts.ValType.STRING;
import static trex.examples.RuleR0.EVENT_SMOKE;
import static trex.examples.RuleR0.EVENT_TEMP;

/**
 * Created by sony on 4/4/2021.
 */
public class HttpEngine {

    private static final String HOSTNAME = "0.0.0.0";
    private static final int PORT = 9876; //read from config
    private static final int BACKLOG = 0;

    private static final String HEADER_ALLOW = "Allow";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final int STATUS_OK = 200;
    private static final int STATUS_NOK = 201;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    private static final int NO_RESPONSE_LENGTH = -1;

    private static final String METHOD_POST = "POST";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String ALLOWED_METHODS = METHOD_POST + "," + METHOD_OPTIONS;

    public static void main(String[] args) throws Exception {

        TRexEngine engine = new TRexEngine(1);

        //Creating a Rule to submitted to the engine this could potentially happen via the network by marshalling.
        RuleR1 testRule = new RuleR1();

        //Adding the rule to the engine.
        engine.processRulePkt(testRule.buildRule());
        //Create a thread to handle incoming events.
        engine.finalizing();

        //Adding result listener to inform us about the detected complex event (Subscribing)
        ResultListener listener = new ResultListener(testRule.buildSubscription());
        //Addd the listener to the engine.
        engine.addResultListener(listener);


        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);

            server.createContext("/proc", httpExchange -> {
                Long time = System.currentTimeMillis();
                JsonObject obj = new JsonObject();
                final Headers headers = httpExchange.getResponseHeaders();
                final String requestMethod = httpExchange.getRequestMethod().toUpperCase();

                switch (requestMethod) {
                    case METHOD_POST:
                        final Request request = getRequestParameters(httpExchange.getRequestBody());
                        PubPkt p = processMessage(request.getData());
                        engine.processPubPkt(p);
                        final String responseBody = "{\"message\":\"Status Successfully change to" + "\"}";
                        headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                        httpExchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                        httpExchange.getResponseBody().write(rawResponseBody);
                        httpExchange.close();
                        break;

                    case METHOD_OPTIONS:
                        headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                        httpExchange.sendResponseHeaders(STATUS_NOK, NO_RESPONSE_LENGTH);
                        httpExchange.getResponseBody().write("{\"status\":\"nok\"}".getBytes());
                        httpExchange.close();
                        break;
                    default:
                        headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                        httpExchange.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                        httpExchange.getResponseBody().write("{\"status\":\"nok\"}".getBytes());
                        httpExchange.close();
                        break;
                }
            });

            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static PubPkt processMessage(Data message) {
        PubPkt pubPkt;
        switch (message.getTopic()) {
            case "temp":

                // Temp event
                Attribute tempAttr[] = new Attribute[3];
                tempAttr[0] = new Attribute();
                tempAttr[1] = new Attribute();
                tempAttr[2] = new Attribute();
                // Value attribute
                tempAttr[0].setName("value");
                tempAttr[0].setValType(INT);
                tempAttr[0].setIntVal(Integer.parseInt(message.getValue()));
                // Area attribute
                tempAttr[1].setName("area");
                tempAttr[1].setValType(STRING);
                tempAttr[1].setStringVal(message.getArea());
                // QoS attribute
                tempAttr[2].setName("accuracy");
                tempAttr[2].setValType(INT);
                tempAttr[2].setIntVal(Integer.parseInt(message.getQoS()));
                pubPkt = new PubPkt(EVENT_TEMP, tempAttr, 3);
                pubPkt.setTimeStamp(Long.parseLong(message.getTime()));
                System.out.println("Temp: " + pubPkt.toString());
                return pubPkt;
            case "smoke":
                // Smoke event
                // Area attribute
                Attribute smokeAttr[] = new Attribute[1];
                smokeAttr[0] = new Attribute();
                smokeAttr[0].setName("area");
                smokeAttr[0].setValType(STRING);
                smokeAttr[0].setStringVal(message.getArea());
                pubPkt = new PubPkt(EVENT_SMOKE, smokeAttr, 1);
                pubPkt.setTimeStamp(Long.parseLong(message.getTime()));
                System.out.println("Smoke: " + pubPkt.toString());
                return pubPkt;
            default:
                pubPkt = new PubPkt();
                return pubPkt;
        }

    }

    public class Data {
        private String topic;
        private String value;
        private String time;
        private String uid;
        private String QoS;
        private String Area;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getQoS() {
            return QoS;
        }

        public void setQoS(String qoS) {
            QoS = qoS;
        }

        public String getArea() {
            return Area;
        }

        public void setArea(String area) {
            Area = area;
        }

        @Override
        public String toString() {
            JsonObject object = new JsonObject();
            object.addProperty("topic", this.getTopic());
            object.addProperty("value", this.getValue());
            object.addProperty("time", this.getTime());
            object.addProperty("uid", this.getUid());
            object.addProperty("QoS", this.getQoS());
            object.addProperty("Area", this.getArea());
            return object.toString();
        }
    }


    private class Request {
        private String type;
        private Data data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }


    }

    private static Request getRequestParameters(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String read;
            while ((read = br.readLine()) != null) {
                sb.append(read);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(sb.toString(), Request.class);
    }

}
