package pl.vertx.router.user.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import pl.vertx.AuthenticationService;
import pl.vertx.repository.user.User;
import pl.vertx.router.user.UserService;

import java.util.Optional;

import static pl.vertx.router.user.processor.ProcessorUtil.*;

public class LoginProcessor {
    private static final String DESCRIPTION_KEY = "description";
    private static final String TOKEN_KEY = "token";
    private static final String INVALID_REQUEST_MESSAGE = "Incorrect input json data";
    private static final String INVALID_CREDENTIALS_MESSAGE = "Incorrect credentials";

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public LoginProcessor(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    public void login(RoutingContext routingContext) {
        JsonObject requestBody = routingContext.getBodyAsJson();
        String login = requestBody.getString("login");
        String password = requestBody.getString("password");

        if(isDataValid(login, password)) {
            userService.ifUserExists(login, password, (optionalUser) -> processLoginResponse(routingContext, optionalUser));
        } else {
            prepareResponse(routingContext, 400).end(prepareMessage(DESCRIPTION_KEY, INVALID_REQUEST_MESSAGE));
        }
    }

    private void processLoginResponse(RoutingContext routingContext, Optional<User> optionalUser) {
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            prepareResponse(routingContext, 200).end(prepareMessage(TOKEN_KEY, authenticationService.provideToken(user.getId(), user.getLogin())));
        } else {
            prepareResponse(routingContext, 401).end(prepareMessage(DESCRIPTION_KEY, INVALID_CREDENTIALS_MESSAGE));
        }
    }
}
