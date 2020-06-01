package pl.vertx.repository.item;

import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class Item {
    private final UUID id;
    private final UUID owner;
    private final String name;

    public Item(UUID id, UUID owner, String name) {
        this.id = id;
        this.owner = owner;
        this.name = name;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("id", id.toString())
                .put("owner", owner.toString())
                .put("name", name);
    }

    public static Item fromJson(JsonObject jsonObject) {
        return new Item(
                uuidFrom(jsonObject.getString("id")),
                uuidFrom(jsonObject.getString("owner")),
                jsonObject.getString("name")
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
