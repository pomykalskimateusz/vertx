package pl.vertx;

import java.util.Base64;

public class EncryptionService {
    public String encrypt(String value) {
        return Base64
                .getEncoder()
                .encodeToString(value.getBytes());
    }
}
