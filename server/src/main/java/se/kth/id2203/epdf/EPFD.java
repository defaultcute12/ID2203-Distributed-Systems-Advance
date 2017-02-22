package se.kth.id2203.epdf;

/**
 * Created by Mallu on 21-02-2017.
 */
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

    private final ArrayList<NetAddress> topology;
    private final ArrayList<NetAddress> suspected;
    private final ArrayList<NetAddress> alive;
    private final NetAddress self;
    private int period = 40;
    private int delta = 10;
    private int seqnum = 0;
    Positive<Network> net = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    Negative<EPFDPort> epfd = provides(EPFDPort.class);

    public EPFD(Init init) {
        this.topology = init.topology;
        suspected = new ArrayList<>();
        alive = new ArrayList<>();
        this.self = init.self;
    }

    private void startTimer(int period) {
        ScheduleTimeout st = new ScheduleTimeout(period);
        st.setTimeoutEvent(new HeartbeatTimeout(st));
        HeartbeatTimeout timeout = new HeartbeatTimeout(st);
        trigger(st, timer);
    }

    Handler<Start> startHandler = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            alive.addAll(topology);
            startTimer(period);
        }
    };

    Handler<HeartbeatTimeout> heartbeatTimeoutHandler = new Handler<HeartbeatTimeout>() {

        @Override
        public void handle(HeartbeatTimeout event) {

            ArrayList<NetAddress> intersect = new ArrayList<>(alive);
            intersect.retainAll(suspected);
            if(intersect.isEmpty()) {
                period += delta;
            }
            seqnum = seqnum + 1;

            for(NetAddress p : topology) {
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

    Handler<HeartbeatRequest> heartbeatRequestHandler = new Handler<HeartbeatRequest>() {
        @Override
        public void handle(HeartbeatRequest event) {
            trigger(new HeartbeatReply(self, event.getSource(), event.getSeqnum()), net);
        }
    };

    Handler<HeartbeatReply> heartbeatReplyHandler = new Handler<HeartbeatReply>() {
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

    public static class Init extends se.sics.kompics.Init<EPFD> {
        public final NetAddress self;
        public final ArrayList<NetAddress> topology;

        public Init(NetAddress self, ArrayList<NetAddress> topology) {
            this.self = self;
            this.topology = topology;
        }
    }

    {
        subscribe(startHandler, control);
        subscribe(heartbeatTimeoutHandler, timer);
        subscribe(heartbeatRequestHandler, net);
        subscribe(heartbeatReplyHandler, net);
    }
}
