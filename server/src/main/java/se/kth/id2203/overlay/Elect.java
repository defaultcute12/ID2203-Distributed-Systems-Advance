package se.kth.id2203.overlay;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

/**
 * Created by Klas on 2017-02-17.
 */
public class Elect implements KompicsEvent {
    private static final long serialVersionUID = -5044903955690184224L;
    public final NetAddress leaderAddress;

    public Elect(NetAddress leaderAddress) {
        this.leaderAddress = leaderAddress;
    }
}
