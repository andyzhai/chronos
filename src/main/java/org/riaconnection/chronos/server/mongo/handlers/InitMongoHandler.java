package org.riaconnection.chronos.server.mongo.handlers;

import static org.riaconnection.chronos.server.mongo.ChronosMongoMessages.DB_LOADED;
import static org.riaconnection.chronos.server.mongo.MongoModule.MONGO_APP_NOTIFY_ADDRESS;

import org.riaconnection.chronos.server.mongo.MongoModule;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;

public class InitMongoHandler implements Handler<String> {

    private EventBus eb;

    public InitMongoHandler(EventBus eb) {
        this.eb = eb;
    }

    @Override
    public void handle(String message) {

        // Notify app that data has been loaded
        if (MONGO_APP_NOTIFY_ADDRESS != null) {
            eb.send(MongoModule.MONGO_APP_NOTIFY_ADDRESS, DB_LOADED.name());
        }

    }
}
