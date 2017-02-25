package se.kth.id2203.riwm;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Klas on 2017-02-25.
 */
public class RIWMBooted implements KompicsEvent {
    protected final Collection<NetAddress> partition;

    public RIWMBooted(Collection<NetAddress> partition) {
        this.partition = partition;
    }

    public Collection<NetAddress> getPartition() {
        return partition;
    }
}
