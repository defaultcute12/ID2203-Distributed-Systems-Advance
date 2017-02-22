package se.kth.id2203.beb;

/**
 * Created by Mallu on 20-02-2017.
 */
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;


public class BEBDeliver implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 4088333329204792579L;
    private NetAddress src;
    private KompicsEvent event;

    public BEBDeliver(NetAddress src, KompicsEvent event) {
        this.src = src;
        this.event = event;
    }

    public NetAddress getSource() {
        return src;
    }

    public KompicsEvent getEvent() {
        return event;
    }
}
