package org.riaconnection.chronos.server.auth.handlers;

import static org.riaconnection.chronos.server.ChronosResponder.RespondWith;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

public class SecureWebHandler implements Handler<Message<JsonObject>> {

    private HttpServerRequest req;
    private String rootPath;

    public SecureWebHandler(HttpServerRequest req, String rootPath) {
        this.req = req;
        this.rootPath = rootPath;
    }

    @Override
    public void handle(Message<JsonObject> message) {
        if (message.body.getString("status").equals("ok")) {
            req.response.sendFile(rootPath + req.path);
        } else {
            RespondWith.userNotAuthorizedOr403(req.response);
        }
    }

}
