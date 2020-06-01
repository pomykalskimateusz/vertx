package pl.vertx.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class AuthenticationService {
    private final JWTAuth authProvider;

    public AuthenticationService(JWTAuth authProvider) {
        this.authProvider = authProvider;
    }

    public String provideToken(UUID id, String login) {
        return authProvider.generateToken(jsonFor(id, login));
    }

    public void authenticate(String token, Consumer<Optional<UUID>> function) {
        authProvider.authenticate(jsonFor(token), (result) -> function.accept(processAuthenticationResult(result)));
    }

    private Optional<UUID> processAuthenticationResult(AsyncResult<User> result) {
        if(result.succeeded()) {
            JsonObject authUser = result.result().principal();
            return Optional
                    .ofNullable(authUser.getString("id"))
                    .map(UUID::fromString);
        } else return Optional.empty();
    }

    private JsonObject jsonFor(UUID id, String login) {
        return new JsonObject()
                .put("id", id.toString())
                .put("login", login);
    }

    private JsonObject jsonFor(String token) {
        return new JsonObject()
                .put("jwt", token);
    }
}
