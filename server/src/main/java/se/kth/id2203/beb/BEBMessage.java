package se.kth.id2203.beb;

/**
 * Created by Mallu on 20-02-2017.
 */
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.networking.NetMessage;
import se.sics.kompics.network.Transport;


import java.io.Serializable;

public class BEBMessage extends NetMessage implements Serializable {

    private static final long serialVersionUID = -5669233156267202337L;

    private NetAddress source;
    private BEBDeliver data;

    public BEBMessage(NetAddress source, NetAddress destination, BEBDeliver data) {
        super(source, destination, Transport.TCP);
        this.data = data;
    }

    public BEBDeliver getData() {
        return data;
    }
}