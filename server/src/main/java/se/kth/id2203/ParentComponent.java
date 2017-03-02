
package se.kth.id2203;

import com.google.common.base.Optional;
import se.kth.id2203.beb.BEB;
import se.kth.id2203.beb.BEBPort;
import se.kth.id2203.bootstrapping.BootstrapClient;
import se.kth.id2203.bootstrapping.BootstrapServer;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.epdf.EPFD;
import se.kth.id2203.epdf.EPFDPort;
import se.kth.id2203.kvstore.KVPort;
import se.kth.id2203.kvstore.KVService;
import se.kth.id2203.meld.MELD;
import se.kth.id2203.meld.MELDPort;
import se.kth.id2203.mp.Consensus;
import se.kth.id2203.mp.MP;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.VSOverlayManager;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

public class ParentComponent extends ComponentDefinition {

    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    protected final Positive<BEBPort> bebPort = requires(BEBPort.class);
    protected final Positive<KVPort> kvPort = requires(KVPort.class);
    //******* Children ******
    protected final Component overlay = create(VSOverlayManager.class, Init.NONE);
    protected final Component kv = create(KVService.class, Init.NONE);
    protected final Component beb = create(BEB.class, Init.NONE);
    protected final Component epfd = create(EPFD.class, Init.NONE);
    protected final Component meld = create(MELD.class, Init.NONE);
    protected final Component asc = create(MP.class, Init.NONE);
    protected final Component boot;

    {
        Optional<NetAddress> serverO = config().readValue("id2203.project.bootstrap-address", NetAddress.class);
        if (serverO.isPresent()) { // start in client mode
            boot = create(BootstrapClient.class, Init.NONE);
        } else { // start in server mode
            boot = create(BootstrapServer.class, Init.NONE);
        }
        // Boot
        connect(timer, boot.getNegative(Timer.class), Channel.TWO_WAY);
        connect(net, boot.getNegative(Network.class), Channel.TWO_WAY);
        // Overlay
        connect(boot.getPositive(Bootstrapping.class), overlay.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(net, overlay.getNegative(Network.class), Channel.TWO_WAY);
        // KV
        connect(overlay.getNegative(KVPort.class), kv.getPositive(KVPort.class), Channel.TWO_WAY);
        connect(net, kv.getNegative(Network.class), Channel.TWO_WAY);
        // BEB
        connect(overlay.getNegative(BEBPort.class), beb.getPositive(BEBPort.class), Channel.TWO_WAY);
        connect(net, beb.getNegative(Network.class), Channel.TWO_WAY);
        // EPFD
        connect(boot.getPositive(Bootstrapping.class), epfd.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(timer, epfd.getNegative(Timer.class), Channel.TWO_WAY);
        connect(net, epfd.getNegative(Network.class), Channel.TWO_WAY);
        // MELD
        connect(boot.getPositive(Bootstrapping.class), meld.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(overlay.getNegative(MELDPort.class), meld.getPositive(MELDPort.class), Channel.TWO_WAY);
        connect(meld.getNegative(EPFDPort.class), epfd.getPositive(EPFDPort.class), Channel.TWO_WAY);
        // MP
        connect(boot.getPositive(Bootstrapping.class), asc.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(overlay.getNegative(Consensus.class), asc.getPositive(Consensus.class), Channel.TWO_WAY);
        connect(net, asc.getNegative(Network.class), Channel.TWO_WAY);
    }
}