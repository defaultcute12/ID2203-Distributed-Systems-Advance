package se.kth.id2203.riwm;

import se.sics.kompics.PortType;

/**
 * Created by Klas on 2017-02-25.
 */
public class AtomicRegister extends PortType {
    {
        request(ReadRequest.class);
        request(WriteRequest.class);
        indication(ReadResponse.class);
        indication(WriteResponse.class);
    }
}
