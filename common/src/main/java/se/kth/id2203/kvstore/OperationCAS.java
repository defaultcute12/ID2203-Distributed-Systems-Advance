package se.kth.id2203.kvstore;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Klas on 2017-02-20.
 */
public class OperationCAS extends Operation {

    public final String refValue;
    public final String newValue;

    public OperationCAS(String key, String refValue, String newValue) {
        super(key);
        this.refValue = refValue;
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("key", key)
                .toString();
    }
}