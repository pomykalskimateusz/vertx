package pl.vertx.router.user.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import pl.vertx.EncryptionService;
import pl.vertx.router.RoutingContextSupport;
import pl.vertx.router.user.UserService;

import static pl.vertx.router.Messages.*;
import static pl.vertx.router.ProcessorUtil.*;

public class RegisterProcessor {
    private final UserService userService;
    private final EncryptionService encryptionService;

    public RegisterProcessor(UserService userService, EncryptionService encryptionService) {
        this.userService = userService;
        this.encryptionService = encryptionService;
    }

    public void process(RoutingContext routingContext) {
        if(isHeaderValid(routingContext.request())) {
            JsonObject requestBody = routingContext.getBodyAsJson();
            String login = requestBody.getString("login");
            String password = requestBody.getString("password");

            if(isDataValid(login, password)) {
                userService.ifUserExists(login, isUserExists -> processRegisterResponse(routingContext, isUserExists, login, encryptionService.encrypt(password)));
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

    private void processRegisterResponse(RoutingContext routingContext, boolean isUserExists, String login, String password) {
        if(isUserExists) {
            RoutingContextSupport
                    .of(routingContext)
                    .jsonResponseWith(400)
                    .end(prepareMessage(DESCRIPTION_KEY, USER_EXISTS_MESSAGE));
        } else {
            userService.createUser(login, password, (result) -> routeSuccessfulResponse(routingContext));
        }
    }

    private void routeSuccessfulResponse(RoutingContext routingContext) {
        RoutingContextSupport
                .of(routingContext)
                .jsonResponseWith(200)
                .end(prepareMessage(DESCRIPTION_KEY, REGISTRATION_SUCCESSFUL_MESSAGE));
    }
}
