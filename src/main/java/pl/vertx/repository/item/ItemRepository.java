package pl.vertx.repository.item;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;
import java.util.UUID;

public class ItemRepository {
    private static final String ITEM_COLLECTION = "items";

    private final MongoClient mongoClient;

    public ItemRepository(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void createItem(UUID owner, String name, Handler<AsyncResult<String>> handleResult) {
        mongoClient.insert(ITEM_COLLECTION, prepareItem(owner, name).toJson(), handleResult);
    }

    public void findByOwner(UUID owner, Handler<AsyncResult<List<JsonObject>>> handleResult) {
        mongoClient.find(ITEM_COLLECTION, prepareQuery(owner), handleResult);
    }

    private JsonObject prepareQuery(UUID owner) {
        return new JsonObject()
                .put("owner", owner.toString());
    }

    private Item prepareItem(UUID owner, String name) {
        return new Item(UUID.randomUUID(), owner, name);
    }
}
