package pl.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class Server extends AbstractVerticle {
    private final static int REQUEST_POOL_SIZE = 10;

    @Override
    public void start() {
        Router router = Router.router(vertx);
        MongoClient mongoClient = MongoClient.createShared(vertx, databaseConfiguration());
        ServerFactory serverFactory = new ServerFactory(mongoClient, authorizationProvider(), REQUEST_POOL_SIZE);

        router.route().handler(BodyHandler.create());
        serverFactory.userRouter().route(router);
        serverFactory.itemRouter().route(router);
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
    }

    private JsonObject databaseConfiguration() {
        return new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name", "test");
    }

    private JWTAuth authorizationProvider() {
        JWTAuthOptions config = new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("HS256")
                        .setPublicKey("keyboard cat")
                        .setSymmetric(true));

        return JWTAuth.create(vertx, config);
    }
}
