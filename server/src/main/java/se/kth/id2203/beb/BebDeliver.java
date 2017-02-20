package se.kth.id2203.beb;

/**
 * Created by Mallu on 20-02-2017.
 */
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;


public class BebDeliver implements KompicsEvent {

    private static final long serialVersionUID = 4088333329204792579L;

    private NetAddress source;
    private Object data;

    public BebDeliver(NetAddress source, Object data) {
        this.source = source;
        this.data = data;
    }

    public NetAddress getSource() {
        return source;
    }

    public Object getData() {
        return data;
    }
}
