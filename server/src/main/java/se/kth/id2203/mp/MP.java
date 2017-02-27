package se.kth.id2203.mp;

import se.kth.id2203.beb.BEBPort;
import se.kth.id2203.networking.Message;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.ArrayList;

/**
 * Created by Klas on 2017-02-27.
 */
public class MP extends ComponentDefinition {
    /**** Ports ****/
    protected final Negative<Consensus> c = provides(Consensus.class);
    protected final Positive<BEBPort> beb = requires(BEBPort.class);
    protected final Positive<Network> net = requires(Network.class);

    /**** Fields ****/
    protected int ts = 0;
    protected int prepts = 0;

    /* (ats,av,al) */
    protected int ats = 0;
    protected Integer av = null;
    protected int al = 0;

    /* (pts,pv,pl) */
    protected int pts = 0;
    protected Integer pv = null;
    protected int pl = 0;

    protected ArrayList<Integer> proposedValues = null;
    protected ArrayList<Integer> readlist = null;
    protected ArrayList<Integer> accepted = null;
    protected ArrayList<Integer> decided = null;


    /**** Handlers ****/
    protected final Handler<MPBooted> bootedHandler = new Handler<MPBooted>() {
        @Override
        public void handle(MPBooted event) {

        }
    };
    protected final Handler<Propose> proposeHandler = new Handler<Propose>() {
        @Override
        public void handle(Propose event) {

        }
    };
    protected final ClassMatchedHandler<Prepare, Message> connectHandler = new ClassMatchedHandler<Prepare, Message>() {

        @Override
        public void handle(Prepare content, Message context) {

        }
    };
    protected final ClassMatchedHandler<Nack, Message> nackHandler = new ClassMatchedHandler<Nack, Message>() {

        @Override
        public void handle(Nack content, Message context) {

        }
    };
    protected final ClassMatchedHandler<PrepareAck, Message> prepareAckHandler = new ClassMatchedHandler<PrepareAck, Message>() {

        @Override
        public void handle(PrepareAck content, Message context) {

        }
    };
    protected final ClassMatchedHandler<Accept, Message> acceptHandler = new ClassMatchedHandler<Accept, Message>() {

        @Override
        public void handle(Accept content, Message context) {

        }
    };
    protected final ClassMatchedHandler<AcceptAck, Message> acceptAckHandler = new ClassMatchedHandler<AcceptAck, Message>() {

        @Override
        public void handle(AcceptAck content, Message context) {

        }
    };
    protected final ClassMatchedHandler<Decide, Message> decideHandler = new ClassMatchedHandler<Decide, Message>() {

        @Override
        public void handle(Decide content, Message context) {

        }
    };
}