package pl.vertx.router.user.processor;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import pl.vertx.common.EncryptionService;
import pl.vertx.router.user.UserService;

import static pl.vertx.router.Messages.*;
import static pl.vertx.router.ProcessorUtil.*;
import static pl.vertx.router.RoutingContextSupport.*;

public class RegisterProcessor {
    private final UserService userService;
    private final EncryptionService encryptionService;

    public RegisterProcessor(UserService userService, EncryptionService encryptionService) {
        this.userService = userService;
        this.encryptionService = encryptionService;
    }

    public void process(RoutingContext routingContext) {
        if(isContentTypeHeaderValid(routingContext.request())) {
            JsonObject requestBody = routingContext.getBodyAsJson();
            String login = requestBody.getString("login");
            String password = requestBody.getString("password");

            if(isDataValid(login, password)) {
                userService
                    .checkIfUserExists(login, isUserExists -> processRegister(routingContext, isUserExists, login, tryToEncryptPassword(password)));
            } else {
                routeInvalidResponse(routingContext);
            }
        } else {
            routeUnsupportedContentTypeResponse(routingContext);
        }
    }

    private String tryToEncryptPassword(String password) {
        try {
            return encryptionService.encrypt(password);
        } catch (Exception ex) {
            return null;
        }
    }

    private void processRegister(RoutingContext routingContext, boolean isUserExists, String login, String password) {
        if(isUserExists) {
            jsonResponseWith(routingContext, 400)
                    .end(prepareMessage(DESCRIPTION_KEY, USER_EXISTS_MESSAGE));
        } else if (password == null) {
            routeInternalErrorResponse(routingContext);
        }
        else {
            userService.createUser(login, password, (result) -> routeSuccessfulResponse(routingContext));
        }
    }

    private void routeSuccessfulResponse(RoutingContext routingContext) {
        jsonResponseWith(routingContext, 200)
                .end(prepareMessage(DESCRIPTION_KEY, REGISTRATION_SUCCESSFUL_MESSAGE));
    }
}
