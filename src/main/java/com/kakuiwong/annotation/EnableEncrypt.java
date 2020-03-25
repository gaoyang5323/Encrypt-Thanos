package com.kakuiwong.annotation;

import com.kakuiwong.EncryptInit;
import com.kakuiwong.config.aop.SortSignEncryptConfig;
import com.kakuiwong.config.web.WebConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({EncryptInit.class,
        WebConfig.class,
        SortSignEncryptConfig.class})
public @interface EnableEncrypt {
}
