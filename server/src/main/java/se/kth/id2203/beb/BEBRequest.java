package se.kth.id2203.beb;

/**
 * Created by Mallu on 20-02-2017.
 */
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.util.Collection;

public class BEBRequest implements KompicsEvent {

    private final BEBDeliver deliverEvent;
    private final Collection<NetAddress> nodes;

    public BEBRequest(BEBDeliver deliverEvent, Collection<NetAddress> nodes) {
        this.deliverEvent = deliverEvent;
        this.nodes = nodes;
    }

    public BEBDeliver getDeliverEvent() {
        return deliverEvent;
    }

    public Collection<NetAddress> getBroadcastNodes() {
        return nodes;
    }
}
