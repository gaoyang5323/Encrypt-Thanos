package com.kakuiwong.config.web;

import com.kakuiwong.config.servlet.EncryptFilter;
import com.kakuiwong.service.EncryptHandler;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class WebConfig {

    @Resource
    private EncryptHandler encryptService;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new EncryptFilter(encryptService));
        bean.addUrlPatterns("/*");
        bean.setName("encryptFilter");
        bean.setOrder(0);
        return bean;
    }
}
