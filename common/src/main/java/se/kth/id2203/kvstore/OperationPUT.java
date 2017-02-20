package se.kth.id2203.kvstore;

import com.google.common.base.MoreObjects;

/**
 * Created by Klas on 2017-02-20.
 */
public class OperationPUT extends Operation {

    public final String value;

    public OperationPUT(String key, String value) {
        super(key);
        this.value = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("key", key)
                .toString();
    }
}