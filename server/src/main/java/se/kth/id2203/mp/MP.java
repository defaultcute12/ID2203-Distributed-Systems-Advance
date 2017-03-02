package se.kth.id2203.mp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Klas on 2017-02-27.
 */
public class MP extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(MP.class);
    /**** Ports ****/
    protected final Negative<Consensus> asc = provides(Consensus.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);

    /**** Fields ****/
    protected final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    protected int selfRank = -1;
    protected int N = -1;
    protected ArrayList<NetAddress> partition = null;

    protected int t = 0;
    protected int prepts = 0;

    public class Tup {
        protected int ts = 0;
        protected ArrayList<Object> v = new ArrayList<>();
        protected int l = 0;
    }
    public class Pair {
        protected int ts;
        protected ArrayList<Object> vsuf;
        public Pair(int ts, ArrayList<Object> vsuf) {
            this.ts = ts;
            this.vsuf = vsuf;
        }
    }

    /* (ats,av,al) */
    Tup acceptor = new Tup();
    Tup proposer = new Tup();

    /* Defined in boot */
    protected ArrayList<Object> proposedValues = new ArrayList<>();
    protected HashMap<NetAddress, Pair> readlist = new HashMap<>();
    protected HashMap<NetAddress, Integer> accepted = new HashMap<>();
    protected HashMap<NetAddress, Integer> decided = new HashMap<>();

    protected ArrayList<Object> prefix(ArrayList<Object> v, int l) {
        ArrayList<Object> vPrefix = new ArrayList<>();
        for(int i = 0; i < l; i++) {
            vPrefix.add(v.get(i));
        }
        return vPrefix;
    }
    protected ArrayList<Object> suffix(ArrayList<Object> v, int l) {
        ArrayList<Object> vSuffix = new ArrayList<>();
        for(int i = l; i < v.size(); i++) {
            vSuffix.add(v.get(i));
        }
        return vSuffix;
    }

    /**** Handlers ****/
    protected final Handler<MPBooted> bootedHandler = new Handler<MPBooted>() {
        @Override
        public void handle(MPBooted event) {
            LOG.debug("Handler<MPBooted>");
            partition = event.getPartition();
            selfRank = partition.indexOf(self);
            N = partition.size();
        }
    };

    protected final Handler<AscPropose> proposeHandler = new Handler<AscPropose>() {
        @Override
        public void handle(AscPropose event) {
            t = t + 1;
            if(proposer.ts == 0) {
                proposer.ts = t * N + selfRank;
                proposer.v = prefix(acceptor.v, acceptor.l);
                proposer.l = 0;
                proposedValues.clear();
                proposedValues.add(event.getV());
                readlist.clear();
                accepted.clear();
                decided.clear();
                for(NetAddress p : partition) {
                    trigger(new Message(self, p, new Prepare(proposer.ts, acceptor.l, t)), net);
                }
            } else if(readlist.size() <= N/2) {
                proposedValues.add(event.getV());
            } else if(!proposer.v.contains(event.getV())) {
                proposer.v.add(event.getV());
                for(NetAddress p : partition) {
                    if(readlist.containsKey(p)) {
                        ArrayList<Object> v = new ArrayList<>();
                        v.add(event.getV());
                        trigger(new Message(self, p, new Accept(proposer.ts, v, proposer.v.size() - 1, t)), net);
                    }
                }
            }
        }
    };
    protected final ClassMatchedHandler<Prepare, Message> prepareHandler = new ClassMatchedHandler<Prepare, Message>() {
        @Override
        public void handle(Prepare content, Message context) {
            t = Math.max(t, content.getT()) + 1;
            if(content.getTs() < prepts) {
                trigger(new Message(self, context.getSource(), new Nack(content.getTs(), t)), net);
            } else {
                prepts = content.getTs();
                trigger(new Message(self, context.getSource(), new PrepareAck(content.getTs(), acceptor.ts, suffix(acceptor.v, content.getL()), acceptor.l, t)), net);
            }
        }
    };
    protected final ClassMatchedHandler<Nack, Message> nackHandler = new ClassMatchedHandler<Nack, Message>() {

        @Override
        public void handle(Nack content, Message context) {
            t = Math.max(t, content.getT()) + 1;
            if(content.getPts() == proposer.ts) {
                proposer.ts = 0;
                trigger(new AscAbort(), asc);
            }
        }
    };
    protected final ClassMatchedHandler<PrepareAck, Message> prepareAckHandler = new ClassMatchedHandler<PrepareAck, Message>() {

        @Override
        public void handle(PrepareAck content, Message context) {
            t = Math.max(t, content.getT()) + 1;
            if(content.getPts() == proposer.ts) {
                readlist.put(context.getSource(), new Pair(content.getTs(), content.getVsuf()));
                decided.put(context.getSource(), content.getL());
                if(readlist.size() == N/2 + 1) {
                    Pair p1 = new Pair(0, new ArrayList<>());
                    for(Pair p2 : readlist.values()) {
                        if(p1.ts < p2.ts || (p1.ts == p2.ts && p1.vsuf.size() < p2.vsuf.size())) {
                            p1.ts = p2.ts;
                            p1.vsuf = p2.vsuf;
                        }
                    }
                    proposer.v.addAll(content.getVsuf());
                    for(Object v : proposedValues) {
                        if(!proposer.v.contains(v)) {
                            proposer.v.add(v);
                        }
                    }
                    for(NetAddress p : partition) {
                        if(readlist.containsKey(p)) {
                            int l1 = decided.get(p);
                            trigger(new Message(self, p, new Accept(proposer.ts, suffix(proposer.v, l1), l1, t)), net);
                        }
                    }
                } else if(readlist.size() > N/2 + 1) {
                    trigger(new Message(self, context.getSource(), new Accept(proposer.ts, suffix(proposer.v, content.l), content.l, t)), net);
                    if(proposer.l != 0) {
                        trigger(new Message(self, context.getSource(), new Decide(proposer.ts, proposer.l, t)), net);
                    }
                }
            }
        }
    };
    protected final ClassMatchedHandler<Accept, Message> acceptHandler = new ClassMatchedHandler<Accept, Message>() {

        @Override
        public void handle(Accept content, Message context) {
            t = Math.max(t, content.getT()) + 1;
            if(content.getTs() != prepts) {
                trigger(new Message(self, context.getSource(), new Nack(content.getTs(), t)), net);
            } else {
                int ats = content.getTs();
                if(content.getOffs() < acceptor.v.size()) {
                    acceptor.v = prefix(acceptor.v, content.getOffs());
                }
                acceptor.v.addAll(content.getVsuf());
                trigger(new Message(self, context.getSource(), new AcceptAck(content.getTs(), acceptor.v.size(), t)), net);
            }
        }
    };
    protected final ClassMatchedHandler<AcceptAck, Message> acceptAckHandler = new ClassMatchedHandler<AcceptAck, Message>() {

        @Override
        public void handle(AcceptAck content, Message context) {
            t = Math.max(t, content.getT()) + 1;
            if(content.getPts() == proposer.ts) {
                accepted.put(context.getSource(), content.getL());
                int num_greater = 0;

                for(NetAddress p : partition) {
                    LOG.debug("accepted: {} p: {} content.getL(): {}", accepted, p, content);
                    if(accepted.get(p) != null && accepted.get(p) >= content.getL()) {
                        num_greater++;
                    }
                }
                if(proposer.l < content.getL() && num_greater > N / 2) {
                    proposer.l = content.getL();
                    for(NetAddress p : partition) {
                        if(readlist.containsKey(p)) {
                            trigger(new Message(self, p, new Decide(proposer.ts, proposer.l, t)), net);
                        }
                    }
                }
            }
        }
    };
    protected final ClassMatchedHandler<Decide, Message> decideHandler = new ClassMatchedHandler<Decide, Message>() {

        @Override
        public void handle(Decide content, Message context) {
            t = Math.max(t, content.getT()) + 1;
            if(content.getTs() == prepts) {
                while(acceptor.l < content.getL()) {
                    trigger(new AscDecide(acceptor.v.get(acceptor.l)), asc);
                    acceptor.l = acceptor.l + 1;
                }
            }
        }
    };

    {
        subscribe(bootedHandler, boot);
        subscribe(proposeHandler, asc);
        subscribe(prepareHandler, net);
        subscribe(nackHandler, net);
        subscribe(prepareAckHandler, net);
        subscribe(acceptHandler, net);
        subscribe(acceptAckHandler, net);
        subscribe(decideHandler, net);
    }
}