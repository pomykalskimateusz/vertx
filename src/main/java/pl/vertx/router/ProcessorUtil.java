package pl.vertx.router;

import io.vertx.core.http.HttpServerRequest;

public class ProcessorUtil {
    public static String extractToken(HttpServerRequest request) {
        return request
                .getHeader("Authorization")
                .split("Bearer ")[1];
    }

    public static boolean isContentTypeHeaderValid(HttpServerRequest request) {
        String header = request.getHeader("Content-Type");

        if(header == null) return false;
        else if(header.equals("application/json")) return true;
        else return false;
    }

    public static boolean isAuthorizationHeaderValid(HttpServerRequest request) {
        String header = request.getHeader("Authorization");

        if(header == null) return false;
        else if(header.startsWith("Bearer ")) return true;
        else return false;
    }

    public static boolean isDataValid(String login, String password) {
        if(login == null || password == null) return false;
        else if(login.isEmpty() || password.isEmpty()) return false;
        else return true;
    }

    public static boolean isNameValid(String name) {
        if(name == null) return false;
        else if(name.isEmpty()) return false;
        else return true;
    }
}
