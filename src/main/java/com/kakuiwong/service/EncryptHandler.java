package com.kakuiwong.service;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 * 加密处理器
 */
public interface EncryptHandler {

    byte[] encode(byte[] content);


    byte[] decode(byte[] content);
}
