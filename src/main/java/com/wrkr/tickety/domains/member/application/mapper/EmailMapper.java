package com.wrkr.tickety.domains.member.application.mapper;

import com.wrkr.tickety.domains.member.application.dto.request.EmailCreateReqDTO;
import org.springframework.stereotype.Component;

@Component
public class EmailMapper {
    
    public static EmailCreateReqDTO toEmailCreateReqDTO(
            String to, String subject, String message
            )
    {
        return EmailCreateReqDTO.builder()
                .to(to)
                .subject(subject)
                .message(message)
                .build();
    }
}
