package com.kakuiwong.service.encryService.impl;

import com.kakuiwong.service.encryService.EncryptHandler;
import org.springframework.util.Base64Utils;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public class Base64EncryptHandler implements EncryptHandler {
    @Override
    public byte[] encode(byte[] b) {
        return Base64Utils.encode(b);
    }

    @Override
    public byte[] decode(byte[] b) {
        return Base64Utils.decode(b);
    }
}
