package se.kth.id2203.beb;

/**
 * Created by Mallu on 20-02-2017.
 */
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.networking.NetMessage;
import se.sics.kompics.network.Transport;


import java.io.Serializable;

public class BebDataMessage extends NetMessage implements Serializable {

    private NetAddress source;
    private BebDeliver data;

    public BebDataMessage(NetAddress source, NetAddress destination, BebDeliver data) {
        super(source, destination, Transport.TCP);
        this.source = source;
        this.data = data;
    }

    public BebDeliver getData() {
        return data;
    }
}
