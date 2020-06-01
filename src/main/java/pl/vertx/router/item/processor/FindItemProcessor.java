package pl.vertx.router.item.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import pl.vertx.AuthenticationService;
import pl.vertx.repository.item.Item;
import pl.vertx.router.item.ItemService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static pl.vertx.router.ProcessorUtil.prepareMessage;
import static pl.vertx.router.ProcessorUtil.prepareResponse;
import static pl.vertx.router.item.processor.ItemProcessorUtil.extractToken;

public class FindItemProcessor {
    private static final String DESCRIPTION_KEY = "description";
    private static final String INVALID_REQUEST_MESSAGE = "Incorrect request";
    private static final String UNAUTHORIZED_MESSAGE = "You have not provided an authentication token, the one provided has expired, was revoked or is not authentic.";

    private final ItemService itemService;
    private final AuthenticationService authenticationService;

    public FindItemProcessor(ItemService itemService, AuthenticationService authenticationService) {
        this.itemService = itemService;
        this.authenticationService = authenticationService;
    }

    public void process(RoutingContext routingContext) {
        if(ItemProcessorUtil.isAuthorizationHeaderValid(routingContext.request())) {
            authenticationService
                    .authenticate(extractToken(routingContext.request()), (uuid) -> processAuthorizationResponse(routingContext, uuid));
        } else {
            prepareResponse(routingContext, 400).end(prepareMessage(DESCRIPTION_KEY, INVALID_REQUEST_MESSAGE));
        }
    }

    private void processAuthorizationResponse(RoutingContext routingContext, Optional<UUID> id) {
        if(id.isPresent()) {
            itemService.findByOwner(id.get(), (result) -> routeItemsResponse(routingContext, result));
        } else {
            prepareResponse(routingContext, 401).end(prepareMessage(DESCRIPTION_KEY, UNAUTHORIZED_MESSAGE));
        }
    }

    private void routeItemsResponse(RoutingContext routingContext, List<Item> items) {
        List<JsonObject> mappedItems = items.stream().map(this::toJson).collect(Collectors.toList());

        prepareResponse(routingContext, 200).end(mappedItems.toString());
    }

    private JsonObject toJson(Item item) {
        return new JsonObject()
                .put("id", item.getId().toString())
                .put("title", item.getName());
    }
}
