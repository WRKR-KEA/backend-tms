package com.wrkr.tickety.global.config.security.jwt;

import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.INVALID_TOKEN;
import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.TOKEN_EXPIRED;
import static com.wrkr.tickety.domains.auth.exception.AuthErrorCode.TOKEN_NOT_FOUND;
import static com.wrkr.tickety.global.config.security.jwt.constant.TokenType.REFRESH;
import static com.wrkr.tickety.global.response.code.CommonErrorCode.UNAUTHORIZED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.global.config.security.auth.CustomUserDetails;
import com.wrkr.tickety.global.config.security.auth.CustomUserDetailsService;
import com.wrkr.tickety.global.config.security.jwt.constant.TokenType;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.ApplicationResponse;
import com.wrkr.tickety.global.response.code.BaseErrorCode;
import com.wrkr.tickety.infrastructure.redis.RedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    private final CustomUserDetailsService customUserDetailsService;
    private final RedisRepository redisRepository;

    @Value("${jwt.secret-key}")
    private String secret;

    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE = "application/json";
    private static final String CHARACTER_ENCODING = "UTF-8";
    private static final String ROLE = "role";
    private static final String TYPE = "type";

    public void validateToken(HttpServletResponse response, String token, TokenType tokenType) {
        try {
            Claims claims = parseClaims(token);
            if (!claims.get(TYPE).equals(tokenType.name())) {
                throw ApplicationException.from(INVALID_TOKEN);
            }
        } catch (ExpiredJwtException e) {
            jwtExceptionHandler(response, TOKEN_EXPIRED);
            throw ApplicationException.from(TOKEN_EXPIRED);
        } catch (Exception e) {
            jwtExceptionHandler(response, INVALID_TOKEN);
            throw ApplicationException.from(INVALID_TOKEN);
        }
    }

    public void validateRefreshToken(Long memberId, HttpServletRequest request) {
        String refreshToken = request.getHeader(AUTHORIZATION).split(" ")[1];
        String redisToken = redisRepository.getValues(REFRESH.toString() + memberId)
            .orElseThrow(() -> ApplicationException.from(TOKEN_NOT_FOUND));

        if (!redisToken.equals(refreshToken)) {
            throw ApplicationException.from(INVALID_TOKEN);
        }
    }

    public void expireRefreshToken(Long memberId) {
        redisRepository.deleteValues(REFRESH.toString() + memberId);
    }

    public Authentication getAuthentication(HttpServletResponse response, String token) throws ApplicationException {
        Claims claims = parseClaims(token);
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(claims.get(ROLE).toString()));
        Member member = loadUserDetailsFromClaims(response, claims).getMember();

        return new UsernamePasswordAuthenticationToken(member, "", authorities);
    }

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private CustomUserDetails loadUserDetailsFromClaims(HttpServletResponse response, Claims claims) {
        try {
            return customUserDetailsService.loadUserByUsername(claims.getSubject());
        } catch (ApplicationException e) {
            jwtExceptionHandler(response, UNAUTHORIZED);
            throw e;
        }
    }

    private void jwtExceptionHandler(HttpServletResponse response, BaseErrorCode error) {
        response.setStatus(error.getHttpStatus().value());
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER_ENCODING);

        log.error("errorCode {}, errorMessage {}", error.getCustomCode(), error.getMessage());

        try {
            String json = new ObjectMapper().writeValueAsString(ApplicationResponse.onFailure(error.getCustomCode(), error.getMessage()));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
