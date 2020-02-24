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
import java.util.concurrent.atomic.LongAdder;

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
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (isEncryptAnnotation.get()) {
            if (checkUri(httpServletRequest.getRequestURI())) {
                this.chain(httpServletRequest, servletResponse, filterChain);
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            this.chain(httpServletRequest, servletResponse, filterChain);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
        InitHandler.handler(filterConfig, encryptCacheUri, isEncryptAnnotation);
        print();
    }

    @Override
    public void destroy() {

    }

    private void print() {
        System.out.println("___________                                   __            ___________.__                                \n" +
                "\\_   _____/ ____   ___________ ___.__._______/  |_          \\__    ___/|  |__ _____    ____   ____  ______\n" +
                " |    __)_ /    \\_/ ___\\_  __ <   |  |\\____ \\   __\\  ______   |    |   |  |  \\\\__  \\  /    \\ /  _ \\/  ___/\n" +
                " |        \\   |  \\  \\___|  | \\/\\___  ||  |_> >  |   /_____/   |    |   |   Y  \\/ __ \\|   |  (  <_> )___ \\ \n" +
                "/_______  /___|  /\\___  >__|   / ____||   __/|__|             |____|   |___|  (____  /___|  /\\____/____  >\n" +
                "        \\/     \\/     \\/       \\/     |__|                                  \\/     \\/     \\/           \\/ ");
        if (!isEncryptAnnotation.get()) {
            System.out.println("Global encryption turned on");
            return;
        }
        if (isEncryptAnnotation.get() && encryptCacheUri.size() > 0) {
            System.out.println("Local encryption turned on:");
            LongAdder adder = new LongAdder();
            encryptCacheUri.stream().forEach(url -> {
                System.out.print(url + "   ");
                if (adder.intValue() == 10) {
                    System.out.println();
                    adder.reset();
                }
                adder.increment();
            });
            System.out.println();
        }
    }

    private boolean checkUri(String uri) {
        uri = uri.replaceAll("//+", "/");
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }
        if (encryptCacheUri.contains(uri)) {
            return true;
        }
        return false;
    }

    private void chain(HttpServletRequest httpServletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = EncryptRequestWrapperFactory.getWrapper(httpServletRequest, encryptService);
        EncryptResponseWrapper response = new EncryptResponseWrapper((HttpServletResponse) servletResponse);
        filterChain.doFilter(request, response);
        byte[] responseData = response.getResponseData();
        if (responseData.length > 0) {
            byte[] encode = encryptService.encode(responseData);
            servletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            ServletOutputStream outputStream = servletResponse.getOutputStream();
            outputStream.write(encode);
            outputStream.flush();
        }
    }
}
