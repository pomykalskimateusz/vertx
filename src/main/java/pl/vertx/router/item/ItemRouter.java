package pl.vertx.router.item;

import io.vertx.ext.web.Router;

import pl.vertx.RequestExecutor;
import pl.vertx.router.item.processor.CreateItemProcessor;
import pl.vertx.router.item.processor.FindItemProcessor;

public class ItemRouter {
    private final CreateItemProcessor createItemProcessor;
    private final FindItemProcessor findItemProcessor;
    private final RequestExecutor requestExecutor;

    public ItemRouter(CreateItemProcessor createItemProcessor, FindItemProcessor findItemProcessor, RequestExecutor requestExecutor) {
        this.createItemProcessor = createItemProcessor;
        this.findItemProcessor = findItemProcessor;
        this.requestExecutor = requestExecutor;
    }

    public void route(Router router) {
        router.post("/items")
               .handler(routingContext -> requestExecutor.processRequest(() -> createItemProcessor.process(routingContext)));
        router.get("/items")
                .handler(routingContext -> requestExecutor.processRequest(() -> findItemProcessor.process(routingContext)));
    }
}
