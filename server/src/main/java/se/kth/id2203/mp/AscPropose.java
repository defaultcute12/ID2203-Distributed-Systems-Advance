package se.kth.id2203.mp;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Klas on 2017-02-27.
 */
public class AscPropose implements KompicsEvent, Serializable {
    protected final Object v;

    public AscPropose(Object v) {
        this.v = v;
    }
    public Object getV() {
        return v;
    }
}
