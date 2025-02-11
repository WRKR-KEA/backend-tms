package com.wrkr.tickety.infrastructure.email;

import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.response.code.CommonErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
@RequiredArgsConstructor
@Transactional
@EnableAsync
public class EmailUtil {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    // TODO: 스레드 풀 설정 필요

    /**
     * @param emailCreateRequest
     * @param code:              타임리프 내부에 매핑할 변수
     * @param type:              html 파일 이름
     * @return
     */
    @Async
    public void sendMail(EmailCreateRequest emailCreateRequest, String code, String type) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailCreateRequest.to()); // 메일 수신자
            mimeMessageHelper.setSubject(emailCreateRequest.subject()); // 메일 제목
            mimeMessageHelper.setText(setContext(code, type), true); // 메일 본문 내용, HTML 여부
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) { // TODO: 존재하지 않은 이메일인 경우도 고려할 수 있으면 고려
            throw new ApplicationException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param code: 타임리프 내부에 매핑할 변수
     * @param type: html 파일 이름
     * @return
     */
    private String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(type, context);
    }
}