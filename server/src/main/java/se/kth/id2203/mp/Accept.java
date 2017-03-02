package se.kth.id2203.mp;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Klas on 2017-02-27.
 */
public class Accept implements KompicsEvent, Serializable {
    protected final int ts;
    protected final ArrayList<Object> vsuf;
    protected final int offs;
    protected final int t;

    public Accept(int ts, ArrayList<Object> vsuf, int offs, int t) {
        this.ts = ts;
        this.vsuf = vsuf;
        this.offs = offs;
        this.t = t;
    }

    public int getTs() {
        return ts;
    }

    public ArrayList<Object> getVsuf() {
        return vsuf;
    }

    public int getOffs() {
        return offs;
    }

    public int getT() {
        return t;
    }
}
