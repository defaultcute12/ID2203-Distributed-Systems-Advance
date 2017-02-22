package se.kth.id2203.beb;

/**
 * Created by Mallu on 20-02-2017.
 */
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.util.Collection;

public class BEBRequest implements KompicsEvent {

    private final BEBDeliver deliver;
    private final Collection<NetAddress> nodes;

    public BEBRequest(BEBDeliver deliver, Collection<NetAddress> nodes) {
        this.deliver = deliver;
        this.nodes = nodes;
    }

    public BEBDeliver getDeliver() {
        return deliver;
    }

    public Collection<NetAddress> getNodes() {
        return nodes;
    }
}