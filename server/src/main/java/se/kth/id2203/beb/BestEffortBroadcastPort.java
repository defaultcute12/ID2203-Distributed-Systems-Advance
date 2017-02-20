package se.kth.id2203.beb;

/**
 * Created by Mallu on 20-02-2017.
 */
import se.sics.kompics.PortType;

public class BestEffortBroadcastPort extends PortType {
    {
        indication(BebDeliver.class);
        request(BebBroadcastRequest.class);
    }
}
