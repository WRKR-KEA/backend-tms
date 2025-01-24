package com.wrkr.tickety.domains.ticket.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
/**
 * url에 표시되는거라 소문자가 예쁠거 같아서 소문자로 했습니다.
 * */
public enum StatisticsType {
    daily,
    monthly,
    yearly
}
