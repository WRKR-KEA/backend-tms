package com.wrkr.tickety.domains.notification.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@RequiredArgsConstructor
public class MapEmailTemplateService {

    private final SpringTemplateEngine templateEngine;

    public String setTicketStatusChangeContext(String status, String ticketSerialNumber, String type) {
        Context context = new Context();
        context.setVariable("status", status);
        context.setVariable("ticketSerialNumber", ticketSerialNumber);
        return templateEngine.process(type, context);
    }

    public String setDelegateContext(String serialNumber, String managerNickname, String type) {
        Context context = new Context();
        context.setVariable("ticketSerialNumber", serialNumber);
        context.setVariable("nickname", managerNickname);
        return templateEngine.process(type, context);
    }
}
