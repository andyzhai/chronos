package org.riaconnection.chronos.server.mongo.handlers;

import static org.riaconnection.chronos.server.ChronosFactory.ChronosFactory;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

public class MongoStatusHandler implements Handler<Message<JsonObject>> {

    private Message<JsonObject> replyToMessage;
    private String successMessage = "OK";
    private String errorMessage = "ERROR";

    public MongoStatusHandler(String successMessage, String errorMessage, Message<JsonObject> replyToMessage) {

        this.replyToMessage = replyToMessage;
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;

    }

    @Override
    public void handle(Message<JsonObject> mongoMessage) {

        if (this.replyToMessage != null) {
            JsonObject status;

            if (mongoMessage.body.getString("status").equals("ok")) {
                status = ChronosFactory.buildStatusObject("ok", this.successMessage);
            } else {
                status = ChronosFactory.buildStatusObject("error", this.errorMessage);
            }

            this.replyToMessage.reply(status);
        }
    }
}
