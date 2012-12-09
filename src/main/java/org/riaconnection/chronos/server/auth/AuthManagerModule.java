package org.riaconnection.chronos.server.auth;

import static org.riaconnection.chronos.server.auth.AuthManagerFactory.AuthFactory;

import org.riaconnection.chronos.server.auth.handlers.UpdateCredentialsHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Container;

public enum AuthManagerModule {
    AuthManager;

    private static final String AUTH_ADDRESS = "vertx.basicauthmanager";
    private static final String AUTHORIZE = AUTH_ADDRESS + ".authorise";
    private static final String LOGIN = AUTH_ADDRESS + ".login";

    private Container container;
    private EventBus eb;

    public void init(Container container, EventBus eb, JsonObject config) {
        // Deploy MongoDB module and load some sample data into it
        this.container = container;
        this.eb = eb;
        this.container.deployModule("vertx.auth-mgr-v1.1");
    }

    public void login(String username, String password, Handler<Message<JsonObject>> handler) {

        if (username == null || username.isEmpty() || password == null) return;

        JsonObject doc = AuthFactory.generateLoginActionObject(username, password);

        eb.send(LOGIN, doc, handler);
    }

    public void validateSession(String sessionID, Handler<Message<JsonObject>> handler) {

        if (sessionID == null || sessionID.isEmpty()) return;

        JsonObject doc = AuthFactory.generateAuthoriseActionObject(sessionID);

        eb.send(AUTHORIZE, doc, handler);
    }

    public void updateCredentials(JsonObject data, Message<JsonObject> replyToMessage) {

        validateSession(data.getString("sessionID"), new UpdateCredentialsHandler(data, replyToMessage));

    }

}
