package com.alpha.omega.user.batch;

import com.alpha.omega.user.repository.UserContextEntity;
import com.alpha.omega.user.repository.UserEntity;

import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static com.alpha.omega.user.utils.Constants.COLON;

public class BatchUtil {

    /* Generating Secret key */
    final static String ALGORITHM = "AES";
    final static Integer KEY_LENGTH = 256;

    // Generating Secret Key using KeyGenerator class with 256
    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(n);
        SecretKey originalKey = keyGenerator.generateKey();
        return originalKey;
    }

    // Generating Secret Key using password and salt
    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, KEY_LENGTH);
        SecretKey originalKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
        return originalKey;
    }

    /* Converting Secret key into String */
    public static String convertSecretKeyToString(SecretKey secretKey) throws NoSuchAlgorithmException {
        // Converting the Secret Key into byte array
        byte[] rawData = secretKey.getEncoded();
        // Getting String - Base64 encoded version of the Secret Key
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        return encodedKey;
    }

    /* Converting String into Secret key into */
    public static SecretKey convertStringToSecretKeyto(String encodedKey) {
        // Decoding the Base64 encoded string into byte array
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        // Rebuilding the Secret Key using SecretKeySpec Class
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
        return originalKey;
    }

    public static String generateSecretKeyString(String password, String salt){

        String keyVal = null;
        try {
            SecretKey key = getKeyFromPassword(password, salt);
            keyVal = convertSecretKeyToString(key);
        } catch (Exception e) {
            throw new UserBatchException("Could not create key",e);
        }
        return keyVal;
    }

    public static String basicAuthCredsFrom(String username, String password){
        return Base64.getEncoder().encodeToString(new StringBuilder(username)
                .append(COLON)
                .append(password)
                .toString().getBytes(Charset.defaultCharset()));
    }

    public static Function<UserLoad, UserEntity> defaultUserLoadUserEntityFunction(){
        return userLoad -> {
            UserEntity userEntity = new UserEntity();
            userEntity.setFirstName(userLoad.getFirst());
            userEntity.setLastName(userLoad.getLast());
            userEntity.setMailCode(userLoad.getPostcode());
            userEntity.setEmail(userLoad.getEmail());
            userEntity.setCountry(userLoad.getCountry());
            return userEntity;
        };
    }

    public static Function<UserLoad, UserContextEntity> defaultUserLoadUserContextEntityFunction() {
        return userLoad -> {
            UserContextEntity userContextEntity = new UserContextEntity();
            userContextEntity.setContextId(userLoad.getContextId());
            userContextEntity.setUserId(userLoad.getEmail());
            userContextEntity.setRoleId(userLoad.getRole());
            return userContextEntity;
        };
    }

    public static final String getStringArrayValue(String array[], int index){
        String val = null;
        try{
            val = array[index];
        } catch (ArrayIndexOutOfBoundsException aioe){

        }
        return val;
    }
}