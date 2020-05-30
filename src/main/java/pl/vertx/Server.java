package pl.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;

//TODO Add dependency injection
public class Server extends AbstractVerticle {
    private final static int REQUEST_POOL_SIZE = 10;

    @Override
    public void start() {
        Router router = Router.router(vertx);

        MongoClient mongoClient = MongoClient.createShared(vertx, databaseConfiguration());
        RequestExecutor requestExecutor = new RequestExecutor(REQUEST_POOL_SIZE);
        Repository repository = new Repository(mongoClient);
        DataRouter dataRouter = new DataRouter(router, repository, requestExecutor);

        dataRouter.route();
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
}
