package org.riaconnection.chronos.server.mongo;

import static org.riaconnection.chronos.server.mongo.MongoFactory.MongoFactory;

import org.riaconnection.chronos.server.mongo.handlers.InitMongoHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Container;

public enum MongoModule {
    MongoDB;

    private Container container;
    private EventBus eb;
    public static String MONGO_APP_NOTIFY_ADDRESS;

    public static final String MONGO_PUBLISH_ADDRESS = "vertx.mongopersistor";

    public void init(Container container, EventBus eb, String notifyAddress, JsonObject config,
            InitMongoHandler initMongoHandler) {
        // Deploy MongoDB module and load some sample data into it
        this.container = container;
        this.eb = eb;
        MONGO_APP_NOTIFY_ADDRESS = notifyAddress;
        this.container.deployModule("vertx.mongo-persistor-v1.2", config, 1, initMongoHandler);
    }

    public void find(String collection, String matcher, Handler<Message<JsonObject>> handler) {

        if (collection == null || collection.isEmpty()) {
            return;
        }

        JsonObject action = MongoFactory.generateFindActionObject(collection, matcher);

        eb.send(MONGO_PUBLISH_ADDRESS, action, handler);
    }

    public void delete(String collection, String matcher, Handler<Message<JsonObject>> handler) {

        if (collection == null || collection.isEmpty()) {
            return;
        }

        JsonObject action = MongoFactory.generateDeleteActionObject(collection, matcher);

        eb.send(MONGO_PUBLISH_ADDRESS, action, handler);
    }

    public void update(String collection, JsonObject criteria, JsonObject newData, Handler<Message<JsonObject>> handler) {

        if (collection == null || collection.isEmpty()) {
            return;
        }

        JsonObject action = MongoFactory.generateUpdateActionObject(collection, criteria, newData);

        eb.send(MONGO_PUBLISH_ADDRESS, action, handler);
    }

    public void save(String collection, JsonObject document, Handler<Message<JsonObject>> handler) {

        if (collection == null || collection.isEmpty()) {
            return;
        }

        JsonObject action = MongoFactory.generateSaveActionObject(collection, document);

        eb.send(MONGO_PUBLISH_ADDRESS, action, handler);
    }
}
