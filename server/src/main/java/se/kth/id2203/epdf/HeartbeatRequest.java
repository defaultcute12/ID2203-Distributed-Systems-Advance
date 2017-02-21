package se.kth.id2203.epdf;

/**
 * Created by Mallu on 21-02-2017.
 */
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.networking.NetHeader;
import se.kth.id2203.networking.NetMessage;
import se.sics.kompics.network.Transport;
import java.io.Serializable;

public class HeartbeatRequest extends NetMessage implements Serializable {
    public HeartbeatRequest(NetAddress src, NetAddress dst) {
        super(src, dst, Transport.TCP);
    }

    public HeartbeatRequest(NetAddress src, NetAddress dst, Transport proto) {
        super(src,dst,proto);
    }
}
