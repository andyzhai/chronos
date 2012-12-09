package org.riaconnection.chronos.server.mongo.handlers;

import static org.riaconnection.chronos.server.ChronosResponder.RespondWith;

import java.util.Iterator;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

public class MongoWebHandler implements Handler<Message<JsonObject>> {

    private HttpServerRequest req;
    private String rootProperty;

    public MongoWebHandler(HttpServerRequest req, String rootProperty) {
        this.req = req;
        this.rootProperty = rootProperty;
    }

    @Override
    public void handle(Message<JsonObject> message) {
        String doc = "\"\"";

        if (message.body.getString("status").equals("ok")) {
            JsonArray results = message.body.getArray("results");
            Iterator<Object> i = results.iterator();
            while (i.hasNext()) {
                doc = i.next().toString();
            }
        }

        if (this.rootProperty != null) {
            doc = "{ \"" + this.rootProperty + "\":" + doc + "}\n";
        }

        Buffer buff = new Buffer(doc);
        RespondWith.jsonData(req.response, buff);
    }

}
