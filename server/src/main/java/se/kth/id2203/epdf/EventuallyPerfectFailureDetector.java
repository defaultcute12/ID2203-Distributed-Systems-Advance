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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class EventuallyPerfectFailureDetector extends ComponentDefinition {

    private final ArrayList<NetAddress> neighbours;
    private final ArrayList<NetAddress> suspectedNodes;
    private final ArrayList<NetAddress> aliveNodes;
    private final NetAddress self;
    private UUID timerId;
    private int heartbeatDelay = 40;
    private int heartbeatDelayIncrease = 10;
    Positive<Network> net = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    Negative<FdPort> epfd = provides(FdPort.class);

    public EventuallyPerfectFailureDetector(Init init) {
        this.neighbours = init.neighbours;
        suspectedNodes = new ArrayList<>();
        aliveNodes = new ArrayList<>();
        this.self = init.self;

        subscribe(startHandler, control);
        subscribe(heartbeatTimeoutHandler, timer);
        subscribe(heartbeatRequestHandler, net);
        subscribe(heartbeatReplyHandler, net);
    }

    Handler<Start> startHandler = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            aliveNodes.addAll(neighbours);
            startTimer(heartbeatDelay);
        }
    };


    Handler<HeartbeatTimeout> heartbeatTimeoutHandler = new Handler<HeartbeatTimeout>() {

        @Override
        public void handle(HeartbeatTimeout heartbeatTimeout) {

            ArrayList<NetAddress> intersection = new ArrayList<>(aliveNodes);
            intersection.retainAll(suspectedNodes);
            //If we found an alive node that is also suspected, we know need to wait longer for heartbeat reply
            //This will prevent us from suspecting nodes that are alive simply because they did not reply to heartbeat
            if (!intersection.isEmpty()) {
                heartbeatDelay += heartbeatDelayIncrease;
            }

            Iterator it = neighbours.iterator();
            while(it.hasNext()) {
                NetAddress neighbour = (NetAddress) it.next();
                //If we found a node that has not replied to last heartbeat and is not suspected
                //We suspect it may have crashed and add it to the suspected list
                if (!aliveNodes.contains(neighbour) && !suspectedNodes.contains(neighbour)) {
                    suspectedNodes.add(neighbour);
                    //Here we trigger program logic for detection of suspected node
                    System.out.println(self  + ": Suspect: " + neighbour.toString());
                    trigger(new Suspect(neighbour), epfd);
                }
                //If we found a node that replied to heartbeat and is also suspected we consider it alive
                else if (aliveNodes.contains(neighbour) && suspectedNodes.contains(neighbour)) {
                    suspectedNodes.remove(neighbour);
                    //Here we trigger program logic for restore of suspected node
                    System.out.println(self  + ": Restore: " + neighbour.toString());
                    trigger(new Restore(neighbour), epfd);
                }
                //Send a new heartbeat
                trigger(new HeartbeatRequest(self, neighbour), net);
            }
            aliveNodes.clear();
            //Subscribe to new timeout
            startTimer(heartbeatDelay);
        }
    };

    private void startTimer(int delay) {
        ScheduleTimeout st = new ScheduleTimeout(delay);
        HeartbeatTimeout timeout = new HeartbeatTimeout(st);
        st.setTimeoutEvent(timeout);
        trigger(st, timer);
        timerId = timeout.getTimeoutId();
    }

    Handler<HeartbeatReply> heartbeatReplyHandler = new Handler<HeartbeatReply>() {
        @Override
        public void handle(HeartbeatReply heartbeatTimeout) {
            aliveNodes.add(heartbeatTimeout.getSource());
        }
    };

    Handler<HeartbeatRequest> heartbeatRequestHandler = new Handler<HeartbeatRequest>() {
        @Override
        public void handle(HeartbeatRequest heartbeatTimeout) {
            trigger(new HeartbeatReply(self, heartbeatTimeout.getSource()), net);
        }
    };

    public static class HeartbeatTimeout extends Timeout {
        public HeartbeatTimeout(ScheduleTimeout spt) {
            super(spt);
        }
    }

    public static class Init extends se.sics.kompics.Init<EventuallyPerfectFailureDetector> {
        public final NetAddress self;
        public final ArrayList<NetAddress> neighbours;

        public Init(NetAddress self, ArrayList<NetAddress> neighbours) {
            this.self = self;
            this.neighbours = neighbours;
        }
    }
}
