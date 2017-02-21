package se.kth.id2203.epdf;

/**
 * Created by Mallu on 21-02-2017.
 */
import se.sics.kompics.PortType;

public class FdPort extends PortType {
    {
        indication(Suspect.class);
        indication(Restore.class);
    }
}
