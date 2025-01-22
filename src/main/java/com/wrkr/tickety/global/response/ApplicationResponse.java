package com.wrkr.tickety.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.wrkr.tickety.global.response.code.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApplicationResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    public static <T> ApplicationResponse<T> onSuccess(T result) {
        return new ApplicationResponse<>(
                true,
                SuccessCode.SUCCESS.getCustomCode(),
                SuccessCode.SUCCESS.getMessage(),
                result
        );
    }

    public static ApplicationResponse<Void> onSuccess() {
        return new ApplicationResponse<>(
                true,
                SuccessCode.SUCCESS.getCustomCode(),
                SuccessCode.SUCCESS.getMessage(),
                null
        );
    }

    public static <T> ApplicationResponse<T> onFailure(String code, String message, T result) {
        return new ApplicationResponse<>(
                false,
                code,
                message,
                result
        );
    }

    public static <T> ApplicationResponse<T> onFailure(String code, String message) {
        return new ApplicationResponse<>(
                false,
                code,
                message,
                null
        );
    }
}
