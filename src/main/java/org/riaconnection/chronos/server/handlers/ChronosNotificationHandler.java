package org.riaconnection.chronos.server.handlers;

import static org.riaconnection.chronos.server.ChronosFactory.ChronosFactory;
import static org.riaconnection.chronos.server.auth.AuthManagerModule.AuthManager;
import static org.riaconnection.chronos.server.mongo.ChronosMongoMessages.DB_LOADED;
import static org.riaconnection.chronos.server.mongo.MongoModule.MongoDB;

import org.riaconnection.chronos.server.mongo.handlers.MongoStatusHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.deploy.Container;

public class ChronosNotificationHandler implements Handler<Message<?>> {

    private Logger logger;
    private Message<JsonObject> clientMessage;
    private JsonObject data;

    public ChronosNotificationHandler(Container container) {
        logger = container.getLogger();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(Message<?> message) {
        logger.info("Notification message received: " + message.body.toString());

        if (message.body instanceof String) {
            if (message.body.equals(DB_LOADED.name())) {
                // DB was loaded... need to do anything?
                checkForAdministrator();
            }
        } else if (message.body instanceof JsonObject) {

            this.clientMessage = (Message<JsonObject>) message;

            data = JsonObject.class.cast(message.body);
            String action = data.getString("action");

            if (action.equals("changePassword")) {
                data = data.getObject("document");

                AuthManager.updateCredentials(data, this.clientMessage);
            }
        }
    }

    private void checkForAdministrator() {
        MongoDB.find("users", null, new Handler<Message<JsonObject>>() {

            @Override
            public void handle(Message<JsonObject> mongoMessage) {
                if (mongoMessage.body.getString("status").equals("ok")) {
                    JsonArray results = mongoMessage.body.getArray("results");
                    if (results.size() == 0) {
                        JsonObject document = ChronosFactory.buildStringObject("username", "admin", "password",
                                "password");
                        MongoDB.save("users", document, new MongoStatusHandler("Admin user added.",
                                "Failed add admin user.", null));
                    }
                }
            }

        });
    }

}
