package se.kth.id2203.mp;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.util.ArrayList;

/**
 * Created by Klas on 2017-02-27.
 */
public class MPBooted implements KompicsEvent {
    protected final ArrayList<NetAddress> partition;

    public MPBooted(ArrayList<NetAddress> partition) {
        this.partition = partition;
    }

    public ArrayList<NetAddress> getPartition() {
        return partition;
    }
}
