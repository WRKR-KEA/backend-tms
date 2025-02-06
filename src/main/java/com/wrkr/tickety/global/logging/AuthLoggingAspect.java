package com.wrkr.tickety.global.logging;

import static com.wrkr.tickety.domains.log.application.mapper.LogMapper.toAccessLog;

import com.wrkr.tickety.domains.auth.application.dto.request.LoginRequest;
import com.wrkr.tickety.domains.log.domain.constant.ActionType;
import com.wrkr.tickety.domains.log.domain.model.AccessLog;
import com.wrkr.tickety.domains.log.domain.service.AccessLogSaveService;
import com.wrkr.tickety.domains.log.utils.ClientIpResolver;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.annotation.logging.LogClientIp;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthLoggingAspect {

    private final ClientIpResolver clientIpResolver;
    private final AccessLogSaveService accessLogSaveService;

    @Around("@annotation(logClientIp)")
    public Object logClientIp(ProceedingJoinPoint joinPoint, LogClientIp logClientIp) throws Throwable {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra == null) {
            log.warn("RequestContextHolder에서 RequestAttributes를 가져올 수 없습니다.");
            return joinPoint.proceed();
        }

        HttpServletRequest request = sra.getRequest();
        String clientIp = clientIpResolver.getClientIp(request);

        Object[] args = joinPoint.getArgs();
        String nickname = extractNicknameOrMember(args);

        ActionType actionType = logClientIp.action();
        AccessLog accessLog = toAccessLog(nickname, clientIp, actionType, true);
        accessLogSaveService.save(accessLog);

        return joinPoint.proceed();
    }

    private String extractNicknameOrMember(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof LoginRequest request) {
                return request.nickname();
            } else if (arg instanceof Member member) {
                return member.getNickname();
            }
        }
        return "Unknown";
    }
}
