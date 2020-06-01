package pl.vertx.router.item.processor;

import io.vertx.core.http.HttpServerRequest;

class ItemProcessorUtil {
    static String extractToken(HttpServerRequest request) {
        return request
                .getHeader("Authorization")
                .split("Bearer ")[1];
    }

    static boolean isAuthorizationHeaderValid(HttpServerRequest request) {
        String header = request.getHeader("Authorization");

        if(header == null) return false;
        else if(header.startsWith("Bearer ")) return true;
        else return false;
    }

    static boolean isNameValid(String name) {
        if(name == null) return false;
        else if(name.isEmpty()) return false;
        else return true;
    }
}
