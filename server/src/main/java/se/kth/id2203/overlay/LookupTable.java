/*
 * The MIT License
 *
 * Copyright 2017 Lars Kroll <lkroll@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id2203.overlay;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.TreeMultimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.NodeAssignment;
import se.kth.id2203.networking.NetAddress;

/**
 *
 * @author Lars Kroll <lkroll@kth.se>
 */
public class LookupTable implements NodeAssignment {

    final static Logger LOG = LoggerFactory.getLogger(LookupTable.class);
    static int REPLICATION_DEGREE = 3;
    static int KEY_MAX = 100;

    private static final long serialVersionUID = -8766981433378303267L;

    private final TreeMultimap<Integer, NetAddress> partitions = TreeMultimap.create();
    private final HashMap<NetAddress, Integer> keyofnode = new HashMap<>();

    public int hash(String key) {
        return key.hashCode() % KEY_MAX;
    }

    public Collection<NetAddress> lookup(int keyHash) {
        Integer partition = partitions.keySet().floor(keyHash);
        if (partition == null) {
            partition = partitions.keySet().last();
        }
        return partitions.get(partition);
    }

    public Collection<NetAddress> getNodes() {
        return partitions.values();
    }

    public Set<Integer> getKeys() {
        return partitions.keys().elementSet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LookupTable(\n");
        for (Integer key : partitions.keySet()) {
            sb.append(key);
            sb.append(" -> ");
            sb.append(Iterables.toString(partitions.get(key)));
            sb.append("\n");
        }
        sb.append(")");
        return sb.toString();
    }

    static LookupTable generate(ImmutableSet<NetAddress> nodes) {
        LookupTable lut = new LookupTable();
        int num_nodes = nodes.size();
        int num_partitions = num_nodes/REPLICATION_DEGREE;
        float share = KEY_MAX/num_partitions;
        int i = 0;
        int j = 0;
        for(i = 0; i < num_partitions; i++) {
            for(j = 0; j < REPLICATION_DEGREE; j++) {
                Float key = i*share;
                LOG.info("Lookup table key: " + key + " and share " + share);
                lut.partitions.put(key.intValue(), nodes.asList().get(i*REPLICATION_DEGREE+j));
                lut.keyofnode.put(nodes.asList().get(i*REPLICATION_DEGREE+j), key.intValue());
            }
        }
        return lut;
    }

    public Integer getKeyOfNode(NetAddress node) {
        return keyofnode.get(node);
    }
}