package pl.vertx.router.user.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import pl.vertx.AuthenticationService;
import pl.vertx.EncryptionService;
import pl.vertx.repository.user.User;
import pl.vertx.router.user.UserService;

import java.util.Optional;

import static pl.vertx.router.Messages.*;
import static pl.vertx.router.ProcessorUtil.*;
import static pl.vertx.router.RoutingContextSupport.*;

public class LoginProcessor {
    private final UserService userService;
    private final EncryptionService encryptionService;
    private final AuthenticationService authenticationService;

    public LoginProcessor(UserService userService, EncryptionService encryptionService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.encryptionService = encryptionService;
        this.authenticationService = authenticationService;
    }

    public void process(RoutingContext routingContext) {
        if(isContentTypeHeaderValid(routingContext.request())) {
            JsonObject requestBody = routingContext.getBodyAsJson();
            String login = requestBody.getString("login");
            String password = requestBody.getString("password");

            if(isDataValid(login, password)) {
                userService.fetchIfUserExists(login, encryptionService.encrypt(password), optionalUser -> processLogin(routingContext, optionalUser));
            } else {
                routeInvalidResponse(routingContext);
            }
        } else {
            routeUnsupportedContentTypeResponse(routingContext);
        }
    }

    private void processLogin(RoutingContext routingContext, Optional<User> optionalUser) {
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            jsonResponseWith(routingContext, 200)
                    .end(prepareMessage(TOKEN_KEY, authenticationService.provideToken(user.getId(), user.getLogin())));
        } else {
            jsonResponseWith(routingContext, 401)
                    .end(prepareMessage(DESCRIPTION_KEY, INVALID_CREDENTIALS_MESSAGE));
        }
    }
}
