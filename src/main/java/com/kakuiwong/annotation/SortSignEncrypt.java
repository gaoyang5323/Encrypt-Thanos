package com.kakuiwong.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SortSignEncrypt {

    long timeout() default 60000L;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
