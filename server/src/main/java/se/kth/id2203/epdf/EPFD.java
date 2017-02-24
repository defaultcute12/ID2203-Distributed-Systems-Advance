package se.kth.id2203.epdf;

/**
 * Created by Mallu on 21-02-2017.
 */
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;
import sun.nio.ch.Net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class EPFD extends ComponentDefinition {

    protected ArrayList<NetAddress> partition;
    protected ArrayList<NetAddress> suspected = new ArrayList<>();
    protected ArrayList<NetAddress> alive = new ArrayList<>();
    protected final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    protected int period = 40;
    protected int delta = 10;
    protected int seqnum = 0;
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Negative<EPFDPort> epfd = provides(EPFDPort.class);

    private void startTimer(int period) {
        ScheduleTimeout st = new ScheduleTimeout(period);
        st.setTimeoutEvent(new HeartbeatTimeout(st));
        HeartbeatTimeout timeout = new HeartbeatTimeout(st);
        trigger(st, timer);
    }

    protected Handler<EPFDBooted> bootedHandler = new Handler<EPFDBooted>() {

        @Override
        public void handle(EPFDBooted event) {
            partition = event.getPartition();
            alive.addAll(partition);
            startTimer(period);
        }
    };

    protected Handler<HeartbeatTimeout> heartbeatTimeoutHandler = new Handler<HeartbeatTimeout>() {

        @Override
        public void handle(HeartbeatTimeout event) {

            ArrayList<NetAddress> intersect = new ArrayList<>(alive);
            intersect.retainAll(suspected);
            if(intersect.isEmpty()) {
                period += delta;
            }
            seqnum = seqnum + 1;

            for(NetAddress p : partition) {
                if(!alive.contains(p) && !suspected.contains(p)) {
                    suspected.add(p);
                    trigger(new Suspect(p), epfd);
                } else if(alive.contains(p) && suspected.contains(p)) {
                    suspected.remove(p);
                    trigger(new Restore(p), epfd);
                }
                trigger(new HeartbeatRequest(self, p, seqnum), net);
            }
            alive.clear();
            startTimer(period);
        }
    };

    protected Handler<HeartbeatRequest> heartbeatRequestHandler = new Handler<HeartbeatRequest>() {
        @Override
        public void handle(HeartbeatRequest event) {
            trigger(new HeartbeatReply(self, event.getSource(), event.getSeqnum()), net);
        }
    };

    protected Handler<HeartbeatReply> heartbeatReplyHandler = new Handler<HeartbeatReply>() {
        @Override
        public void handle(HeartbeatReply event) {
            if(event.getSeqnum() == seqnum || suspected.contains(event.getSource())) {
                alive.add(event.getSource());
            }
        }
    };


    public static class HeartbeatTimeout extends Timeout {
        public HeartbeatTimeout(ScheduleTimeout spt) {
            super(spt);
        }
    }

    {
        subscribe(bootedHandler, boot);
        subscribe(heartbeatTimeoutHandler, timer);
        subscribe(heartbeatRequestHandler, net);
        subscribe(heartbeatReplyHandler, net);
    }
}
