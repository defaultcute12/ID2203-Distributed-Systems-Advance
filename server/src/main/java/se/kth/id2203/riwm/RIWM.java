package se.kth.id2203.riwm;

import se.kth.id2203.beb.BEBDeliver;
import se.kth.id2203.beb.BEBPort;
import se.kth.id2203.beb.BEBRequest;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.networking.NetAddressConverter;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import javax.xml.ws.Holder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Klas on 2017-02-25.
 */
public class RIWM extends ComponentDefinition {
    /* Ports */
    protected final Negative<AtomicRegister> nnar = provides(AtomicRegister.class);
    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<BEBPort> beb = requires(BEBPort.class);

    /* Fields */
    protected final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    protected int n = -1;
    protected final int selfRank = self.getPort();
    protected Collection<NetAddress> partition = null;

    protected int ts = 0;
    protected int wr = 0;
    protected Integer value = null;
    protected int acks = 0;
    protected Integer readval = null;
    protected Integer writeval = null;
    protected int rid = 0;
    protected class Tup {
        int ts;
        int wr;
        Integer value;
        /* TODO: Check on later */
        public int compareTo(Tup that) {
            if(that.ts == this.ts && that.wr == this.wr && that.value.compareTo(this.value) == 0) {
                return 0;
            } else if(that.ts > this.ts && that.wr > this.ts && that.value.compareTo(this.value) == 1) {
                return -1;
            } else {
                return 1;
            }
        }
    }
    protected HashMap<NetAddress, Tup> readlist = new HashMap<>();
    protected boolean reading = false;

    /* Handlers */
    /* boot */
    protected final Handler<RIWMBooted> bootedHandler = new Handler<RIWMBooted>() {

        @Override
        public void handle(RIWMBooted event) {
            partition = event.getPartition();
            n = event.getPartition().size();
        }
    };
    /* nnar */
    protected final Handler<ReadRequest> readRequestHandler = new Handler<ReadRequest>() {

        @Override
        public void handle(ReadRequest event) {
            rid = rid + 1;
            acks = 0;
            readlist.clear();
            reading = true;
            trigger(new BEBRequest(new BEBDeliver(self, new READ(rid)), partition), beb);
        }
    };
    protected final Handler<WriteRequest> writeRequestHandler = new Handler<WriteRequest>() {

        @Override
        public void handle(WriteRequest event) {
            rid = rid + 1;
            writeval = event.getValue();
            acks = 0;
            readlist.clear();
            trigger(new BEBRequest(new BEBDeliver(self, new READ(rid)), partition), beb);
        }
    };
    /* beb */
    protected final ClassMatchedHandler<READ, BEBDeliver> readHandler = new ClassMatchedHandler<READ, BEBDeliver>() {

        @Override
        public void handle(READ content, BEBDeliver context) {
            trigger(new Message(self, context.getSource(), new VALUE(content.rid, ts, wr, value)), net);
        }
    };
    protected final ClassMatchedHandler<WRITE, BEBDeliver> writeHandler = new ClassMatchedHandler<WRITE, BEBDeliver>() {

        @Override
        public void handle(WRITE content, BEBDeliver context) {
            if(content.getTs() > ts && content.getWr() > wr) {
                ts = content.getTs();
                wr = content.getWr();
                value = content.getWriteVal();
            }
            trigger(new Message(self, context.getSource(), new ACK(content.getRid())), net);
        }
    };
    /* net */
    protected final ClassMatchedHandler<VALUE, Message> valueHandler = new ClassMatchedHandler<VALUE, Message>() {

        @Override
        public void handle(VALUE content, Message context) {
            if(content.getRid() == rid) {
                readlist.get(context.getSource()).ts = content.ts;
                readlist.get(context.getSource()).wr = content.wr;
                readlist.get(context.getSource()).value = content.value;

                if(readlist.size() > n/2) {
                    Map.Entry<NetAddress, Tup> maxEntry = null;
                    for(Map.Entry<NetAddress, Tup> entry : readlist.entrySet()) {
                        if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                            maxEntry = entry;
                        }
                    }

                    int maxts = maxEntry.getValue().ts;
                    int rr = maxEntry.getValue().wr;
                    Integer readval = maxEntry.getValue().value;

                    readlist.clear();
                    int bcastval = 0;
                    if(reading) {
                        bcastval = readval;
                    } else {
                        rr = selfRank;
                        maxts = maxts + 1;
                        bcastval = writeval;
                    }
                    trigger(new BEBRequest(new BEBDeliver(self, new WRITE(rid, maxts, rr, bcastval)), partition), beb);
                }
            }
        }
    };
    protected final ClassMatchedHandler<ACK, Message> ackHandler = new ClassMatchedHandler<ACK, Message>() {

        @Override
        public void handle(ACK content, Message context) {
            if(content.getRid() == rid) {
                acks = acks + 1;
                if(acks > n/2) {
                    acks = 0;
                    if(reading) {
                        reading = false;
                        trigger(new ReadResponse(readval), net);
                    } else {
                        trigger(new WriteResponse(), net);
                    }
                }
            }
        }
    };

    {
        subscribe(bootedHandler, boot);
        subscribe(readRequestHandler, nnar);
        subscribe(writeRequestHandler, nnar);
        subscribe(readHandler, beb);
        subscribe(writeHandler, beb);
        subscribe(valueHandler, net);
        subscribe(ackHandler, net);
    }
}