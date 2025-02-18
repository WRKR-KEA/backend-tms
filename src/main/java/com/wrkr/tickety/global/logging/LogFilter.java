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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws IOException, ServletException {

        String uri = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);

        if (uri.startsWith("/api/tickety-tms") || uri.endsWith("/sse/subscribe")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String ip = requestWrapper.getRemoteAddr();

        MDC.put("request_ip", ip);
        MDC.put("request_timestamp", Instant.ofEpochMilli(System.currentTimeMillis()).toString());

        long start = System.currentTimeMillis();

        filterChain.doFilter(requestWrapper, responseWrapper);

        long end = System.currentTimeMillis();
        long ms = end - start;

        String responseContent = new String(responseWrapper.getContentAsByteArray());
        responseWrapper.copyBodyToResponse();

        MDC.put("type", "api");
        MDC.put("value.total_ms", Long.toString(ms));

        MDC.put("value.request.method", requestWrapper.getMethod());
        MDC.put("value.request.url", uri);
        MDC.put("value.request.params", getRequestParams(requestWrapper));
        MDC.put("value.request.body", getRequestBody(requestWrapper));

        MDC.put("value.response.status", Integer.toString(responseWrapper.getStatus()));
        try {
            ApplicationResponse<Object> applicationResponse = objectMapper.readValue(responseContent, new TypeReference<>() {
            });
            MDC.put("value.response.code", applicationResponse.getCode());
            MDC.put("value.response.message", applicationResponse.getMessage());
            MDC.put("value.response.result", getResponseResult(applicationResponse));

        } catch (JsonProcessingException e) {
            MDC.put("value.response.body", responseContent);
        }

        log.info("Api Log");

        MDC.clear();
    }

    private String getRequestParams(ContentCachingRequestWrapper request) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();
            String[] rawValue = entry.getValue();
            String value = rawValue.length == 1 ? Arrays.asList(rawValue).getFirst() : Arrays.toString(rawValue);

            map.put(key, value);
        }
        return objectMapper.writeValueAsString(map);
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        return new String(request.getContentAsByteArray());
    }

    private String getResponseResult(ApplicationResponse<?> applicationResponse) throws JsonProcessingException {
        Object result = applicationResponse.getResult();
        if (result instanceof Map || result instanceof List) {
            return objectMapper.writeValueAsString(applicationResponse.getResult());
        } else {
            return String.valueOf(result);
        }
    }
}
