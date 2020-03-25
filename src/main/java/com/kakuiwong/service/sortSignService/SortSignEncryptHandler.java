package com.kakuiwong.service.sortSignService;

import com.kakuiwong.exception.EncryptException;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public interface SortSignEncryptHandler {

    public Object handle(Object proceed, long timeout, TimeUnit timeUnit, String sortSignSecret,
                         Map<Object, Object> jsonMap) throws EncryptException;
}
