package trex.examples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.log4j.Logger;
import trex.common.Attribute;
import trex.communication.TransportManager;
import trex.marshalling.Unmarshaller;
import trex.packets.PubPkt;
import trex.packets.RulePkt;
import trex.packets.SubPkt;
import trex.ruleparser.TRexRuleParser;
import trexengine.ResultListener;
import trexengine.TRexEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static trex.common.Consts.ValType.*;

/**
 * Created by sony on 11/26/2020.
 */
public class GeoTRex {
    private static final Logger logger = Logger.getLogger(GeoTRex.class.getName());
    private TransportManager tManager = new TransportManager(true);
    private static final String HOSTNAME = "0.0.0.0";
    private static final int PORT = 5001;
    private static final int BACKLOG = 300000;

    private static final String HEADER_ALLOW = "Allow";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final int STATUS_OK = 200;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    private static final int NO_RESPONSE_LENGTH = -1;

    private static final String METHOD_POST = "POST";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String ALLOWED_METHODS = METHOD_POST + "," + METHOD_OPTIONS;

    private static final Queue<PubPkt> queue = new LinkedList<PubPkt>();

    private static final TRexEngine trexEngine = new TRexEngine(1);

    /**
     * {\"requestType\":\"RULE\",\"request\":\"Assign 1=>O3,2=>NO2,3=>airQuality Define airQuality(area:Geometry,measuredO3:Int,measuredNO2:Int) From O3(area=>$a) and each NO2([Geometry]areaINTERSECT$a,value>45) within 300000 from O3 Where area:=O3.areaINTERSECTNO2.area,measuredO3:=O3.value,measuredNO2:=NO2.value\",\"requestId\":\"1\"}
     * <p>
     * curl -X POST -d "{\"requestType\":\"RULE\",\"request\":\"Assign 1=">"O3,2=">" NO2,3=">" AirQuality Define AirQuality(area:Geometry,measuredO3:Int,measuredNO2:Int) From O3(area=">"$a) and Each NO2([Geometry]area INTERSECT $a,value">"45) within 300000 from O3 Where area:=O3.area INTERSECTION NO2.area,measuredO3:=O3.value,measuredNO2:=NO2.value;\",\"requestId\":\"1\"}" http://localhost:5001/CEP --header "Content-Type:application/json"
     * <p>
     * Assign 1=>O3,2=>NO2,3=>airQuality Define airQuality(area:Geometry,measuredO3:Int,measuredNO2:Int) From O3(area=>$a) and each NO2([Geometry]areaINTERSECT$a,value>45) within 300000 from O3 Where area:=O3.areaINTERSECTNO2.area,measuredO3:=O3.value,measuredNO2:=NO2.value
     *
     * @param args
     * @throws IOException
     */

