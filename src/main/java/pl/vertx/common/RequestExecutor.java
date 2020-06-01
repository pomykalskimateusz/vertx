package pl.vertx.common;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RequestExecutor {
    private final Executor executor;

    public RequestExecutor(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize);
    }

    public void processRequest(Runnable runnable) {
        executor.execute(runnable);
    }
}
