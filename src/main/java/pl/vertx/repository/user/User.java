package pl.vertx.repository.user;

import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class User {
    private UUID id;
    private String login;
    private String password;

    public User(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id.toString())
                .put("login", login)
                .put("password", password);
    }

    public static User fromJson(JsonObject jsonObject) {
        return new User(
                uuidFrom(jsonObject.getString("id")),
                jsonObject.getString("login"),
                jsonObject.getString("password")
        );
    }

    private static UUID uuidFrom(String value) {
        if(value == null) {
            return null;
        } else {
            return UUID.fromString(value);
        }
    }
}
