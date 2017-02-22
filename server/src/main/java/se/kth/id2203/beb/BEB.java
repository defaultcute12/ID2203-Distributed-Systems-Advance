package se.kth.id2203.beb;
/**
 * Created by Mallu on 20-02-2017.
 */

import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import java.util.Collection;

public class BEB extends ComponentDefinition {

    private Positive<Network> net = requires(Network.class);
    private Negative<BEBPort> beb = provides(BEBPort.class);

    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);

    private Handler<BEBRequest> broadcastHandler = new Handler<BEBRequest>() {
        @Override
        public void handle(BEBRequest event) {
            Collection<NetAddress> nodes = event.getNodes();
            for (NetAddress node : nodes) {
                System.out.println("Broadcasting from: " + self + " to " + node);
                trigger(new Message(self, node, event.getDeliver()), net);
            }
        }
    };

    private ClassMatchedHandler<BEBDeliver, Message> deliverHandler = new ClassMatchedHandler<BEBDeliver, Message>() {

        @Override
        public void handle(BEBDeliver content, Message context) {
            System.out.println("Delivering to: " + self);
            trigger(new Message(content.getSource(), self, content.getEvent()), net);
        }
    };

    {
        subscribe(broadcastHandler, beb);
        subscribe(deliverHandler, net);
    }
}