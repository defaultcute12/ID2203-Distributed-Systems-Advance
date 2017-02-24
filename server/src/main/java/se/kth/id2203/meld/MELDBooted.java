package se.kth.id2203.meld;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Klas on 2017-02-24.
 */
public class MELDBooted implements KompicsEvent, Serializable {
    protected final ArrayList<NetAddress> partition;
    public MELDBooted(ArrayList<NetAddress> partition) {
        this.partition = partition;
    }

    public ArrayList<NetAddress> getPartition() {
        return partition;
    }
}
