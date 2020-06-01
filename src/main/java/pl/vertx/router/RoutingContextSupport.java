package pl.vertx.router;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class RoutingContextSupport {
    private final RoutingContext routingContext;

    private RoutingContextSupport(RoutingContext routingContext) {
        this.routingContext = routingContext;
    }

    public static RoutingContextSupport of(RoutingContext routingContext) {
        return new RoutingContextSupport(routingContext);
    }

    public HttpServerResponse jsonResponseWith(int statusCode) {
        return this.routingContext
                .response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(statusCode);
    }
}
