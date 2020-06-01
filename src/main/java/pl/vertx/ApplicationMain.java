package pl.vertx;

import io.vertx.core.Launcher;

public class ApplicationMain {
    public static void main(String[] args) {
        Launcher.executeCommand("run", Server.class.getName());
    }
}
