package com.wrkr.tickety.domains.auth.utils;

import com.wrkr.tickety.infrastructure.redis.RedisRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginAttemptHandler {

    private final RedisRepository redisRepository;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCK_TIME = Duration.ofMinutes(30);
    private static final String LOGIN_FAIL_COUNT_KEY = "login_fail_count:";
    private static final String ACCOUNT_LOCKED_KEY = "account_locked:";
    private static final String ACCOUNT_LOCKED_VALUE = "true";

    public void handleFailedAttempt(Long memberId) {
        if (isAccountLocked(memberId)) {
            lockAccount(memberId);
            return;
        }

        int failedAttempts = getFailedAttempts(memberId) + 1;

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            lockAccount(memberId);
        } else {
            saveFailedAttempts(memberId, failedAttempts);
        }
    }

    public boolean isAccountLocked(Long memberId) {
        return redisRepository.getValues(getAccountLockedKey(memberId)).isPresent();
    }

    public void resetFailedAttempts(Long memberId) {
        redisRepository.deleteValues(getLoginFailCountKey(memberId));
    }

    private int getFailedAttempts(Long memberId) {
        return redisRepository.getValues(getLoginFailCountKey(memberId))
            .map(Integer::parseInt)
            .orElse(0);
    }

    private void saveFailedAttempts(Long memberId, int failedAttempts) {
        redisRepository.setValues(getLoginFailCountKey(memberId), String.valueOf(failedAttempts), LOCK_TIME);
    }

    private void lockAccount(Long memberId) {
        redisRepository.setValues(getAccountLockedKey(memberId), ACCOUNT_LOCKED_VALUE, LOCK_TIME);
    }

    private String getLoginFailCountKey(Long memberId) {
        return LOGIN_FAIL_COUNT_KEY + memberId;
    }

    private String getAccountLockedKey(Long memberId) {
        return ACCOUNT_LOCKED_KEY + memberId;
    }
}
