package com.wrkr.tickety.global.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.global.response.ApplicationResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
@Order(-10000)
public class LogFilter extends OncePerRequestFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws IOException, ServletException {

        long start = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String ip = requestWrapper.getRemoteAddr();

        MDC.put("request_ip", ip);
        MDC.put("request_timestamp", Instant.ofEpochMilli(System.currentTimeMillis()).toString());

        filterChain.doFilter(requestWrapper, responseWrapper);

        long end = System.currentTimeMillis();
        long ms = end - start;

        ApplicationResponse<Object> applicationResponse = objectMapper.readValue(responseWrapper.getContentInputStream(), new TypeReference<>() {});
        responseWrapper.copyBodyToResponse();

        String uri = URLDecoder.decode(requestWrapper.getRequestURI(), StandardCharsets.UTF_8);

        MDC.put("type", "api");
        MDC.put("value.total_ms", Long.toString(ms));

        MDC.put("value.request.method", requestWrapper.getMethod());
        MDC.put("value.request.url", uri);
        MDC.put("value.request.params", getParams(requestWrapper));
        MDC.put("value.request.body", getBody(requestWrapper));

        MDC.put("value.response.status", Integer.toString(responseWrapper.getStatus()));
        MDC.put("value.response.code", applicationResponse.getCode());
        MDC.put("value.response.message", applicationResponse.getMessage());

        Object result = applicationResponse.getResult();
        if (result instanceof Map || result instanceof List) {
            MDC.put("value.response.result",
                objectMapper.writeValueAsString(applicationResponse.getResult()));
        } else {
            MDC.put("value.response.result", String.valueOf(result));
        }

        log.info("Api Log");

        MDC.remove("type");
        MDC.remove("value.total_ms");

        MDC.remove("value.request.method");
        MDC.remove("value.request.url");
        MDC.remove("value.request.params");
        MDC.remove("value.request.body");

        MDC.remove("value.response.status");
        MDC.remove("value.response.code");
        MDC.remove("value.response.message");
        MDC.remove("value.response.result");

        MDC.remove("request_ip");
        MDC.remove("request_timestamp");
    }

    private String getParams(ContentCachingRequestWrapper request) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] rawValue = entry.getValue();
            String value = rawValue.length == 1 ? Arrays.asList(rawValue).getFirst() : Arrays.toString(rawValue);

            map.put(key, value);
        }
        return objectMapper.writeValueAsString(map);
    }

    private static String getBody(ContentCachingRequestWrapper request) {
        return new String(request.getContentAsByteArray());
    }
}
