package com.kakuiwong.config.servlet;

import com.kakuiwong.service.encryService.EncryptHandler;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public class EncryptRequestWrapperFactory {

    public static HttpServletRequest getWrapper(HttpServletRequest request,
                                                EncryptHandler encryptService) throws IOException, ServletException {
        String contentType = request.getContentType();
        int contentLength = request.getContentLength();
        if (contentType == null || contentLength == 0) {
            return request;
        }
        contentType = contentType.toLowerCase();
        if (contentIsJson(contentType)) {
            return new EncryptBodyRequestWrapper(request, encryptService);
        }
        return request;
    }

    public static HttpServletRequest getCacheWarpper(HttpServletRequest request) throws IOException, ServletException {
        if (!"POST".equalsIgnoreCase(request.getMethod()) ||
                !contentIsJson(request.getContentType())) {
            return request;
        }
        return new CacheRequestWrapper(request);
    }

    public static boolean contentIsJson(String contentType) {
        return contentType.equals(MediaType.APPLICATION_JSON_VALUE.toLowerCase()) ||
                contentType.equals(MediaType.APPLICATION_JSON_UTF8_VALUE.toLowerCase());
    }
}
