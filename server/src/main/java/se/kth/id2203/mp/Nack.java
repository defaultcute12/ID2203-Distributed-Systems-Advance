package se.kth.id2203.mp;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by Klas on 2017-02-27.
 */
public class Nack implements KompicsEvent, Serializable {
    protected final int pts;
    protected final int t;

    public Nack(int pts, int t) {
        this.pts = pts;
        this.t = t;
    }

    public int getPts() {
        return pts;
    }

    public int getT() {
        return t;
    }
}
