package com.kakuiwong.config.servlet;

import com.kakuiwong.service.EncryptHandler;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public class EncryptRequestWrapper extends HttpServletRequestWrapper {
    private String body;
    private EncryptHandler encryptService;

    public EncryptRequestWrapper(HttpServletRequest request, EncryptHandler encryptService) throws IOException {
        super(request);
        this.encryptService = encryptService;
        BufferedReader reader = request.getReader();
        body = "";
        String line;
        while ((line = reader.readLine()) != null) {
            body += line;
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        byte[] decode = encryptService.decode(body.getBytes());
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decode);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }
}
