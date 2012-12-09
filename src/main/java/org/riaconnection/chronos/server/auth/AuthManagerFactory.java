package org.riaconnection.chronos.server.auth;

import org.vertx.java.core.json.JsonObject;

public enum AuthManagerFactory {
    AuthFactory;

    public JsonObject generateAuthoriseActionObject(String sessionID) {
        JsonObject action = new JsonObject();
        action.putString("sessionID", sessionID);
        return action;
    }

    public JsonObject generateLoginActionObject(String username, String password) {
        JsonObject action = new JsonObject();
        action.putString("username", username);
        action.putString("password", password);
        return action;
    }
}
