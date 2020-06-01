package pl.vertx;

import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.mongo.MongoClient;
import pl.vertx.repository.user.UserRepository;
import pl.vertx.router.user.UserRouter;
import pl.vertx.router.user.UserService;

public class ServerFactory {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RequestExecutor requestExecutor;

    public ServerFactory(MongoClient mongoClient, JWTAuth authenticationProvider, int requestPoolSize) {
        this.authenticationService = new AuthenticationService(authenticationProvider);
        this.userRepository = new UserRepository(mongoClient);
        this.userService = new UserService(userRepository);
        this.requestExecutor = new RequestExecutor(requestPoolSize);
    }

    public UserRouter userRouter() {
        return new UserRouter(userService, authenticationService, requestExecutor);
    }
}
