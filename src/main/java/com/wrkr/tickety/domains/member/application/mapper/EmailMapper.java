package com.wrkr.tickety.domains.member.application.mapper;

import com.wrkr.tickety.domains.member.application.dto.request.EmailCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class EmailMapper {
    
    public static EmailCreateRequest toEmailCreateRequest(
            String to, String subject, String message
            )
    {
        return EmailCreateRequest.builder()
                .to(to)
                .subject(subject)
                .message(message)
                .build();
    }
}
