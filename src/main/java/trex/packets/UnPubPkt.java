package trex.packets;

import trex.common.Attribute;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Behnam Khazael
 */
public class UnPubPkt implements TRexPkt{
    private int eventType;
    protected Collection<Attribute> attributes = new ArrayList<Attribute>();

    public UnPubPkt(int eventType) {
        this.eventType = eventType;
        attributes = new ArrayList<Attribute>();
    }

    public UnPubPkt(int eventType, Collection<Attribute> attr) {
        this.eventType = eventType;
        attributes = new ArrayList<Attribute>(attr);
    }


    public UnPubPkt(UnPubPkt trexUnPubPkt){
        this(trexUnPubPkt.getEventType(), trexUnPubPkt.getAttributes());
    }


    public UnPubPkt(int parEventType, Attribute[] parAttributes,
                    int parAttributesNum) {
        eventType = parEventType;
        int AttributesNum = parAttributesNum;
        for (Attribute i :
                parAttributes) {
            attributes.add(i);
        }
    }


    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    public void addAttribute(Attribute attr) {
        attributes.add(attr);
    }

    public String toString(){

        //// TODO: 1/30/2020
        String a= "";
        for (Attribute b :
                attributes) {
            a = a + (b.toString());
        }
        return "UnPublish Packet: {" +
                "Event Type: {" + this.eventType + "}" +
                ", Attributes: {" + a + "}" +
                "}";
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (! (obj instanceof SubPkt)) return false;
        UnPubPkt other = (UnPubPkt) obj;
        if (eventType != other.eventType) return false;
        if (! attributes.containsAll(other.attributes)) return false;
        if (! other.attributes.containsAll(attributes)) return false;
        return true;
    }

}
