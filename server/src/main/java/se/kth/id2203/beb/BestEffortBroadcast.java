package se.kth.id2203.beb;

/**
 * Created by Mallu on 20-02-2017.
 */
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import java.util.ArrayList;

public class BestEffortBroadcast extends ComponentDefinition {

    private Positive<Network> net = requires(Network.class);
    private Negative<BestEffortBroadcastPort> beb = provides(BestEffortBroadcastPort.class);

    private NetAddress self;

    public BestEffortBroadcast(Init init) {
        this.self = init.self;
        subscribe(startHandler, control);
        subscribe(broadcastHandler, beb);
        subscribe(deliverHandler, net);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {

        }
    };

    //Perform broadcast using Perfect Links
    private Handler<BebBroadcastRequest> broadcastHandler = new Handler<BebBroadcastRequest>() {
        @Override
        public void handle(BebBroadcastRequest event) {
            ArrayList <NetAddress> nodes = event.getBroadcastNodes();
            for (NetAddress node : nodes) {
                printBroadcast(event.getDeliverEvent(), node);
                BebDataMessage msg = new BebDataMessage(self,node, event.getDeliverEvent());
                trigger(msg, net);
            }
        }
    };

    private void printBroadcast(BebDeliver deliver, NetAddress node) {
        Message command = (Message) deliver.getData();
        if (command instanceof GETRequest) {
            GETRequest get = (GETRequest) command;
            System.out.println(self + " Sending Best-effort Broadcast " + get + " to " + node);
        }
        else if (command instanceof PUTRequest) {
            PUTRequest put = (PUTRequest) command;
            System.out.println(self + " Sending Best-effort Broadcast " + put + " to " + node);
        }
        else if (command instanceof CASRequest) {
            CASRequest cas = (CASRequest) command;
            System.out.println(self + " Sending Best-effort Broadcast " + cas + " to " + node);
        }
    }

    private void printDeliver(BebDeliver deliver, NetAddress node) {
        Message command = (Message) deliver.getData();
        if (command instanceof GETRequest) {
            GETRequest get = (GETRequest) command;
            System.out.println(self + " Received Best-effort Deliver " + get + " from " + node);
        }
        else if (command instanceof PUTRequest) {
            PUTRequest put = (PUTRequest) command;
            System.out.println(self + " Received Best-effort Deliver " + put + " from " + node);
        }
        else if (command instanceof CASRequest) {
            CASRequest cas = (CASRequest) command;
            System.out.println(self + " Received Best-effort Deliver " + cas + " from " + node);
        }
    }

    //Deliver to application
    private Handler<BebDataMessage> deliverHandler = new Handler<BebDataMessage>() {
        @Override
        public void handle(BebDataMessage event) {
            printDeliver(event.getData(), event.getSource());
            trigger(event.getData(), beb);
        }
    };

    public static class Init extends se.sics.kompics.Init<BestEffortBroadcast> {
        public NetAddress self;

        public Init(NetAddress self) {
            this.self = self;
        }
    }
}
