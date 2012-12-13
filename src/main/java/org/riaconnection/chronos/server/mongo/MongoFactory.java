package org.riaconnection.chronos.server.mongo;

import org.vertx.java.core.json.JsonObject;

public enum MongoFactory {
    MongoFactory;

    public JsonObject generateFindActionObject(String collection, String matcher) {

        return generateActionObject("find", collection, matcher);
    }

    public JsonObject generateDeleteActionObject(String collection, String matcher) {

        return generateActionObject("delete", collection, matcher);
    }

    public JsonObject generateSetObject(JsonObject setValue) {
        JsonObject set = new JsonObject();
        set.putObject("$set", setValue);
        return set;
    }

    public JsonObject generateUpdateActionObject(String collection, JsonObject criteria, JsonObject newData) {

        JsonObject action = new JsonObject();
        action.putString("action", "update");
        action.putString("collection", collection);

        action.putObject("criteria", criteria);
        action.putObject("objNew", newData);

        action.putBoolean("upsert", false);
        action.putBoolean("multi", false);

        return action;
    }

    private JsonObject generateActionObject(String type, String collection, String matcher) {

        JsonObject action = new JsonObject();
        action.putString("action", type);
        action.putString("collection", collection);

        JsonObject m = (matcher == null ? new JsonObject("{}") : new JsonObject(matcher));

        action.putObject("matcher", m);

        return action;
    }

    public JsonObject generateSaveActionObject(String collection, JsonObject document) {

        JsonObject action = new JsonObject();
        action.putString("action", "save");
        action.putString("collection", collection);

        action.putObject("document", document);

        return action;
    }
}
