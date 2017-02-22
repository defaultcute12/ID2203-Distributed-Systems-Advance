package se.kth.id2203.le;

import se.sics.kompics.PortType;

/**
 * Created by Klas on 2017-02-21.
 */
public class LEPort extends PortType {
    {
        indication(Elect.class);
    }
}
