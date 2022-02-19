package trexengine;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import trex.common.Attribute;
import trex.common.Constraint;
import trex.packets.PubPkt;
import trex.packets.SubPkt;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;



/**
 * Created by sony on 2/3/2020.
 */

public class ResultListener {

    final static Logger logger = Logger.getLogger(ResultListener.class);



    private HashSet<SubPkt> subs= new HashSet<>();

    private HashSet<String> subsCallBacks= new HashSet<>();

    private String name;


    public ResultListener(SubPkt subPkt){
        subs.add(subPkt);
    }

    public ResultListener(String callBack){
        subsCallBacks.add(callBack);
    }

    public ResultListener(String callBack, String engineName){
        subsCallBacks.add(callBack);
        name = engineName;
    }


    void handleResult(Set<PubPkt> genPkts, Long procTime){
        long time = System.currentTimeMillis();
//        for (PubPkt b :
//             genPkts) {
//            for (SubPkt s :
//                    subs) {
//                if (match(s,b) != 0){
//
//                }
//            }

            for (String s :
                    subsCallBacks) {
                for (PubPkt p :
                        genPkts) {



                        Runnable task3 = () -> {
                            //System.out.println(Thread.currentThread().getName() + " is running");

                            try {


                        //logger.info(s);
                        //URL url = new URL(URLEncoder.encode(s, StandardCharsets.UTF_8.toString()));
                        URL url = new URL(s);

                        //String[] s2 = s.split("/" , 4);
//                        System.out.println("1) " + s2[0]);
//                        System.out.println("2) " + s2[1]);
//                        System.out.println("3) " + s2[2]);
                        //String[] s3 = s2[2].split(":", 2);
//                        System.out.println("3.1) " + s3[0]);
//                        System.out.println("3.2) " + s3[1]);
//                        System.out.println("4) " +s2[3]);


//                        URL url = new URL("http://" + URLEncoder.encode(s3[0], StandardCharsets.UTF_8.toString())
//                                + ":"
//                                + URLEncoder.encode(s3[1], StandardCharsets.UTF_8.toString())
//                                + "/"
//                                + URLEncoder.encode(s2[3], StandardCharsets.UTF_8.toString())
//                        );
//

                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);
//                        httpURLConnection.setReadTimeout(15000);
//                        httpURLConnection.setConnectTimeout(15000);


                        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                        JsonObject message = new JsonObject();
                        JsonObject innerMessage = new JsonObject();
                        message.addProperty("type", "pub");
                        innerMessage.addProperty("topic", p.getEventType());
                        innerMessage.add("value", new JsonParser().parse(p.toString()).getAsJsonObject());
                        innerMessage.addProperty("eventTime", p.getTimeStamp());
                        innerMessage.addProperty("time", procTime);
                        innerMessage.addProperty("engineName", name);
                        message.add("data", innerMessage);

                        //String message = "{\"type\":\"pub\",\"data\":{\"topic\":\"" + p.getEventType()  + "\",\"value\":\"" + p.toString() + "\",\"eventTime\":" + p.getTimeStamp() + "\"time\":" + time + "}}";
                        wr.write(message.toString().getBytes());

                        int responseCode = httpURLConnection.getResponseCode();

                        if (responseCode == 200) {
                            logger.debug("{\"message\":\"ok\"}");
                        } else {
                            logger.debug("{\"message\":\"nok\"}");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.info(e.getLocalizedMessage());
                    }

                    };
                    new Thread(task3).start();

                }
            }
        //}
    }
    public int match(SubPkt subPkt, PubPkt pkt) {
        //first I must match event type
        if (subPkt.getEventType() != pkt.getEventType()) return 0;
        //Then constraints
        Collection<Attribute> attrs = pkt.getAttributes();
        //Here comes a list of switches that handle all the value types and the operators.
        //This is just like the one found in the TRexServer project, in the TRexUtils.cpp file
        for (Constraint constr : subPkt.getConstraints()) {
            for (Attribute at : attrs) {
                if (constr.getName().equals(at.getName())) {
                    switch (constr.getValType()) {
                        case INT:
                            switch (constr.getOp()) {
                                case EQ:
                                    if (constr.getIntVal() != at.getIntVal()) return 0;

                                case NE:
                                    if (constr.getIntVal() == at.getIntVal()) return 0;

                                case GT:
                                    if (constr.getIntVal() <= at.getIntVal()) return 0;

                                case LT:
                                    if (constr.getIntVal() >= at.getIntVal()) return 0;

                                case LE:
                                    if (constr.getIntVal() > at.getIntVal()) return 0;

                                case GE:
                                    if (constr.getIntVal() < at.getIntVal()) return 0;
                                default:
                                    break;
                            }
                            break;

                        case FLOAT:
                            switch (constr.getOp()) {
                                case EQ:
                                    if (constr.getFloatVal() != at.getFloatVal()) return 0;

                                case NE:
                                    if (constr.getFloatVal() == at.getFloatVal()) return 0;

                                case GT:
                                    if (constr.getFloatVal() <= at.getFloatVal()) return 0;

                                case LT:
                                    if (constr.getFloatVal() >= at.getFloatVal()) return 0;

                                case LE:
                                    if (constr.getFloatVal() > at.getFloatVal()) return 0;

                                case GE:
                                    if (constr.getFloatVal() < at.getFloatVal()) return 0;

                                default:
                                    break;
                            }
                            break;

                        case BOOL:
                            switch (constr.getOp()) {
                                case EQ:
                                    if (constr.getBoolVal() != at.getBoolVal()) return 0;

                                case NE:
                                    if (constr.getBoolVal() == at.getBoolVal()) return 0;

                                default:
                                    break;
                            }
                            break;

                        case STRING:
                            switch (constr.getOp()) {
                                case EQ:
                                    if (!constr.getStringVal().equals(at.getStringVal())) return 0;

                                case NE:
                                    if (constr.getStringVal().equals(at.getStringVal())) return 0;

                                    //Not defined for strings
                                case GT:
                                    return 0;

                                //Not defined for strings
                                case LT:
                                    return 0;

                                case IN:
                                    //FROM SERVER CODE, TRexUtils.cpp
                                    // The constraint's value should be a substring of the attribute's value:
                                    // it is a filter specified for published events' attributes
                                    if (at.getStringVal().indexOf(constr.getStringVal()) < 0) return 0;

                                default:
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        }
        //And finally the custom matcher
        if (subPkt.match(pkt) == 1) return 1;
        else return -1;
    }

}
