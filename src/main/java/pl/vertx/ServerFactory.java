package pl.vertx;

import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.mongo.MongoClient;
import pl.vertx.repository.item.ItemRepository;
import pl.vertx.repository.user.UserRepository;
import pl.vertx.router.item.ItemRouter;
import pl.vertx.router.item.ItemService;
import pl.vertx.router.user.UserRouter;
import pl.vertx.router.user.UserService;
import pl.vertx.router.user.processor.LoginProcessor;
import pl.vertx.router.user.processor.RegisterProcessor;

public class ServerFactory {
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final LoginProcessor loginProcessor;
    private final RegisterProcessor registerProcessor;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final RequestExecutor requestExecutor;

    public ServerFactory(MongoClient mongoClient, JWTAuth authenticationProvider, int requestPoolSize) {
        this.authenticationService = new AuthenticationService(authenticationProvider);
        this.userRepository = new UserRepository(mongoClient);
        this.userService = new UserService(userRepository);
        this.itemRepository = new ItemRepository(mongoClient);
        this.itemService = new ItemService(itemRepository);
        this.loginProcessor = new LoginProcessor(userService, authenticationService);
        this.registerProcessor = new RegisterProcessor(userService);
        this.requestExecutor = new RequestExecutor(requestPoolSize);
    }

    public UserRouter userRouter() {
        return new UserRouter(loginProcessor, registerProcessor, requestExecutor);
    }

    public ItemRouter itemRouter() {
        return new ItemRouter(itemService, authenticationService, requestExecutor);
    }
}
