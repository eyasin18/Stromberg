package de.repictures.stromberg.uiHelper;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class Cryptor {

    public byte[] encrypt(String input, byte[] key){
        try{
            Cipher cipher = Cipher.getInstance("AES"); //Cipher Objekt wird erzeugt. Wir wollen auf AES verschlüsseln.
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES"); //bytearray wir zum "SecretKey" gemacht (key [benutzter Schlüssel als bytearray], offset, [wie lang unser Schlüssel sein soll], algorithm [welchen verschlüsselungsargorythums verwenden wir?])
            cipher.init(Cipher.ENCRYPT_MODE, originalKey); //Cipher wird initialisiert
            return cipher.doFinal(input.getBytes("UTF-8")); //Input wird verschlüsselt
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e){
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(byte[] encryptedInput, byte[] key){
        try {
            Cipher cipher = Cipher.getInstance("AES"); //Cipher Objekt wird erzeugt. Wir wollen auf AES entschlüsseln.
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES"); //bytearray wir zum "SecretKey" gemacht (key [benutzter Schlüssel als bytearray], offset, [wie lang unser Schlüssel sein soll], algorithm [welchen verschlüsselungsargorythums verwenden wir?])
            cipher.init(Cipher.DECRYPT_MODE, originalKey); //cipher wird initialisiert
            byte[] decryptedBytes = cipher.doFinal(encryptedInput); //input wird entschlüsselt
            return new String(decryptedBytes); //bytearray wird zum string gemacht
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Sinnlose methode die wir vielleicht mal wieder gebrauchen könnten. Ich vermute dass hier der Punkt von der Kreisbahn abgelesen wird.
    private AlgorithmParameterSpec getIV(Cipher cipher){
        byte[] iv = new byte[cipher.getBlockSize()];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private byte[] hashKey = "ppies2QU5r2zvm3ezE0G".getBytes();//Veralteter Schlüssel zum Hashen.

    //Veraltete Hashmethode
    public byte[] manualHash(String input) {
        try {
            KeySpec spec = new PBEKeySpec(input.toCharArray(), hashKey, 65536, 256);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return f.generateSecret(spec).getEncoded();
        }catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }
}
