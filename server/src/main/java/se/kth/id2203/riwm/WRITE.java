package se.kth.id2203.riwm;

import se.sics.kompics.KompicsEvent;

/**
 * Created by Klas on 2017-02-25.
 */
public class WRITE implements KompicsEvent {
    protected final int rid;
    protected final int ts;
    protected final int wr;
    protected final Integer writeVal;

    public WRITE(int rid, int ts, int wr, Integer writeVal) {
        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.writeVal = writeVal;
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

    public Integer getWriteVal() {
        return writeVal;
    }
}
