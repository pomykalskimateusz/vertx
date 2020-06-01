package pl.vertx.router.item.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import pl.vertx.AuthenticationService;
import pl.vertx.repository.item.Item;
import pl.vertx.router.RoutingContextSupport;
import pl.vertx.router.item.ItemService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.vertx.router.Messages.*;
import static pl.vertx.router.ProcessorUtil.prepareMessage;
import static pl.vertx.router.item.processor.ItemProcessorUtil.extractToken;

public class FindItemProcessor {
    private final ItemService itemService;
    private final AuthenticationService authenticationService;

    public FindItemProcessor(ItemService itemService, AuthenticationService authenticationService) {
        this.itemService = itemService;
        this.authenticationService = authenticationService;
    }

    public void process(RoutingContext routingContext) {
        if(ItemProcessorUtil.isAuthorizationHeaderValid(routingContext.request())) {
            authenticationService
                    .authenticate(extractToken(routingContext.request()), uuid -> processAuthenticationResponse(routingContext, uuid));
        } else {
            RoutingContextSupport
                    .of(routingContext)
                    .jsonResponseWith(400)
                    .end(prepareMessage(DESCRIPTION_KEY, INVALID_REQUEST_MESSAGE));
        }
    }

    private void processAuthenticationResponse(RoutingContext routingContext, Optional<UUID> id) {
        if(id.isPresent()) {
            itemService.findByOwner(id.get(), result -> routeItemsResponse(routingContext, result));
        } else {
            RoutingContextSupport
                    .of(routingContext)
                    .jsonResponseWith(401)
                    .end(prepareMessage(DESCRIPTION_KEY, UNAUTHORIZED_MESSAGE));
        }
    }

    private void routeItemsResponse(RoutingContext routingContext, List<Item> items) {
        List<JsonObject> mappedItems = items.stream().map(this::toJson).collect(Collectors.toList());

        RoutingContextSupport
                .of(routingContext)
                .jsonResponseWith(200)
                .end(mappedItems.toString());
    }

    private JsonObject toJson(Item item) {
        return new JsonObject()
                .put("id", item.getId().toString())
                .put("title", item.getName());
    }
}
