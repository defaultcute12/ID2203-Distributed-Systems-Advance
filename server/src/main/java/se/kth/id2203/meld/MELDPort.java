package se.kth.id2203.meld;

import se.sics.kompics.PortType;

/**
 * Created by Klas on 2017-02-21.
 */
public class MELDPort extends PortType {
    {
        indication(Elect.class);
    }
}
