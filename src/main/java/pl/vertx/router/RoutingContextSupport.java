package pl.vertx.router;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static pl.vertx.router.Messages.*;

public class RoutingContextSupport {
    public static void routeInternalErrorResponse(RoutingContext routingContext) {
        routingContext
                .response()
                .setStatusCode(500)
                .end();
    }
    public static void routeInvalidResponse(RoutingContext routingContext) {
        jsonResponseWith(routingContext, 400)
                .end(prepareMessage(DESCRIPTION_KEY, INVALID_REQUEST_MESSAGE));
    }

    public static void routeUnauthorizedResponse(RoutingContext routingContext) {
        jsonResponseWith(routingContext, 401)
                .end(prepareMessage(DESCRIPTION_KEY, UNAUTHORIZED_MESSAGE));
    }

    public static void routeUnsupportedContentTypeResponse(RoutingContext routingContext) {
        jsonResponseWith(routingContext, 400)
                .end(prepareMessage(DESCRIPTION_KEY, UNSUPPORTED_CONTENT_TYPE));
    }

    public static String prepareMessage(String key, String message) {
        return new JsonObject()
                .put(key, message)
                .toString();
    }

    public static HttpServerResponse jsonResponseWith(RoutingContext routingContext, int statusCode) {
        return routingContext
                .response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(statusCode);
    }
}
