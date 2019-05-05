package com.kakuiwong.config.servlet;

import com.kakuiwong.service.EncryptHandler;
import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public class EncryptFilter implements Filter {

    private EncryptHandler encryptService;

    public EncryptFilter(EncryptHandler encryptService) {
        this.encryptService = encryptService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        EncryptRequestWrapper request = new EncryptRequestWrapper((HttpServletRequest) servletRequest, encryptService);
        EncryptResponseWrapper response = new EncryptResponseWrapper((HttpServletResponse) servletResponse);
        filterChain.doFilter(request, response);
        byte[] responseData = response.getResponseData();
        servletResponse.setContentLength(-1);
        servletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        servletResponse.getOutputStream().write(encryptService.encode(responseData));
    }

    @Override
    public void destroy() {

    }
}
