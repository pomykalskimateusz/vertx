package pl.vertx.router.user;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import pl.vertx.AuthenticationService;
import pl.vertx.RequestExecutor;
import pl.vertx.repository.user.User;

import java.util.Optional;
import java.util.function.Consumer;

public class UserRouter {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final RequestExecutor requestExecutor;

    public UserRouter(UserService userService, AuthenticationService authenticationService, RequestExecutor requestExecutor) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.requestExecutor = requestExecutor;
    }

    public void route(Router router) {
        router.post("/register")
              .handler(routingContext -> requestExecutor.processRequest(() -> registerUser(routingContext)));
        router.post("/login")
                .handler(routingContext -> requestExecutor.processRequest(() -> loginUser(routingContext)));
    }

    private void registerUser(RoutingContext routingContext) {
        JsonObject requestBody = routingContext.getBodyAsJson();
        String login = requestBody.getString("login");
        String password = requestBody.getString("password");

        if(isDataValid(login, password)) {
            userService.ifUserExists(login, processRegistration(routingContext, login, password));
        } else {
            prepareResponse(routingContext, 400);
        }
    }

    private Consumer<Boolean> processRegistration(RoutingContext routingContext, String login, String password) {
        return (isExists) -> {
            if(isExists) {
                prepareResponse(routingContext, 400).end("User already exists");
            } else {
                userService.createUser(login, password, (resultId) -> prepareResponse(routingContext, 200).end(resultId));
            }
        };
    }

    private void loginUser(RoutingContext routingContext) {
        JsonObject requestBody = routingContext.getBodyAsJson();
        String login = requestBody.getString("login");
        String password = requestBody.getString("password");

        if(isDataValid(login, password)) {
            userService.ifUserExists(login, password, processLogin(routingContext));
        } else {
            prepareResponse(routingContext, 404).end("Invalid json");
        }
    }

    private Consumer<Optional<User>> processLogin(RoutingContext routingContext) {
        return (optionalUser) -> {
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                routingContext.response().end(authenticationService.provideToken(user.getId(), user.getLogin()));
            } else {
                routingContext.response().end("Invalid credentials");
            }
        };
    }

    private boolean isDataValid(String login, String password) {
        if(login == null || password == null) return false;
        else if(login.isEmpty() || password.isEmpty()) return false;
        else return true;
    }

    private HttpServerResponse prepareResponse(RoutingContext routingContext, int statusCode) {
        return routingContext
                .response()
                .setStatusCode(statusCode);
    }
}
