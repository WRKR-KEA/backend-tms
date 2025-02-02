package com.wrkr.tickety.global.utils.date;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimePeriod {

    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
}