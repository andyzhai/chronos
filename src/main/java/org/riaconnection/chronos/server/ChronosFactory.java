package org.riaconnection.chronos.server;

import org.vertx.java.core.json.JsonObject;

public enum ChronosFactory {
    ChronosFactory;

    public JsonObject buildStringObject(String... strings) {
        JsonObject obj = new JsonObject();

        if (strings != null) {
            for (int i = 0; i < strings.length - 1; i += 2) {
                obj.putString(strings[i], strings[i + 1]);
            }
        }

        return obj;
    }

    public JsonObject buildStatusObject(String status, String message, String... strings) {
        JsonObject obj = buildStringObject(strings);

        obj.putString("status", status);
        obj.putString("message", message);

        return obj;
    }
}
