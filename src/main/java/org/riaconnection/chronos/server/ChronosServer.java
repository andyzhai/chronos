package org.riaconnection.chronos.server;

import static org.riaconnection.chronos.server.ChronosResponder.RespondWith;
import static org.riaconnection.chronos.server.auth.AuthManagerModule.AuthManager;
import static org.riaconnection.chronos.server.mongo.MongoModule.MongoDB;

import java.io.File;

import org.riaconnection.chronos.server.auth.handlers.SecureWebHandler;
import org.riaconnection.chronos.server.handlers.ChronosNotificationHandler;
import org.riaconnection.chronos.server.mongo.handlers.MongoWebHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.deploy.Verticle;

public class ChronosServer extends Verticle {
    private final static String APP_NOTIFY_ADDRESS = "chronos.app.notify";

    private Logger logger;
    private String cwd;

    @Override
    public void start() throws Exception {

        logger = container.getLogger();

        setCurrentWorkingDirectory();

        HttpServer server = vertx.createHttpServer();

        // Register HTTP handler - has to registered before the EventBus bridge
        server.requestHandler(new Handler<HttpServerRequest>() {

            @Override
            public void handle(HttpServerRequest req) {
                String file = null;
                if (req.path.equals("/")) {
                    file = cwd + "/index.html";
                } else if (req.path.startsWith("/admin")) {
                    if (req.params().containsKey("sessionID")) {
                        String sessionID = req.params().get("sessionID");
                        SecureWebHandler se = new SecureWebHandler(req, cwd + "/secured");
                        AuthManager.validateSession(sessionID, se);
                    } else {
                        RespondWith.userNotAuthorizedOr403(req.response);
                    }
                    return;
                } else if (req.path.endsWith("/timeline.json")) {
                    MongoWebHandler wh = new MongoWebHandler(req, "timeline");
                    MongoDB.find("timeline", null, wh);
                    return;
                } else {
                    file = cwd + req.path;
                    File f = new File(file);
                    logger.info("Absolute path: " + f.getAbsolutePath());
                    if (!f.exists() || f.isDirectory()) {
                        RespondWith.nonExistentContentOr404(req.response);
                        return;
                    }
                }
                req.response.sendFile(file);
                logger.info("Request path: " + file);
            }

        });

        // Load Auth configuration
        String authString = loadStaticData("auth_permissions.json");
        JsonObject authPermissions = new JsonObject(authString);

        // Set security permission to let everything go through
        JsonArray permitted = new JsonArray();
        // permitted.add(new JsonObject());
        permitted.add(authPermissions);

        // Create SockJS and bridge it to the Event Bus
        SockJSServer sockJSServer = vertx.createSockJSServer(server);
        sockJSServer.bridge(new JsonObject().putString("prefix", "/eventbus"), permitted, permitted);

        EventBus eb = vertx.eventBus();

        AuthManager.init(container, eb, null);

        // Load DB configuration
        String configString = loadStaticData("permissions.json");
        JsonObject config = new JsonObject(configString);

        // Start Mongo DB module
        MongoDB.init(container, eb, APP_NOTIFY_ADDRESS, config, null);

        // Register for app notifications
        eb.registerHandler(APP_NOTIFY_ADDRESS, new ChronosNotificationHandler(container));

        server.listen(8080);
    }

    private String loadStaticData(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }

        Buffer buffer = null;
        String data = null;

        try {
            buffer = vertx.fileSystem().readFileSync(this.cwd + "/data/" + filename);
            data = buffer.getString(0, buffer.length());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return data;
    }

    private void setCurrentWorkingDirectory() throws Exception {

        // If running as a module, find resource from classpath
        if (this.cwd == null) {

            // Check if webroot property was set
            this.cwd = System.getProperty("chronos.webroot.dir");
            if (this.cwd == null) {
                this.cwd = ChronosServer.class.getClassLoader().getResource("webroot") == null ? null
                        : ChronosServer.class.getClassLoader().getResource("webroot").getPath();

                if (this.cwd == null) {
                    // assume that files will be directly under CWD
                    this.cwd = "webroot";
                }
            }

            File f = new File(this.cwd);
            if (f.exists() && f.isDirectory()) {
                logger.info("Chronos Web Base set to: " + f.getAbsolutePath());
            } else {
                RuntimeException e = new RuntimeException(
                        "ERROR: 'webroot' directory not found. Please make sure that the directory exists under vertx working directory or under module the classpath.");
                logger.error(e.getMessage());
                throw e;
            }
        }
    }
}
