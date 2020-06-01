package pl.vertx.router.item;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import pl.vertx.AuthenticationService;
import pl.vertx.RequestExecutor;
import pl.vertx.repository.item.Item;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemRouter {
    private final ItemService itemService;
    private final AuthenticationService authenticationService;
    private final RequestExecutor requestExecutor;

    public ItemRouter(ItemService itemService, AuthenticationService authenticationService, RequestExecutor requestExecutor) {
        this.itemService = itemService;
        this.authenticationService = authenticationService;
        this.requestExecutor = requestExecutor;
    }

    public void route(Router router) {
        router.post("/items")
               .handler(routingContext -> requestExecutor.processRequest(() -> createBook(routingContext)));
        router.get("/items")
                .handler(routingContext -> requestExecutor.processRequest(() -> findItems(routingContext)));
    }

    private void createBook(RoutingContext routingContext) {
        JsonObject requestBody = routingContext.getBodyAsJson();
        String name = requestBody.getString("name");
        String authHeader = routingContext.request().getHeader("Authorization");
        String token = authHeader.split("Bearer ")[1];

        if(isDataValid(name)) {
            authenticationService.authenticate(token, optionalUuidConsumerForCreateItem(routingContext, name));
        } else {
            prepareResponse(routingContext, 400).end("Invalid json");
        }
    }

    private Consumer<Optional<UUID>> optionalUuidConsumerForCreateItem(RoutingContext routingContext, String name) {
        return (optionalUuid) -> {
            if(optionalUuid.isPresent()) {
                itemService.createBook(optionalUuid.get(), name, (result) -> prepareResponse(routingContext, 200).end(result));
            } else {
                prepareResponse(routingContext, 404).end("Not found owner id");
            }
        };
    }

    private void findItems(RoutingContext routingContext) {
        String authHeader = routingContext.request().getHeader("Authorization");
        String token = authHeader.split("Bearer ")[1];

        authenticationService.authenticate(token, optionalUuidConsumerForFindItem(routingContext));
    }

    private Consumer<Optional<UUID>> optionalUuidConsumerForFindItem(RoutingContext routingContext) {
        return (optionalUuid) -> {
            if(optionalUuid.isPresent()) {
                itemService.findByOwner(optionalUuid.get(), itemListConsumer(routingContext));
            } else {
                prepareResponse(routingContext, 404).end("Not found owner id");
            }
        };
    }

    private Consumer<List<Item>> itemListConsumer(RoutingContext routingContext) {
        return (items) -> {
            List<JsonObject> jsonList = items.stream().map(Item::toJson).collect(Collectors.toList());

            prepareResponse(routingContext, 200).end(jsonList.toString());
        };
    }

    private boolean isDataValid(String name) {
        if(name == null) return false;
        else if(name.isEmpty()) return false;
        else return true;
    }

    private HttpServerResponse prepareResponse(RoutingContext routingContext, int statusCode) {
        return routingContext
                .response()
                .setStatusCode(statusCode);
    }
}
