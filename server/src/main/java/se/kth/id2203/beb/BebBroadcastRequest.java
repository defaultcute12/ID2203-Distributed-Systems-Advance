package se.kth.id2203.beb;

/**
 * Created by Mallu on 20-02-2017.
 */
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;
//import system.network.TAddress;

import java.util.ArrayList;

public class BebBroadcastRequest implements KompicsEvent {

    private final BebDeliver deliverEvent;
    private final ArrayList <NetAddress> nodes;

    public BebBroadcastRequest(BebDeliver deliverEvent, ArrayList <NetAddress> nodes) {
        this.deliverEvent = deliverEvent;
        this.nodes = nodes;
    }

    public BebDeliver getDeliverEvent() {
        return deliverEvent;
    }

    public ArrayList<NetAddress> getBroadcastNodes() {
        return nodes;
    }
}
