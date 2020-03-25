package com.kakuiwong.config.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakuiwong.annotation.SortSignEncrypt;
import com.kakuiwong.config.servlet.CacheRequestWrapper;
import com.kakuiwong.config.servlet.EncryptRequestWrapperFactory;
import com.kakuiwong.service.sortSignService.SortSignEncryptHandler;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public class SortSignEncryptInterceptor implements MethodInterceptor {

    private String sortSignSecret;
    private SortSignEncryptHandler sortSignEncryptHandler;

    public SortSignEncryptInterceptor(String sortSignSecret, SortSignEncryptHandler sortSignEncryptHandler) {
        this.sortSignSecret = sortSignSecret;
        this.sortSignEncryptHandler = sortSignEncryptHandler;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object proceed = methodInvocation.proceed();
        CacheRequestWrapper request = (CacheRequestWrapper) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (!"POST".equalsIgnoreCase(request.getMethod()) ||
                !EncryptRequestWrapperFactory.contentIsJson(request.getContentType())) {
            return proceed;
        }
        SortSignEncrypt annotation = methodInvocation.getMethod().getAnnotation(SortSignEncrypt.class);
        long timeout = annotation.timeout();
        TimeUnit timeUnit = annotation.timeUnit();
        if (((CacheRequestWrapper) request).getBody().length < 1) {
            return proceed;
        }
        Map<Object, Object> jsonMap = new ObjectMapper().readValue(request.getBody(), Map.class);
        return sortSignEncryptHandler.handle(proceed, timeout, timeUnit, sortSignSecret, jsonMap);
    }
}
