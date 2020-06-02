package pl.vertx.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;

import java.time.LocalDateTime;
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

            if(isExpirationDateValid(authUser)) {
                return Optional
                        .ofNullable(authUser.getString("id"))
                        .map(UUID::fromString);
            } else return Optional.empty();
        } else return Optional.empty();
    }

    private Boolean isExpirationDateValid(JsonObject authUser) {
        String expirationDate = authUser.getString("expiration");

        return parseDate(expirationDate)
                .map(date -> LocalDateTime.now().isBefore(date))
                .orElseGet(() -> false);
     }

     private Optional<LocalDateTime> parseDate(String date) {
        try {
            return Optional.of(LocalDateTime.parse(date));
        } catch (Exception ex) {
            return Optional.empty();
        }
     }

    private JsonObject jsonFor(UUID id, String login) {
        return new JsonObject()
                .put("id", id.toString())
                .put("login", login)
                .put("expiration", LocalDateTime.now().plusMinutes(30).toString());
    }

    private JsonObject jsonFor(String token) {
        return new JsonObject()
                .put("jwt", token);
    }
}
