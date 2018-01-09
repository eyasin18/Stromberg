package de.repictures.stromberg.Helper;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Cryptor {
    private Logger log = Logger.getLogger(Cryptor.class.getName());

    public String hashToString(String input){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");//Wir wollen auf SHA-256 verschlüsseln
            messageDigest.update(input.getBytes()); //Input wird verschlüsselt
            return bytesToHex(messageDigest.digest()); //Hash wird als Hexadezimalzahl ausgegeben
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] hashToByte(String input){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");//Wir wollen auf SHA-256 verschlüsseln
            messageDigest.update(input.getBytes()); //Input wird verschlüsselt
            return messageDigest.digest(); //Hash wird als Hexadezimalzahl ausgegeben
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generateRandomAesKey(){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encryptSymmetricFromString(String input, byte[] key){
        try{
            Cipher cipher = Cipher.getInstance("AES"); //Cipher Objekt wird erzeugt. Wir wollen auf AES verschlüsseln.
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES"); //bytearray wir zum "SecretKey" gemacht (key [benutzter Schlüssel als bytearray], offset, [wie lang unser Schlüssel sein soll], algorithm [welchen verschlüsselungsargorythums verwenden wir?])
            cipher.init(Cipher.ENCRYPT_MODE, originalKey); //Cipher wird initialisiert
            return cipher.doFinal(input.getBytes("ISO-8859-1")); //Input wird verschlüsselt
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e){
            e.printStackTrace();
            return null;
        }
    }

    public byte[] encryptSymmetricFromByte(byte[] input, byte[] key){
        try{
            Cipher cipher = Cipher.getInstance("AES");                                                                                           //Cipher Objekt wird erzeugt. Wir wollen auf AES verschlüsseln.
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES");                                                //bytearray wir zum "SecretKey" gemacht (key [benutzter Schlüssel als bytearray], offset, [wie lang unser Schlüssel sein soll], algorithm [welchen verschlüsselungsargorythums verwenden wir?])
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);                                                                                       //Cipher wird initialisiert
            return cipher.doFinal(input);                                                                                                        //Input wird verschlüsselt
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e){
            e.printStackTrace();
            return null;
        }
    }

    public String decryptSymmetricToString(byte[] encryptedInput, byte[] key){
        try {
            Cipher cipher = Cipher.getInstance("AES"); //Cipher Objekt wird erzeugt. Wir wollen auf AES entschlüsseln.
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES"); //bytearray wir zum "SecretKey" gemacht (key [benutzter Schlüssel als bytearray], offset, [wie lang unser Schlüssel sein soll], algorithm [welchen verschlüsselungsargorythums verwenden wir?])
            cipher.init(Cipher.DECRYPT_MODE, originalKey); //cipher wird initialisiert
            byte[] decryptedBytes = cipher.doFinal(encryptedInput); //input wird entschlüsselt
            return new String(decryptedBytes); //bytearray wird zum string gemacht
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            log.warning(e.toString());
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decryptSymmetricToByte(byte[] encryptedInput, byte[] key){
        try {
            Cipher cipher = Cipher.getInstance("AES"); //Cipher Objekt wird erzeugt. Wir wollen auf AES entschlüsseln.
            SecretKey originalKey = new SecretKeySpec(key, 0, key.length, "AES"); //bytearray wir zum "SecretKey" gemacht (key [benutzter Schlüssel als bytearray], offset, [wie lang unser Schlüssel sein soll], algorithm [welchen verschlüsselungsargorythums verwenden wir?])
            cipher.init(Cipher.DECRYPT_MODE, originalKey); //cipher wird initialisiert
            return cipher.doFinal(encryptedInput); //input wird entschlüsselt
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            log.warning(e.toString());
            return null;
        }
    }

    public byte[] encryptAsymmetric(byte[] input, PublicKey publicKey){ // Verschlüsselt asymetrisch
        try {
            Provider provider = Security.getProvider("BC");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", provider);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(input);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] decryptAsymmetric(byte[] input, PrivateKey privateKey){ // Entschlüsselt asymetrisch
        try {
            Provider provider = Security.getProvider("BC");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", provider);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(input);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public KeyPair generateKeyPair(){ // Generiert Schlüsselpaar zur asymetrischen Verschlüsselung
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            SecureRandom secureRandom = new SecureRandom();
            kpg.initialize(2048, secureRandom);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PrivateKey stringToPrivateKey(String keyString) {
        try {
            byte[] clear = hexToBytes(keyString);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(keySpec);
            Arrays.fill(clear, (byte) 0);
            return privateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PrivateKey byteToPrivateKey(byte[] keyBytes) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(keySpec);
            Arrays.fill(keyBytes, (byte) 0);
            return privateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }


    public PublicKey stringToPublicKey(String keyString) {
        try {
            byte[] data = hexToBytes(keyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            return fact.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String privateKeyToString(PrivateKey privateKey) {
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec spec = fact.getKeySpec(privateKey,
                    PKCS8EncodedKeySpec.class);
            byte[] packed = spec.getEncoded();
            String keyString = bytesToHex(packed);
            Arrays.fill(packed, (byte) 0);
            return keyString;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String publicKeyToString(PublicKey publicKey) {
        try {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec spec = fact.getKeySpec(publicKey,
                    X509EncodedKeySpec.class);
            return bytesToHex(spec.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String generateAuthPin(int length){ // Generiert zufälligen String mit n Zeichen
        SecureRandom sr = new SecureRandom();
        return new BigInteger(130, sr).toString(32);
    }

    public String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        StringBuilder output = new StringBuilder();
        for (byte aByte : bytes) {
            int v = aByte & 0xFF;
            output.append(hexArray[v >>> 4]);
            output.append(hexArray[v & 0x0F]);
        }
        return output.toString();
    }

    public byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}