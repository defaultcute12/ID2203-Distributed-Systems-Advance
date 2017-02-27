package se.kth.id2203.meld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.epdf.EPFDPort;
import se.kth.id2203.epdf.Restore;
import se.kth.id2203.epdf.Suspect;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;

import java.util.ArrayList;

/**
 * Created by Klas on 2017-02-20.
 */
public class MELD extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(MELD.class);

    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Positive<EPFDPort> epfd = requires(EPFDPort.class);
    protected final Negative<MELDPort> meld = provides(MELDPort.class);

    protected final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    protected ArrayList<NetAddress> partition;

    protected ArrayList<NetAddress> suspected = new ArrayList<>();
    protected NetAddress leader = null;

    protected final Handler<MELDBooted> bootHandler = new Handler<MELDBooted>() {

        @Override
        public void handle(MELDBooted event) {
            partition = event.getPartition();
            leader = self;
            for(NetAddress node : partition) {
                if(!suspected.contains(node)) {
                    if(node.compareTo(leader) > 0) {
                        leader = node;
                    }
                }
            }
            trigger(new Elect(leader), meld);
        }
    };
    protected final Handler<Suspect> suspectHandler = new Handler<Suspect>() {

        @Override
        public void handle(Suspect event) {
            suspected.add(event.getNode());
            if(event.getNode().equals(leader)) {
                leader = self;
                for(NetAddress node : partition) {
                    if(!suspected.contains(node)) {
                        if(node.compareTo(leader) > 0) {
                            leader = node;
                        }
                    }
                }
                trigger(new Elect(leader), meld);
            }
        }
    };
    protected final Handler<Restore> restoreHandler = new Handler<Restore>() {

        @Override
        public void handle(Restore event) {
            suspected.remove(event.getNode());
            if(event.getNode().compareTo(leader) > 0) {
                leader = event.getNode();
                trigger(new Elect(leader), meld);
            }
        }
    };

    {
        subscribe(bootHandler, boot);
        subscribe(suspectHandler, epfd);
        subscribe(restoreHandler, epfd);
    }
}
