package se.kth.id2203.mp;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by Klas on 2017-03-01.
 */
public class AscDecide implements KompicsEvent, Serializable {
    protected Object o;

    public AscDecide(Object o) {
        this.o = o;
    }

    public Object getO() {
        return o;
    }
}
