package se.kth.id2203.riwm;

import se.sics.kompics.KompicsEvent;

/**
 * Created by Klas on 2017-02-25.
 */
public class VALUE implements KompicsEvent {
    protected final int rid;
    protected final int ts;
    protected final int wr;
    protected final Integer value;

    public VALUE(int rid, int ts, int wr, Integer value) {
        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.value = value;
    }

    public int getRid() {
        return rid;
    }

    public int getTs() {
        return ts;
    }

    public int getWr() {
        return wr;
    }

    public Integer getValue() {
        return value;
    }
}
