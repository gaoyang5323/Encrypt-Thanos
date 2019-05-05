package com.kakuiwong.service.impl;

import com.kakuiwong.service.EncryptHandler;
import lombok.Setter;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
@Setter
public class AesEncryptHandler implements EncryptHandler {

    private String secret;

    @Override
    public byte[] encode(byte[] b) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128, new SecureRandom(secret.getBytes()));
            SecretKey original_key = keygen.generateKey();
            byte[] raw = original_key.getEncoded();
            SecretKey key = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] byte_AES = cipher.doFinal(b);
            return Base64Utils.encode(byte_AES);
        } catch (Exception e) {
        }
        return new byte[0];
    }

    @Override
    public byte[] decode(byte[] b) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128, new SecureRandom(secret.getBytes()));
            SecretKey original_key = keygen.generateKey();
            byte[] raw = original_key.getEncoded();
            SecretKey key = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] byte_content = Base64Utils.decode(b);
            byte[] byte_decode = cipher.doFinal(byte_content);
            return byte_decode;
        } catch (Exception e) {
        }
        return new byte[0];
    }
}
