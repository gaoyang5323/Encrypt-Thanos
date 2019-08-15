package com.kakuiwong.service.encryService.impl;

import com.kakuiwong.bean.RsaKeyEntity;
import com.kakuiwong.exception.EncryptException;
import com.kakuiwong.service.encryService.EncryptHandler;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public class RsaEncryptHandler implements EncryptHandler {

    private static final String KEY_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 512;
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    private String publicKey;
    private String privateKey;

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public byte[] encode(byte[] content) {
        try {
            byte[] bytes = encryptByPrivateKey(content, Base64Utils.decodeFromString(privateKey));
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EncryptException("rsa加密错误", e);
        }
    }

    @Override
    public byte[] decode(byte[] content) {
        try {
            byte[] bytes = decryptByPrivateKey(content, Base64Utils.decodeFromString(privateKey));
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EncryptException("rsa解密错误", e);
        }
    }

    public static RsaKeyEntity getRsaKeys() throws Exception {
        Map<String, Object> keyMap = initKey();
        byte[] publicKey = getPublicKey(keyMap);
        byte[] privateKey = getPrivateKey(keyMap);
        RsaKeyEntity rsaKeyEntity = new RsaKeyEntity();
        rsaKeyEntity.setPublicKey(Base64Utils.encodeToString(publicKey));
        rsaKeyEntity.setPrivateKey(Base64Utils.encodeToString(privateKey));
        return rsaKeyEntity;
    }

    private static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;

    }

    private static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        if (data.length == 0) {
            return new byte[0];
        }
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    private static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    private static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        if (data.length == 0) {
            return new byte[0];
        }
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    private static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        return cipher.doFinal(data);
    }

    private static byte[] getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }

    private static byte[] getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }
}
