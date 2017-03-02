package se.kth.id2203.mp;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by Klas on 2017-02-27.
 */
public class AcceptAck implements KompicsEvent, Serializable {
    protected final int pts;
    protected final int l;
    protected final int t;

    public AcceptAck(int pts, int l, int t) {
        this.pts = pts;
        this.l = l;
        this.t = t;
    }

    public int getPts() {
        return pts;
    }

    public int getL() {
        return l;
    }

    public int getT() {
        return t;
    }
}
