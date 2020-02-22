package com.kakuiwong.config.servlet;

import com.kakuiwong.service.encryService.EncryptHandler;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author gaoyang
 * @email 785175323@qq.com
 */
public class EncryptRequestWrapper extends HttpServletRequestWrapper {
    private byte[] body;
    private EncryptHandler encryptService;

    public EncryptRequestWrapper(HttpServletRequest request, EncryptHandler encryptService) throws IOException, ServletException {
        super(request);
        this.encryptService = encryptService;
        if (request.getContentType() == null ||
                (!request.getContentType().toLowerCase().equals(MediaType.APPLICATION_JSON_VALUE) &&
                        !request.getContentType().toLowerCase().equals(MediaType.APPLICATION_JSON_UTF8_VALUE.toLowerCase()))) {
            throw new ServletException("contentType error,Only application/json is supported ");
        }
        ServletInputStream inputStream = request.getInputStream();
        String header = request.getHeader("Content-Length");
        if (header != null) {
            return;
        }
        int contentLength = Integer.valueOf(header);
        byte[] bytes = new byte[contentLength];
        int readCount = 0;
        while (readCount < contentLength) {
            readCount += inputStream.read(bytes, readCount, contentLength - readCount);
        }
        body = bytes;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        byte[] decode = encryptService.decode(body);
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
