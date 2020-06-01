package pl.vertx.router.user.processor;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import pl.vertx.router.user.UserService;

import static pl.vertx.router.user.processor.ProcessorUtil.*;

public class RegisterProcessor {
    private static final String DESCRIPTION_KEY = "description";
    private static final String INVALID_REQUEST_MESSAGE = "Incorrect input json data";
    private static final String USER_EXISTS_MESSAGE = "User already exists.";
    private static final String REGISTRATION_SUCCESSFUL_MESSAGE = "Registering successfull.";

    private final UserService userService;

    public RegisterProcessor(UserService userService) {
        this.userService = userService;
    }

    public void register(RoutingContext routingContext) {
        JsonObject requestBody = routingContext.getBodyAsJson();
        String login = requestBody.getString("login");
        String password = requestBody.getString("password");

        if(isDataValid(login, password)) {
            userService.ifUserExists(login, (isUserExists) -> processRegisterResponse(routingContext, isUserExists, login, password));
        } else {
            prepareResponse(routingContext, 400).end(prepareMessage(DESCRIPTION_KEY, INVALID_REQUEST_MESSAGE));
        }
    }

    private void processRegisterResponse(RoutingContext routingContext, boolean isUserExists, String login, String password) {
        if(isUserExists) {
            prepareResponse(routingContext, 400).end(prepareMessage(DESCRIPTION_KEY, USER_EXISTS_MESSAGE));
        } else {
            userService.createUser(login, password, (result) -> routeSuccessfulResponse(routingContext));
        }
    }

    private void routeSuccessfulResponse(RoutingContext routingContext) {
        prepareResponse(routingContext, 200).end(prepareMessage(DESCRIPTION_KEY, REGISTRATION_SUCCESSFUL_MESSAGE));
    }
}
