package com.kakuiwong.exception;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public class EncryptException extends RuntimeException {

    public EncryptException() {
        super();
    }

    public EncryptException(String message) {
        super(message);
    }

    public EncryptException(String message, Throwable t) {
        super(message, t);
    }
}
