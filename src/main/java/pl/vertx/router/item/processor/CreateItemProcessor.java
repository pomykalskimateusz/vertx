package pl.vertx.router.item.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import pl.vertx.AuthorizationService;
import pl.vertx.router.item.ItemService;

import java.util.Optional;
import java.util.UUID;

import static pl.vertx.router.ProcessorUtil.*;
import static pl.vertx.router.item.processor.ItemProcessorUtil.*;

public class CreateItemProcessor {
    private static final String DESCRIPTION_KEY = "description";
    private static final String SUCCESSFULLY_CREATED_MESSAGE = "Item created successful";
    private static final String INVALID_REQUEST_MESSAGE = "Incorrect request";
    private static final String UNAUTHORIZED_MESSAGE = "You have not provided an authentication token, the one provided has expired, was revoked or is not authentic.";

    private final ItemService itemService;
    private final AuthorizationService authorizationService;

    public CreateItemProcessor(ItemService itemService, AuthorizationService authorizationService) {
        this.itemService = itemService;
        this.authorizationService = authorizationService;
    }

    public void process(RoutingContext routingContext) {
        if(isHeaderValid(routingContext.request()) && isAuthorizationHeaderValid(routingContext.request())) {
            JsonObject requestBody = routingContext.getBodyAsJson();
            String name = requestBody.getString("title");

            if(isNameValid(name)) {
                authorizationService
                        .authenticate(extractToken(routingContext.request()), (uuid) -> processAuthorizationResponse(routingContext, uuid, name));
            } else {
                prepareResponse(routingContext, 400).end(prepareMessage(DESCRIPTION_KEY, INVALID_REQUEST_MESSAGE));
            }
        } else {
            prepareResponse(routingContext, 400).end(prepareMessage(DESCRIPTION_KEY, INVALID_REQUEST_MESSAGE));
        }
    }

    private void processAuthorizationResponse(RoutingContext routingContext, Optional<UUID> id, String name) {
        if(id.isPresent()) {
            itemService
                    .createBook(id.get(), name, (result) -> routeSuccessfulResponse(routingContext));
        } else {
            prepareResponse(routingContext, 401).end(prepareMessage(DESCRIPTION_KEY, UNAUTHORIZED_MESSAGE));
        }
    }

    private void routeSuccessfulResponse(RoutingContext routingContext) {
        prepareResponse(routingContext, 200). end(prepareMessage(DESCRIPTION_KEY, SUCCESSFULLY_CREATED_MESSAGE));
    }
}
