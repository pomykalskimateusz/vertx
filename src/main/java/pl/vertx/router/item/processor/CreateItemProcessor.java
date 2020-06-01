package pl.vertx.router.item.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import pl.vertx.AuthenticationService;
import pl.vertx.router.RoutingContextSupport;
import pl.vertx.router.item.ItemService;

import java.util.Optional;
import java.util.UUID;

import static pl.vertx.router.Messages.*;
import static pl.vertx.router.ProcessorUtil.*;
import static pl.vertx.router.item.processor.ItemProcessorUtil.*;

public class CreateItemProcessor {
    private final ItemService itemService;
    private final AuthenticationService authenticationService;

    public CreateItemProcessor(ItemService itemService, AuthenticationService authenticationService) {
        this.itemService = itemService;
        this.authenticationService = authenticationService;
    }

    public void process(RoutingContext routingContext) {
        if(isHeaderValid(routingContext.request()) && isAuthorizationHeaderValid(routingContext.request())) {
            JsonObject requestBody = routingContext.getBodyAsJson();
            String name = requestBody.getString("title");

            if(isNameValid(name)) {
                authenticationService
                        .authenticate(extractToken(routingContext.request()), uuid -> processAuthenticationResponse(routingContext, uuid, name));
            } else {
                RoutingContextSupport
                        .of(routingContext)
                        .jsonResponseWith(400)
                        .end(prepareMessage(DESCRIPTION_KEY, INVALID_REQUEST_MESSAGE));
            }
        } else {
            RoutingContextSupport
                    .of(routingContext)
                    .jsonResponseWith(400)
                    .end(prepareMessage(DESCRIPTION_KEY, INVALID_REQUEST_MESSAGE));
        }
    }

    private void processAuthenticationResponse(RoutingContext routingContext, Optional<UUID> id, String name) {
        if(id.isPresent()) {
            itemService
                    .createBook(id.get(), name, result -> routeSuccessfulResponse(routingContext));
        } else {
            RoutingContextSupport
                    .of(routingContext)
                    .jsonResponseWith(401)
                    .end(prepareMessage(DESCRIPTION_KEY, UNAUTHORIZED_MESSAGE));
        }
    }

    private void routeSuccessfulResponse(RoutingContext routingContext) {
        RoutingContextSupport
                .of(routingContext)
                .jsonResponseWith(200)
                .end(prepareMessage(DESCRIPTION_KEY, SUCCESSFULLY_CREATED_MESSAGE));
    }
}
