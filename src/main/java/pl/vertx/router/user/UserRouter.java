package pl.vertx.router.user;

import io.vertx.ext.web.Router;

import pl.vertx.common.RequestExecutor;
import pl.vertx.router.user.processor.LoginProcessor;
import pl.vertx.router.user.processor.RegisterProcessor;


public class UserRouter {
    private final LoginProcessor loginProcessor;
    private final RegisterProcessor registerProcessor;
    private final RequestExecutor requestExecutor;

    public UserRouter(LoginProcessor loginProcessor, RegisterProcessor registerProcessor, RequestExecutor requestExecutor) {
        this.loginProcessor = loginProcessor;
        this.registerProcessor = registerProcessor;
        this.requestExecutor = requestExecutor;
    }

    public void route(Router router) {
        router.post("/register")
              .handler(routingContext -> requestExecutor.processRequest(() -> registerProcessor.process(routingContext)));
        router.post("/login")
              .handler(routingContext -> requestExecutor.processRequest(() -> loginProcessor.process(routingContext)));
    }
}
