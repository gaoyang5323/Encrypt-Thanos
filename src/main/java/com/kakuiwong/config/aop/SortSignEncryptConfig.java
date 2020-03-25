package com.kakuiwong.config.aop;


import com.kakuiwong.annotation.SortSignEncrypt;
import com.kakuiwong.service.sortSignService.SortSignEncryptHandler;
import com.kakuiwong.service.sortSignService.impl.SortSignEncryptHandlerImpl;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
@ConditionalOnExpression(value = "environment.getProperty('encrypt.sortSignSecret')!=null && " +
        "environment.getProperty('encrypt.sortSignSecret').trim()!=''")
public class SortSignEncryptConfig {

    @ConditionalOnMissingBean(SortSignEncryptHandler.class)
    @Bean
    public SortSignEncryptHandler SortSignEncryptHandlerDefult() {
        return new SortSignEncryptHandlerImpl();
    }

    @Autowired
    private SortSignEncryptHandler sortSignEncryptHandler;


    @Bean
    public DefaultPointcutAdvisor sortSignEncryptAdvisor(@Value("${encrypt.sortSignSecret}") String sortSignSecret) {
        SortSignEncryptInterceptor interceptor = new SortSignEncryptInterceptor(sortSignSecret, sortSignEncryptHandler);
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(null, SortSignEncrypt.class);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(interceptor);
        return advisor;
    }
}
