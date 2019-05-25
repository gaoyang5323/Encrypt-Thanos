package com.kakuiwong.bean;


/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public enum EncryptType {
    BASE64("base64"),
    CUSTOM("自定义"),
    AES("对称加密,需指定秘钥"),
    RSA("非对称加密,需指定公钥和私钥");

    private String describe;

    EncryptType(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
