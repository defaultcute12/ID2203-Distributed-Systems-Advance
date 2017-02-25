package se.kth.id2203.riwm;

import se.sics.kompics.KompicsEvent;

/**
 * Created by Klas on 2017-02-25.
 */
public class ACK implements KompicsEvent {
    protected final int rid;

    public int getRid() {
        return rid;
    }

    public ACK(int rid) {

        this.rid = rid;
    }
}
