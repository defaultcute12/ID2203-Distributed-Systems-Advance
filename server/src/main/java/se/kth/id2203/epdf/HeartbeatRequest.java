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
    final protected int seqnum;
    public HeartbeatRequest(NetAddress src, NetAddress dst, int seqnum) {
        super(src, dst, Transport.TCP);
        this.seqnum = seqnum;
    }
    public int getSeqnum() {
        return seqnum;
    }
}
