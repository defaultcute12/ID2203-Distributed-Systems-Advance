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
package se.kth.id2203.kvstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.kvstore.OpResponse.Code;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.Routing;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.HashMap;

/**
 *
 * @author Lars Kroll <lkroll@kth.se>
 */
public class KVService extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(KVService.class);
    //******* Ports ******
    protected final Positive<Routing> route = requires(Routing.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Routing> route = requires(Routing.class);

    //******* Fields ******
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    protected HashMap<String, String> kvstore = new HashMap<String, String>() {
        {
            put("1", "Hello Mallu");
            put("20", "There");
            put("33", "My");
            put("10", "Friend");
        }
    };
    //******* Handlers ******
    protected final ClassMatchedHandler<OperationGET, Message> getHandler = new ClassMatchedHandler<OperationGET, Message>() {

        @Override
        public void handle(OperationGET content, Message context) {
            String response = kvstore.get(content.key);
            trigger(new Message(self, context.getSource(), new OpResponse(content.id, response, Code.OK)), net);
        }
    };
    protected final ClassMatchedHandler<OperationPUT, Message> putHandler = new ClassMatchedHandler<OperationPUT, Message>() {

        @Override
        public void handle(OperationPUT content, Message context) {
            String response;
            if(kvstore.put(content.key, content.value) != null) {
                response = "Successfully added (" + content.key + "," + content.value + ")";
            } else {
                response = "FAIL, could not add (" + content.key + "," + content.value + ")";
            }
            trigger(new Message(self, context.getSource(), new OpResponse(content.id, response, Code.OK)), net);
        }
    };
    protected final ClassMatchedHandler<OperationCAS, Message> casHandler = new ClassMatchedHandler<OperationCAS, Message>() {

        @Override
        public void handle(OperationCAS content, Message context) {
            if(kvstore.get(content.key).compareTo(content.refValue) > 0) {
                kvstore.replace(content.key, content.newValue);
            }
            String response = "Successfully CAS (" + kvstore.get(content.key) + "," + content.newValue + ")";
            trigger(new Message(self, context.getSource(), new OpResponse(content.id, response, Code.OK)), net);
        }
    };

    {
        subscribe(getHandler, net);
        subscribe(putHandler, net);
        subscribe(casHandler, net);
    }
}