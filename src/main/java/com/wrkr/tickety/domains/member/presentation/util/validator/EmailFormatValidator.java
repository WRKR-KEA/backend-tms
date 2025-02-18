package com.wrkr.tickety.domains.member.presentation.util.validator;

import static com.wrkr.tickety.domains.member.exception.MemberErrorCode.INVALID_EMAIL;

import com.wrkr.tickety.domains.member.presentation.util.annotation.EmailFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class EmailFormatValidator implements ConstraintValidator<EmailFormat, String> {

    private String[] acceptedDomains;
    private static final String EMAIL_REGEX_TEMPLATE = "^[a-zA-Z0-9._%%+-]+@(%s)$";
    private Pattern emailPattern;

    @Override
    public void initialize(EmailFormat constraintAnnotation) {
        this.acceptedDomains = constraintAnnotation.acceptedDomains();

        if (acceptedDomains == null || acceptedDomains.length == 0) {
            throw new IllegalArgumentException("acceptedDomains must not be null or empty");
        }

        StringBuilder regexBuilder = new StringBuilder();

        for (int i = 0; i < acceptedDomains.length; i++) {
            regexBuilder.append(Pattern.quote(acceptedDomains[i]));
            if (i < acceptedDomains.length - 1) {
                regexBuilder.append("|");
            }
        }

        String emailRegex = String.format(EMAIL_REGEX_TEMPLATE, regexBuilder.toString());

        // 정규식이 유효한지 검증
        try {
            this.emailPattern = Pattern.compile(emailRegex);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid email regex pattern: " + emailRegex, e);
        }
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        boolean isValid = email != null && !email.isBlank() && emailPattern.matcher(email).matches();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            String allowedDomains = String.join(", ", acceptedDomains);
            String errorMessage = String.format("%s (허용된 이메일: %s)",
                INVALID_EMAIL.getMessage(),
                allowedDomains);
            context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
        }

        return isValid;
    }
}
