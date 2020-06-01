package pl.vertx.router.item;

import io.vertx.core.json.JsonObject;
import pl.vertx.repository.item.Item;
import pl.vertx.repository.item.ItemRepository;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void createBook(UUID owner, String name, Consumer<String> function) {
        itemRepository.createItem(owner, name, result -> function.accept(result.result()));
    }

    public void findByOwner(UUID owner, Consumer<List<Item>> function) {
        itemRepository.findByOwner(owner, result -> function.accept(mapResponse(result.result())));
    }

    private List<Item> mapResponse(List<JsonObject> items) {
        return items
                .stream()
                .map(Item::fromJson)
                .collect(Collectors.toList());
    }
}
