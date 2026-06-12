package esfe.utils;

import javax.lang.model.util.SimpleTypeVisitor14;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordHasher {
    public static String hashPassword(String password)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashByte = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hashByte);
        }
        catch (NoSuchAlgorithmException ex)
        {
            return null;
        }
    }
}
