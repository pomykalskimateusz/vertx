package pl.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

public class Repository {
    private final static String COLLECTION = "names";
    private final MongoClient mongoClient;

    public Repository(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void createSomeData(Handler<AsyncResult<String>> handleResult) {
        JsonObject object = new JsonObject().put("name", "Alex");
        mongoClient
                .insert(COLLECTION, object, handleResult);
    }

    public void findAll(Handler<AsyncResult<List<JsonObject>>> handleResult) {
        sleepForEventLoopTestPurposes();
        mongoClient
                .find(COLLECTION, new JsonObject(), handleResult);
    }

    private void sleepForEventLoopTestPurposes() {
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
