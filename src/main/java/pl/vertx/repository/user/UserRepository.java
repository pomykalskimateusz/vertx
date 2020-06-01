package pl.vertx.repository.user;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;
import java.util.UUID;

public class UserRepository {
    private static final String USER_COLLECTION = "users";

    private final MongoClient mongoClient;

    public UserRepository(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void createUser(String login, String password, Handler<AsyncResult<String>> handleResult) {
        mongoClient.insert(USER_COLLECTION, prepareUser(login, password).toJson(), handleResult);
    }

    public void findByLogin(String login, Handler<AsyncResult<List<JsonObject>>> handleResult) {
        mongoClient.find(USER_COLLECTION, new JsonObject().put("login", login),  handleResult);
    }

    public void findByLoginAndPassword(String login, String password, Handler<AsyncResult<JsonObject>> handleResult) {
        mongoClient.findOne(USER_COLLECTION, prepareQuery(login, password), null, handleResult);
    }

    private User prepareUser(String login, String password) {
        return new User(UUID.randomUUID(), login, password);
    }

    private JsonObject prepareQuery(String login, String password) {
        return new JsonObject()
                .put("login", login)
                .put("password", password);
    }
}
