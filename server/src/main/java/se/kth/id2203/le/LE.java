package se.kth.id2203.le;

import se.kth.id2203.epdf.EPFDPort;
import se.kth.id2203.epdf.Restore;
import se.kth.id2203.epdf.Suspect;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Klas on 2017-02-20.
 */
public class LE extends ComponentDefinition {

    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<EPFDPort> epfd = requires(EPFDPort.class);
    protected final Negative<LEPort> le = provides(LEPort.class);

    protected final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    protected final ArrayList<NetAddress> partition;

    public LE(Init init) {
        this.partition = init.partition;
    }

    protected final Handler<Restore> restoreHandler = new Handler<Restore>() {

        @Override
        public void handle(Restore event) {

        }
    };
    protected final Handler<Suspect> suspectHandler = new Handler<Suspect>() {

        @Override
        public void handle(Suspect event) {

        }
    };

    public static class Init extends se.sics.kompics.Init<LE> {

        private final ArrayList<NetAddress> partition;

        public Init(NetAddress self, ArrayList<NetAddress> partition) {
            this.partition = partition;
        }
    }
}
