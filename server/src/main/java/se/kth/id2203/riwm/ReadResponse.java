package se.kth.id2203.riwm;

import se.sics.kompics.KompicsEvent;

/**
 * Created by Klas on 2017-02-25.
 */
public class ReadResponse implements KompicsEvent {
    protected final Integer value;

    public ReadResponse(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
