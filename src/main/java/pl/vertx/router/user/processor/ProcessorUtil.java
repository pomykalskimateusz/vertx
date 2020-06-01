package pl.vertx.router.user.processor;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

class ProcessorUtil {
    static boolean isDataValid(String login, String password) {
        if(login == null || password == null) return false;
        else if(login.isEmpty() || password.isEmpty()) return false;
        else return true;
    }

    static HttpServerResponse prepareResponse(RoutingContext routingContext, int statusCode) {
        return routingContext
                .response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(statusCode);
    }

    static String prepareMessage(String key, String message) {
        return new JsonObject()
                .put(key, message)
                .toString();
    }
}
