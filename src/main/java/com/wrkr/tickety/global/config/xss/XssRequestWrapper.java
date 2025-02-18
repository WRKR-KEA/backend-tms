package com.wrkr.tickety.global.config.xss;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.io.IOUtils;


public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] rawData;

    public XssRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        if ("POST".equalsIgnoreCase(request.getMethod()) ||
            "PUT".equalsIgnoreCase(request.getMethod()) ||
            "PATCH".equalsIgnoreCase(request.getMethod())) {
            String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
            body = XssSanitizer.sanitize(body);
            if (body.isBlank()) {
                body = "{\"content\":\"[Filtered!]\"}";
            }
            this.rawData = body.getBytes(StandardCharsets.UTF_8);
        } else {
            this.rawData = null;
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        if (this.rawData == null) {
            try {
                return super.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.rawData);

        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

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
        };
    }

    @Override
    public String getQueryString() {
        return XssSanitizer.sanitize(super.getQueryString());
    }

    @Override
    public String getParameter(String name) {
        String original = super.getParameter(name);
        return XssSanitizer.sanitize(original);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }

        for (int i = 0; i < values.length; i++) {
            values[i] = XssSanitizer.sanitize(values[i]);
        }

        return values;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        parameterMap.forEach((key, value) -> {
            for (int i = 0; i < value.length; i++) {
                value[i] = XssSanitizer.sanitize(value[i]);
            }
        });

        return parameterMap;
    }
}
