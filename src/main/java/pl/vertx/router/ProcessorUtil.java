package pl.vertx.router;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

public class ProcessorUtil {
    public static boolean isHeaderValid(HttpServerRequest request) {
        String header = request.getHeader("Content-Type");

        if(header == null) return false;
        else if(header.equals("application/json")) return true;
        else return false;
    }

    public static boolean isDataValid(String login, String password) {
        if(login == null || password == null) return false;
        else if(login.isEmpty() || password.isEmpty()) return false;
        else return true;
    }

    public static String prepareMessage(String key, String message) {
        return new JsonObject()
                .put(key, message)
                .toString();
    }
}
