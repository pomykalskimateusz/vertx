package pl.vertx.common;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class EncryptionService {
    private static final String keyFileName = "key";

    private final Cipher cipher;
    private final SecretKey secretKey;

    public EncryptionService() throws Exception {
        secretKey = new SecretKeySpec(readKeyBytes(), "DES");
        cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    }

    public String encrypt(String value) throws Exception {
        byte[] valueBytes = value.getBytes();
        byte[] encryptedBytes = cipher.doFinal(valueBytes);
        byte[] encodedBase64Bytes = BASE64EncoderStream.encode(encryptedBytes);

        return new String(encodedBase64Bytes);
    }

    private byte[] readKeyBytes() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Path path = Paths.get(classLoader.getResource(keyFileName).toURI());

        return Files.readAllBytes(path);
    }
}
