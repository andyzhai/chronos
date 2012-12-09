package org.riaconnection.chronos.server;

import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerResponse;

public enum ChronosResponder {
    RespondWith;

    public void userNotAuthorizedOr403(HttpServerResponse response) {
        response.statusCode = 403;
        response.statusMessage = "Forbidden or No Permission to Access";
        response.sendFile("webroot/system/403.html");
    }

    public void nonExistentContentOr404(HttpServerResponse response) {
        response.statusCode = 404;
        response.statusMessage = "The content your are trying to view does not exist.";
        response.sendFile("webroot/system/404.html");
    }

    public void jsonData(HttpServerResponse response, Buffer buff) {
        response.putHeader("Content-Type", "text/json; charset=utf-8");
        response.putHeader("Content-Length", buff.length());
        response.write(buff);
        response.end();
    }
}
