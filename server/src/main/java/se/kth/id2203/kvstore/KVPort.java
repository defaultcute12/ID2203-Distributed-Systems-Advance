package se.kth.id2203.kvstore;

import se.sics.kompics.PortType;

/**
 * Created by Klas on 2017-02-21.
 */
public class KVPort extends PortType {
    {
        request(OperationGET.class);
        request(OperationPUT.class);
        request(OperationCAS.class);
    }
}
