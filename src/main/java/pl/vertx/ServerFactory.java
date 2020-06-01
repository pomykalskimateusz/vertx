package pl.vertx;

import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.mongo.MongoClient;
import pl.vertx.repository.item.ItemRepository;
import pl.vertx.repository.user.UserRepository;
import pl.vertx.router.item.ItemRouter;
import pl.vertx.router.item.ItemService;
import pl.vertx.router.item.processor.CreateItemProcessor;
import pl.vertx.router.item.processor.FindItemProcessor;
import pl.vertx.router.user.UserRouter;
import pl.vertx.router.user.UserService;
import pl.vertx.router.user.processor.LoginProcessor;
import pl.vertx.router.user.processor.RegisterProcessor;

public class ServerFactory {
    private final AuthorizationService authorizationService;
    private final EncryptionService encryptionService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final LoginProcessor loginProcessor;
    private final RegisterProcessor registerProcessor;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final CreateItemProcessor createItemProcessor;
    private final FindItemProcessor findItemProcessor;
    private final RequestExecutor requestExecutor;

    public ServerFactory(MongoClient mongoClient, JWTAuth authenticationProvider, int requestPoolSize) {
        this.authorizationService = new AuthorizationService(authenticationProvider);
        this.encryptionService = new EncryptionService();
        this.userRepository = new UserRepository(mongoClient);
        this.userService = new UserService(userRepository);
        this.itemRepository = new ItemRepository(mongoClient);
        this.itemService = new ItemService(itemRepository);
        this.createItemProcessor = new CreateItemProcessor(itemService, authorizationService);
        this.findItemProcessor = new FindItemProcessor(itemService, authorizationService);
        this.loginProcessor = new LoginProcessor(userService, encryptionService, authorizationService);
        this.registerProcessor = new RegisterProcessor(userService, encryptionService);
        this.requestExecutor = new RequestExecutor(requestPoolSize);
    }

    public UserRouter userRouter() {
        return new UserRouter(loginProcessor, registerProcessor, requestExecutor);
    }

    public ItemRouter itemRouter() {
        return new ItemRouter(createItemProcessor, findItemProcessor, requestExecutor);
    }
}
