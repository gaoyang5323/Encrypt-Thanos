package com.kakuiwong.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "encrypt")
public class EncryptConfigBean {
    /**
     * 加密类型
     */
    @NestedConfigurationProperty
    private EncryptType type;
    /**
     * 秘钥
     */
    private String secret;
}
