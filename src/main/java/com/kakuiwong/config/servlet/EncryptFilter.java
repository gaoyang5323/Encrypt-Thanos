package com.kakuiwong.config.servlet;

import com.kakuiwong.service.encryService.EncryptHandler;
import com.kakuiwong.service.initService.InitHandler;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public class EncryptFilter implements Filter {

    private EncryptHandler encryptService;
    private static AtomicBoolean isEncryptAnnotation = new AtomicBoolean(false);
    private final static Set<String> encryptCacheUri = new HashSet<>();

    public EncryptFilter(EncryptHandler encryptService) {
        this.encryptService = encryptService;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (this.isEncryptAnnotation.get()) {
            if (checkUri(((HttpServletRequest) servletRequest).getRequestURI())) {
                this.chain(servletRequest, servletResponse, filterChain);
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            this.chain(servletRequest, servletResponse, filterChain);
        }
    }

    private boolean checkUri(String uri) {
        uri = uri.replaceAll("//+", "/");
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        if (this.encryptCacheUri.contains(uri)) {
            return true;
        }
        return false;
    }


    private void chain(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        EncryptRequestWrapper request = new EncryptRequestWrapper((HttpServletRequest) servletRequest, encryptService);
        EncryptResponseWrapper response = new EncryptResponseWrapper((HttpServletResponse) servletResponse);
        filterChain.doFilter(request, response);
        byte[] responseData = response.getResponseData();
        servletResponse.setContentLength(-1);
        servletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        ServletOutputStream outputStream = servletResponse.getOutputStream();
        outputStream.write(encryptService.encode(responseData));
        outputStream.flush();
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        InitHandler.handler(filterConfig, encryptCacheUri, isEncryptAnnotation);
    }


    @Override
    public void destroy() {

    }


}
