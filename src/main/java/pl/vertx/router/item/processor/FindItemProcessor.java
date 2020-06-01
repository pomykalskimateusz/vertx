package pl.vertx.router.item.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import pl.vertx.common.AuthenticationService;
import pl.vertx.repository.item.Item;
import pl.vertx.router.item.ItemService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.vertx.router.ProcessorUtil.*;
import static pl.vertx.router.RoutingContextSupport.*;

public class FindItemProcessor {
    private final ItemService itemService;
    private final AuthenticationService authenticationService;

    public FindItemProcessor(ItemService itemService, AuthenticationService authenticationService) {
        this.itemService = itemService;
        this.authenticationService = authenticationService;
    }

    public void process(RoutingContext routingContext) {
        if(isAuthorizationHeaderValid(routingContext.request())) {
            authenticationService
                    .authenticate(extractToken(routingContext.request()), uuid -> processFindItems(routingContext, uuid));
        } else {
            routeInvalidResponse(routingContext);
        }
    }

    private void processFindItems(RoutingContext routingContext, Optional<UUID> id) {
        if(id.isPresent()) {
            itemService
                    .findByOwner(id.get(), result -> routeItemsResponse(routingContext, result));
        } else {
            routeUnauthorizedResponse(routingContext);
        }
    }

    private void routeItemsResponse(RoutingContext routingContext, List<Item> items) {
        List<JsonObject> mappedItems = items
                .stream()
                .map(this::toResponseJson)
                .collect(Collectors.toList());

        jsonResponseWith(routingContext, 200)
                .end(mappedItems.toString());
    }

    private JsonObject toResponseJson(Item item) {
        return new JsonObject()
                .put("id", item.getId().toString())
                .put("title", item.getName());
    }
}
