package pl.vertx.router.user.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import pl.vertx.AuthenticationService;
import pl.vertx.EncryptionService;
import pl.vertx.repository.user.User;
import pl.vertx.router.RoutingContextSupport;
import pl.vertx.router.user.UserService;

import java.util.Optional;

import static pl.vertx.router.Messages.*;
import static pl.vertx.router.ProcessorUtil.*;

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
        if(isHeaderValid(routingContext.request())) {
            JsonObject requestBody = routingContext.getBodyAsJson();
            String login = requestBody.getString("login");
            String password = requestBody.getString("password");

            if(isDataValid(login, password)) {
                userService.ifUserExists(login, encryptionService.encrypt(password), optionalUser -> processLoginResponse(routingContext, optionalUser));
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
                    .end(prepareMessage(DESCRIPTION_KEY, UNSUPPORTED_CONTENT_TYPE));
        }
    }

    private void processLoginResponse(RoutingContext routingContext, Optional<User> optionalUser) {
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            RoutingContextSupport
                    .of(routingContext)
                    .jsonResponseWith(200)
                    .end(prepareMessage(TOKEN_KEY, authenticationService.provideToken(user.getId(), user.getLogin())));
        } else {
            RoutingContextSupport
                    .of(routingContext)
                    .jsonResponseWith(401)
                    .end(prepareMessage(DESCRIPTION_KEY, INVALID_CREDENTIALS_MESSAGE));
        }
    }
}
