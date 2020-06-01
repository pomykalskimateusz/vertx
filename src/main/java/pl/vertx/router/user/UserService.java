package pl.vertx.router.user;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import pl.vertx.repository.user.User;
import pl.vertx.repository.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void ifUserExists(String login, Consumer<Boolean> function) {
        userRepository.findByLogin(login, (result) -> function.accept(!isNotExistsIn(result)));
    }

    public void ifUserExists(String login, String password, Consumer<Optional<User>> function) {
        userRepository.findByLoginAndPassword(login, password, (result) -> function.accept(userFromResult(result)));
    }

    private Optional<User> userFromResult(AsyncResult<JsonObject> result) {
        return Optional
                .ofNullable(result.result())
                .map(User::fromJson);
    }

    public void createUser(String login, String password, Consumer<String> function) {
        userRepository.createUser(login, password, (result) -> function.accept(result.result()));
    }

    private boolean isNotExistsIn(AsyncResult<List<JsonObject>> result) {
        return result
                .result()
                .isEmpty();
    }
}
