package com.wrkr.tickety.global.config.security.jwt;

import static com.wrkr.tickety.global.config.security.jwt.constant.TokenType.ACCESS;
import static com.wrkr.tickety.global.config.security.jwt.constant.TokenType.REFRESH;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.infrastructure.redis.RedisRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final RedisRepository redisRepository;

    @Value("${jwt.secret-key}")
    private String secret;

    @Value("${jwt.refresh-expiration}")
    private int refreshExpiration;

    @Value("${jwt.access-expiration}")
    private int accessExpiration;

    private static final String ROLE = "role";
    private static final String TYPE = "type";

    public String generateAccessToken(String nickname, Role role) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        Instant accessDate = LocalDateTime.now().plusSeconds(accessExpiration).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
            .claim(ROLE, role)
            .claim(TYPE, ACCESS)
            .setSubject(nickname)
            .setExpiration(Date.from(accessDate))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateRefreshToken(String nickname, Role role, Long memberId) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        Instant refreshDate = LocalDateTime.now().plusSeconds(refreshExpiration).atZone(ZoneId.systemDefault()).toInstant();

        String refreshToken = Jwts.builder()
            .claim(ROLE, role)
            .claim(TYPE, REFRESH)
            .setSubject(nickname)
            .setExpiration(Date.from(refreshDate))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        redisRepository.setValues(REFRESH.toString() + memberId, refreshToken, Duration.ofSeconds(refreshExpiration));

        return refreshToken;
    }
}
