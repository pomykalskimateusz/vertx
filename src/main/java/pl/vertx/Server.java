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
    private static final int REQUEST_POOL_SIZE = 10;
    private static final String DATABASE_CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "test";


    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        MongoClient mongoClient = MongoClient.createShared(vertx, databaseConfiguration());
        ServerFactory serverFactory = new ServerFactory(mongoClient, authenticationProvider(), REQUEST_POOL_SIZE);

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
                .put("connection_string", DATABASE_CONNECTION_STRING)
                .put("db_name", DATABASE_NAME);
    }

    private JWTAuth authenticationProvider() {
        JWTAuthOptions config = new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("HS256")
                        .setPublicKey("keyboard cat")
                        .setSymmetric(true));

        return JWTAuth.create(vertx, config);
    }
}
