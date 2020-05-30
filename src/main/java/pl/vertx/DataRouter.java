package pl.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class DataRouter {
    private final Router router;
    private final Repository repository;
    private final RequestExecutor requestExecutor;

    public DataRouter(Router router, Repository repository, RequestExecutor requestExecutor) {
        this.router = router;
        this.repository = repository;
        this.requestExecutor = requestExecutor;
    }

    public void route() {
        router.get("/create")
              .handler(context -> requestExecutor.processRequest(createData(context)));
        router.get("/findAll")
              .handler(context -> requestExecutor.processRequest(findAll(context)));
    }

    private Runnable createData(RoutingContext context) {
        return () -> repository.createSomeData(result -> routeCreateDataResponse(result, context));
    }

    private Runnable findAll(RoutingContext context) {
        return () -> repository.findAll(result -> routeFindAllResponse(result, context));
    }

    private void routeFindAllResponse(AsyncResult<List<JsonObject>> result, RoutingContext routingContext) {
        prepareResponse(routingContext, 200).end(Json.encodePrettily(result.result()));
    }

    private void routeCreateDataResponse(AsyncResult<String> result, RoutingContext routingContext) {
        if(result.failed()) {
            prepareResponse(routingContext, 200).end("Error during insertion");
        } else {
            prepareResponse(routingContext, 400).end(result.result());
        }
    }

    private HttpServerResponse prepareResponse(RoutingContext routingContext, int status) {
        return routingContext
                .response()
                .setStatusCode(status);
    }
}
