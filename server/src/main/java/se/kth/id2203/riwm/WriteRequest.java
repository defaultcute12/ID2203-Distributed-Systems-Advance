package se.kth.id2203.riwm;

import se.sics.kompics.KompicsEvent;

/**
 * Created by Klas on 2017-02-25.
 */
public class WriteRequest implements KompicsEvent {
    protected final Integer value;

    public WriteRequest(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
