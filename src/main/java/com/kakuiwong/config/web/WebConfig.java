package com.kakuiwong.config.web;

import com.kakuiwong.config.servlet.EncryptFilter;
import com.kakuiwong.service.encryService.EncryptHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class WebConfig {

    @Autowired(required = false)
    private EncryptHandler encryptService;
    @Autowired
    Environment environment;


    @Bean
    @Conditional(DefaultCondition.class)
    public FilterRegistrationBean encryptFilter() {
        Integer order = environment.getProperty("encrypt.order", Integer.class);
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new EncryptFilter(encryptService));
        bean.addUrlPatterns("/*");
        bean.setName("encryptFilter");
        bean.setOrder(order == null ? 0 : order);
        return bean;
    }

    static class DefaultCondition implements Condition {
        @Override
        public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
            Environment environment = conditionContext.getEnvironment();
            Boolean debug = environment.getProperty("encrypt.debug", Boolean.class);
            return (debug == null || !debug) ? true : false;
        }
    }
}
