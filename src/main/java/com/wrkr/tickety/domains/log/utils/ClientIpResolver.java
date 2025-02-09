package com.wrkr.tickety.domains.log.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientIpResolver {

    private static final List<String> IP_HEADERS = List.of(
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_CLIENT_IP",
        "HTTP_X_FORWARDED_FOR",
        "X-Real-IP",
        "REMOTE_ADDR"
    );

    public String getClientIp(HttpServletRequest request) {
        log.info("클라이언트 IP 추출 시작");

        for (String header : IP_HEADERS) {
            String ip = request.getHeader(header);
            log.debug("헤더 {}: {}", header, ip);

            if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
                String clientIp = ip.split(",")[0].trim();
                log.info("클라이언트 IP 확인: {}", clientIp);
                return clientIp;
            }
        }

        String remoteAddr = request.getRemoteAddr();
        log.info("헤더에서 IP를 찾지 못함. 원격 주소 반환: {}", remoteAddr);
        return remoteAddr;
    }
}