    public static void main(String args[]) throws IOException {

        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
//        {
//            RulePkt rule = TRexRuleParser.parse("Assign 1=> O3,2=> NO2,3=> QUALITY Define QUALITY(area:Geometry,measuredO3:Int,measuredNO2:Int) From O3(area=>$a) and Each NO2([Geometry]area INTERSECT $a,value>45) within 300000 from O3 Where area:=O3.area INTERSECTION NO2.area,measuredO3:=O3.value,measuredNO2:=NO2.value;", 1);
//            System.out.println(rule.toString());
//            SetNewEngine(rule, 1);
//
//        }
        server.createContext("/CEP", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                final String requestMethod = he.getRequestMethod().toUpperCase();
                k1: switch (requestMethod) {
                    case METHOD_POST:
                        final Map<String, String> requestParameters = getRequestParameters(he.getRequestBody());
                        String type = requestParameters.get("Type");
                        switch (type) {
                            case "RULE":
                                RulePkt rule = TRexRuleParser.parse(requestParameters.get("Request"), Integer.parseInt(requestParameters.get("RequestId")));
                                //int ruleId = Integer.parseInt(requestParameters.get("RequestId"));
                                //RulePkt rule = TRexRuleParser.parse("Assign 1=> O3,2=> NO2,3=> QUALITY Define QUALITY(area:Geometry,measuredO3:Int,measuredNO2:Int) From O3(area=>$a) and Each NO2([Geometry]area INTERSECT $a,value>45) within 300000 from O3 Where area:=O3.area INTERSECTION NO2.area,measuredO3:=O3.value,measuredNO2:=NO2.value;", 1);
                                System.out.println(rule.toString());
                                SetNewEngine(rule, 1);
                                break;
                            case "SUB":
                                SetSubscription((SubPkt) Unmarshaller.unmarshal(requestParameters.get("Request").getBytes()), Integer.parseInt(requestParameters.get("RequestId")));
                                break;
                            case "PUB":
                                PubPkt pubPkt = buildPublication(requestParameters.get("parameterName"), requestParameters.get("value"), requestParameters.get("time"), requestParameters.get("location"), requestParameters.get("sensingCoverage"));
                                //System.out.println(pubPkt.toString());
                                JsonObject resp = processPubPackets(pubPkt, 1);
                                //trexEngine.processPubPkt(pubPkt);
                                //queue.add(pubPkt);
                                //processPubPackets3();
                                //processPubPackets2(pubPkt, 1);
                                final String responseBody = resp.toString();
                                headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                                final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                                he.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                                he.getResponseBody().write(rawResponseBody);
                                he.close();
                                break k1;
                        }
                        final String responseBody = "['Ok']";
                        headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                        he.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                        he.getResponseBody().write(rawResponseBody);
                        he.close();
                        break;
                    case METHOD_OPTIONS:
                        headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                        he.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);
                        he.getResponseBody().write("ok".getBytes());
                        he.close();
                        break;
                    default:
                        headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                        he.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                        he.getResponseBody().write("ok".getBytes());
                        he.close();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });


//        trexEngine.processRulePkt(new RuleR4().buildRule2());
//        trexEngine.finalizing();
        SetNewEngine(new RuleR4().buildRule(), 1);
        //server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    private static Map<String, String> getRequestParameters(final InputStream requestBody) {
        try {
            InputStream in = requestBody;
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String read;
            while ((read = br.readLine()) != null) {
                //System.out.println(read);
                sb.append(read);
            }
            br.close();
            logger.debug(sb);
            final Map<String, String> requestParameters = new LinkedHashMap<>();
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            RequestParam requestParam = gson.fromJson(sb.toString(), RequestParam.class);
            //RequestParam requestParam = new Gson().fromJson(sb.toString(), RequestParam.class);
            final String requestType = requestParam.requestType;
            final String request = requestParam.request;
            final String requestId = requestParam.requestId;
            final String parameterName = requestParam.event.parameterName;
            final String value = requestParam.event.value;
            final String time = requestParam.event.time;
            final String location = requestParam.event.location;
            final String sensingCoverage = requestParam.event.sensingCoverage;
            requestParameters.put("Type", requestType);
            requestParameters.put("Request", request);
            requestParameters.put("RequestId", requestId);
            requestParameters.put("parameterName", parameterName);
            requestParameters.put("value", value);
            requestParameters.put("time", time);
            requestParameters.put("location", location);
            requestParameters.put("sensingCoverage", sensingCoverage);
            return requestParameters;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class RequestParam {
        String requestType;
        String request;
        String requestId;
        event event;

        RequestParam(String requestType, String request, String requestId, event event) {
            this.requestType = requestType;
            this.request = request;
            this.requestId = requestId;
            this.event = event;
        }

        private class event {
            String parameterName;
            String value;
            String time;
            String location;
            String sensingCoverage;

            event(String parameterName, String value, String time, String location, String sensingCoverage) {
                this.parameterName = parameterName;
                this.value = value;
                this.time = time;
                this.location = location;
                this.sensingCoverage = sensingCoverage;
            }
        }
    }


    static HashMap engineTable = new HashMap<String, TRexEngine>();

    static void SetNewEngine(RulePkt rulePkt, int ruleId) {
        System.out.println("Receiving new rule packet: " + rulePkt.toString());
        TRexEngine engine = new TRexEngine(100);
        //Adding the rule to the engine.
        engine.processRulePkt(rulePkt);
        //Create a thread to handle incoming events.
        engine.finalizing();
        //
        ResultListener listener = new ResultListener("http://0.0.0.0:8000/eventTRex");
        engine.addResultListener(listener);
        //Adding to the engine table
        engineTable.put(ruleId, engine);

        //run to rectify bug! Auogh.
//        {
//            RuleR4 r = new RuleR4();
//            ArrayList<byte[]> pubPkts = null;
//            try {
//                pubPkts = r.buildPublication2();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int i =0 ;
//            for (int c = 0; i < 100 ; c++) {
//                for (byte[] it :
//                        pubPkts) {
//                    PubPkt pubPkt = null;
//                    try {
//                        pubPkt = (PubPkt) Unmarshaller.unmarshal(it);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("Sending Packet #" + i);
//                    System.out.println(pubPkt.toString());
//                    try {
//                        TimeUnit.MILLISECONDS.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    pubPkt.setTimeStamp(System.currentTimeMillis());
//                    engine.processPubPkt(pubPkt);
//                    i++;
//                }
//            }
//        }
    }

    static void SetSubscription(SubPkt subPkt, int ruleId) throws IOException {
        System.out.println("Receiving new subscribing packet: " + subPkt.toString());
        //Adding result listener to inform us about the detected complex event (Subscribing)
        ResultListener listener = new ResultListener(subPkt);
        //Addd the listener to the engine.
        ((TRexEngine) engineTable.get(ruleId)).addResultListener(listener);
    }

    static JsonObject processPubPackets(PubPkt pubPkt, int ruleId) throws IOException {
        //System.out.println("Receiving new public packet: " + pubPkt.toString());
        return ((TRexEngine) engineTable.get(ruleId)).processPubPktTime(pubPkt);
    }

    static void processPubPackets2(PubPkt pubPkt, int ruleId) throws IOException {
        synchronized (queue) {
            PubPkt p = queue.poll();
            if (p != null)
                ((TRexEngine) engineTable.get(ruleId)).processPubPkt(p);
            System.out.println( p.toString());
        }
    }




    static void processPubPackets3() throws IOException {
        synchronized (queue) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            PubPkt p = queue.poll();
            if (p != null){
                trexEngine.processPubPkt(p);
                System.out.println("+++++++++++++: " + p.toString());
            }
        }
    }


