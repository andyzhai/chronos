package org.riaconnection.chronos.server.auth.handlers;

import static org.riaconnection.chronos.server.ChronosFactory.ChronosFactory;
import static org.riaconnection.chronos.server.mongo.MongoFactory.MongoFactory;
import static org.riaconnection.chronos.server.mongo.MongoModule.MongoDB;

import org.riaconnection.chronos.server.mongo.handlers.MongoStatusHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

public class UpdateCredentialsHandler implements Handler<Message<JsonObject>> {

    private Message<JsonObject> replyToMessage;
    private String username;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    public UpdateCredentialsHandler(JsonObject data, Message<JsonObject> replyToMessage) {

        this.username = data.getString("username");
        this.oldPassword = data.getString("oldPassword");
        this.newPassword = data.getString("newPassword");
        this.confirmPassword = data.getString("confirmPassword");
        this.replyToMessage = replyToMessage;
    }

    public UpdateCredentialsHandler(String username, String oldPassword, String newPassword, String confirmPassword,
            Message<JsonObject> replyToMessage) {

        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
        this.replyToMessage = replyToMessage;
    }

    @Override
    public void handle(Message<JsonObject> loginMessage) {

        if (loginMessage.body.getString("status").equals("ok")
                && loginMessage.body.getString("username").equals(this.username)
                && this.newPassword.equals(this.confirmPassword)) {

            MongoDB.find("users", "{\"username\":\"" + this.username + "\",\"password\":\"" + this.oldPassword + "\"}",
                    new Handler<Message<JsonObject>>() {

                        @Override
                        public void handle(Message<JsonObject> findMessage) {
                            if (findMessage.body.getString("status").equals("ok")
                                    && findMessage.body.getArray("results").size() == 1) {

                                JsonObject criteria = ChronosFactory.buildStringObject("username", username,
                                        "password", oldPassword);

                                JsonObject set = ChronosFactory.buildStringObject("password", newPassword);

                                JsonObject update = MongoFactory.generateSetObject(set);

                                MongoDB.update("users", criteria, update, new MongoStatusHandler("Password updated.",
                                        "Failed to change password.", replyToMessage));
                            } else {
                                if (replyToMessage != null) {
                                    JsonObject status = ChronosFactory.buildStatusObject("error",
                                            "Password verification failed.");
                                    replyToMessage.reply(status);
                                }
                            }
                        }

                    });

        } else {
            if (this.replyToMessage != null) {
                JsonObject status = ChronosFactory.buildStatusObject("error", "Password verification failed.");
                this.replyToMessage.reply(status);
            }
        }
    }
}
