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
        if (contentType.equals(MediaType.APPLICATION_JSON_VALUE.toLowerCase()) ||
                contentType.equals(MediaType.APPLICATION_JSON_UTF8_VALUE.toLowerCase())) {
            return new EncryptBodyRequestWrapper(request, encryptService);
        }
        return request;
    }
}
