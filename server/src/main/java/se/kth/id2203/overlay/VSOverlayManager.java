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

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.BEBRequest;
import se.kth.id2203.beb.BEBDeliver;
import se.kth.id2203.beb.BEBPort;
import se.kth.id2203.bootstrapping.*;
import se.kth.id2203.meld.Elect;
import se.kth.id2203.kvstore.*;
import se.kth.id2203.meld.MELDPort;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

/**
 * The V(ery)S(imple)OverlayManager.
 * <p>
 * Keeps all nodes in a single partition in one replication group.
 * <p>
 * Note: This implementation does not fulfill the project task. You have to
 * support multiple partitions!
 * <p>
 * @author Lars Kroll <lkroll@kth.se>
 */
public class VSOverlayManager extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(VSOverlayManager.class);
    //******* Ports ******
    protected boolean is_leader = false;
    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    protected final Positive<BEBPort> beb = requires(BEBPort.class);
    protected final Positive<KVPort> kv = requires(KVPort.class);
    protected final Positive<MELDPort> meld = requires(MELDPort.class);
    //******* Fields ******
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    protected NetAddress leader = null;
    private LookupTable lut = null;
    //******* Handlers ******
    protected final Handler<GetInitialAssignments> initialAssignmentHandler = new Handler<GetInitialAssignments>() {

        @Override
        public void handle(GetInitialAssignments event) {
            LOG.debug("Handler<GetInitialAssignments>");
            LOG.info("Generating LookupTable...");
            LookupTable lut = LookupTable.generate(event.nodes);
            LOG.debug("Generated assignments:\n{}", lut);
            trigger(new InitialAssignments(lut), boot);
        }
    };
    protected final Handler<Booted> bootHandler = new Handler<Booted>() {

        @Override
        public void handle(Booted event) {
            LOG.debug("Handler<Booted>");
            if (event.assignment instanceof LookupTable) {
                LOG.info("Got NodeAssignment, overlay ready.");
                lut = (LookupTable) event.assignment;
            } else {
                LOG.error("Got invalid NodeAssignment type. Expected: LookupTable; Got: {}", event.assignment.getClass());
            }

        }
    };
    protected final ClassMatchedHandler<Connect, Message> connectHandler = new ClassMatchedHandler<Connect, Message>() {

        @Override
        public void handle(Connect content, Message context) {
            LOG.debug("ClassMatchedHandler<Connect, Message>");
            if (lut != null) {
                LOG.debug("Accepting connection request from {}", context.getSource());
                int size = lut.getNodes().size();
                trigger(new Message(self, context.getSource(), content.ack(size)), net);
            } else {
                LOG.info("Rejecting connection request from {}, as system is not ready, yet.", context.getSource());
            }
        }
    };
    protected final ClassMatchedHandler<RouteMsg, Message> routeHandler = new ClassMatchedHandler<RouteMsg, Message>() {

        @Override
        public void handle(RouteMsg content, Message context) {
            LOG.debug("ClassMatchedHandler<Operation, Message>");
            Collection<NetAddress> partition = lut.lookup(lut.hash(content.key));
            if(partition.contains(self)) {
                if(self.equals(leader)) {
                    trigger(content.msg, kv);
                } else {
                    trigger(content.msg, kv);
                }
            } else {
                BEBDeliver bebDeliver = new BEBDeliver(context.getSource(), content);
                BEBRequest bebRequest = new BEBRequest(bebDeliver, partition);
                trigger(bebRequest, beb);
            }
        }
    };
    protected final Handler<Elect> electHandler = new Handler<Elect>() {
        @Override
        public void handle(Elect event) {
            LOG.debug("Handler<Elect>");
            leader = event.leader;
        }
    };

    {
        subscribe(initialAssignmentHandler, boot);
        subscribe(bootHandler, boot);
        subscribe(routeHandler, net);
        subscribe(connectHandler, net);
        subscribe(electHandler, meld);
    }
}