    static PubPkt buildPublication(String parameterName, String value, String time, String location, String sensingCoverage) throws IOException {
        int EVENT_O3 = 10;
        int EVENT_NO2 = 11;
        int EVENT_PM2_5 = 12;
        WKTReader rdr = new WKTReader();


        switch (parameterName) {
            case "PM2.5": {
                // PM2_5 event
                Attribute PM2_5Attr[] = new Attribute[3];
                PM2_5Attr[0] = new Attribute();
                PM2_5Attr[1] = new Attribute();
                // Value attribute
                PM2_5Attr[0].setName("value");
                PM2_5Attr[0].setValType(INT);
                PM2_5Attr[0].setIntVal(Integer.parseInt(value));
                PM2_5Attr[2] = new Attribute();
                // Value attribute
                PM2_5Attr[1].setName("value");
                PM2_5Attr[1].setValType(INT);
                PM2_5Attr[1].setIntVal(Integer.parseInt(value));
                // Area attribute
                PM2_5Attr[2].setName("area");
                PM2_5Attr[2].setValType(GEOMETRY);
                try {
                    PM2_5Attr[2].setGeometryVal(rdr.read(sensingCoverage));
                    //PM2_5Attr[2].setGeometryVal(rdr.read("POLYGON ((10 10, 20 20, 30 10, 20 10, 10 10))"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PubPkt pm2_5PubPkt = new PubPkt(EVENT_PM2_5, PM2_5Attr, 3);
                pm2_5PubPkt.setTimeStamp(Long.parseLong(time));
                //System.out.println("-------------: " + pm2_5PubPkt);
                return pm2_5PubPkt;
            }
            case "O3": {
                // O3 event
                Attribute o3Attr[] = new Attribute[3];
                o3Attr[0] = new Attribute();
                o3Attr[1] = new Attribute();
                o3Attr[2] = new Attribute();
                // Area attribute
                o3Attr[0].setName("area");
                o3Attr[0].setValType(GEOMETRY);
                try {
                    o3Attr[0].setGeometryVal(rdr.read(sensingCoverage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Value attribute
                o3Attr[1].setName("value");
                o3Attr[1].setValType(INT);
                o3Attr[1].setIntVal(Integer.parseInt(value));
                // Value attribute
                o3Attr[2].setName("complex");
                o3Attr[2].setValType(STRING);
                o3Attr[2].setStringVal(location);
                PubPkt o3PubPkt = new PubPkt(EVENT_O3, o3Attr, 3);
                o3PubPkt.setTimeStamp(Long.parseLong(time));
                //System.out.println("-------------: " + o3PubPkt);
                return o3PubPkt;
            }
            case "NO2": {
                // NO2 event
                Attribute no2Attr[] = new Attribute[2];
                no2Attr[0] = new Attribute();
                no2Attr[1] = new Attribute();
                // Value attribute
                no2Attr[0].setName("value");
                no2Attr[0].setValType(INT);
                no2Attr[0].setIntVal(Integer.parseInt(value));
                // Area attribute
                no2Attr[1].setName("area");
                no2Attr[1].setValType(GEOMETRY);
                try {
                    no2Attr[1].setGeometryVal(rdr.read(sensingCoverage));
                    //no2Attr[1].setGeometryVal(rdr.read("POLYGON ((500 500, 700 500, 700 600, 600 600, 500 500))"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PubPkt no2PubPkt = new PubPkt(EVENT_NO2, no2Attr, 2);
                no2PubPkt.setTimeStamp(Long.parseLong(time));
                //System.out.println("-------------: " + no2PubPkt);
                return no2PubPkt;

            }
        }
        return new PubPkt();
    }

}
