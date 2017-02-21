package se.kth.id2203.beb;
/**
 * Created by Mallu on 20-02-2017.
 */
import se.kth.id2203.beb.BEBPort;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import java.util.Collection;

public class BEB extends ComponentDefinition {

    private Positive<Network> net = requires(Network.class);
    private Negative<BEBPort> beb = provides(BEBPort.class);

    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);


    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {

        }
    };

    //Perform broadcast using Perfect Links
    private Handler<BEBRequest> broadcastHandler = new Handler<BEBRequest>() {
        @Override
        public void handle(BEBRequest event) {
            Collection<NetAddress> nodes = event.getBroadcastNodes();
            for (NetAddress node : nodes) {
                printBroadcast(event.getDeliverEvent(), node);
                BEBMessage msg = new BEBMessage(self, node, event.getDeliverEvent());
                trigger(msg, net);
            }
        }
    };

    private void printBroadcast(BEBDeliver deliver, NetAddress node) {
        System.out.println("Sending broadcast");
    }

    private void printDeliver(BEBDeliver deliver, NetAddress node) {
        System.out.println("Delivered broadcast");
    }

    //Deliver to application
    private Handler<BEBMessage> deliverHandler = new Handler<BEBMessage>() {
        @Override
        public void handle(BEBMessage event) {
            printDeliver(event.getData(), event.getSource());
            trigger(event.getData(), beb);
        }
    };

    {
        subscribe(startHandler, control);
        subscribe(broadcastHandler, beb);
        subscribe(deliverHandler, net);
    }
}