package pl.vertx.router.item.processor;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import pl.vertx.AuthenticationService;
import pl.vertx.router.item.ItemService;

import java.util.Optional;
import java.util.UUID;

import static pl.vertx.router.Messages.*;
import static pl.vertx.router.ProcessorUtil.*;
import static pl.vertx.router.RoutingContextSupport.*;

public class CreateItemProcessor {
    private final ItemService itemService;
    private final AuthenticationService authenticationService;

    public CreateItemProcessor(ItemService itemService, AuthenticationService authenticationService) {
        this.itemService = itemService;
        this.authenticationService = authenticationService;
    }

    public void process(RoutingContext routingContext) {
        if(areHeadersValid(routingContext.request())) {
            JsonObject requestBody = routingContext.getBodyAsJson();
            String name = requestBody.getString("title");

            if(isNameValid(name)) {
                authenticationService
                        .authenticate(extractToken(routingContext.request()), uuid -> processCreateItem(routingContext, uuid, name));
            } else {
                routeInvalidResponse(routingContext);
            }
        } else {
            routeInvalidResponse(routingContext);
        }
    }

    private void processCreateItem(RoutingContext routingContext, Optional<UUID> id, String name) {
        if(id.isPresent()) {
            itemService
                    .createBook(id.get(), name, result -> routeSuccessfulResponse(routingContext));
        } else {
            routeUnauthorizedResponse(routingContext);
        }
    }

    private boolean areHeadersValid(HttpServerRequest request) {
        return isContentTypeHeaderValid(request) && isAuthorizationHeaderValid(request);
    }

    private void routeSuccessfulResponse(RoutingContext routingContext) {
        jsonResponseWith(routingContext, 200)
                .end(prepareMessage(DESCRIPTION_KEY, SUCCESSFULLY_CREATED_MESSAGE));
    }
}
