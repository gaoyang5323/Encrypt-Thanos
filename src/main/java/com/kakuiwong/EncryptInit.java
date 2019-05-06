package com.kakuiwong;

import com.kakuiwong.bean.EncryptConfigBean;
import com.kakuiwong.bean.EncryptType;
import com.kakuiwong.exception.EncryptException;
import com.kakuiwong.service.EncryptHandler;
import com.kakuiwong.service.impl.AesEncryptHandler;
import com.kakuiwong.service.impl.Base64EncryptHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 * 初始化
 */
@EnableConfigurationProperties(value = {EncryptConfigBean.class})
@Configuration
@EnableAutoConfiguration
public class EncryptInit implements ApplicationContextAware, BeanFactoryPostProcessor, EnvironmentAware {

    public static ApplicationContext applicationContext;
    public static Environment environment;
    @Resource
    EncryptConfigBean encryptConfigBean;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) configurableListableBeanFactory;
        GenericBeanDefinition bean = new GenericBeanDefinition();
        EncryptType type = environment.getProperty("encrypt.type", EncryptType.class);
        String secret = environment.getProperty("encrypt.secret", String.class);
        if (type == null) {
            throw new EncryptException("没有定义加密类型(No encryption type is defined)");
        }
        switch (type) {
            case BASE64:
                bean.setBeanClass(Base64EncryptHandler.class);
                beanFactory.registerBeanDefinition("encryptHandler", bean);
                break;
            case AES:
                if (secret == null || "".equals(secret.trim())) {
                    throw new EncryptException("没有定义秘钥(No secret key is defined)");
                }
                bean.setBeanClass(AesEncryptHandler.class);
                bean.getPropertyValues().add("secret", secret);
                beanFactory.registerBeanDefinition("encryptHandler", bean);
                break;
            case CUSTOM:
                try {
                    beanFactory.getBean(EncryptHandler.class);
                } catch (Exception e) {
                    throw new EncryptException("没有自定义加密处理器(No custom encryption processor)");
                }
        }

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
