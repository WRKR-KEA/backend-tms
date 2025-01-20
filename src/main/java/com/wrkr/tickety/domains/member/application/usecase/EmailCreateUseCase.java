package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.request.EmailCreateReqDTO;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class EmailCreateUseCase {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public String sendMail(EmailCreateReqDTO emailCreateReqDTO, String code, String type) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailCreateReqDTO.to()); // 메일 수신자
            mimeMessageHelper.setSubject(emailCreateReqDTO.subject()); // 메일 제목
            mimeMessageHelper.setText(setContext(code, type), true); // 메일 본문 내용, HTML 여부
            javaMailSender.send(mimeMessage);

            log.info("이메일 전송 성공");

            return code;

        } catch (MessagingException e) {
            log.info("이메일 전송 실패");
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param code: 타임리프안의 변수
     * @param type: html 파일 이름
     * @return
     */
    private String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(type, context);
    }
}