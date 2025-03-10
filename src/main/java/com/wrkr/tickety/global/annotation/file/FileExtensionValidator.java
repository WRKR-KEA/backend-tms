package com.wrkr.tickety.global.annotation.file;

import static com.wrkr.tickety.global.response.code.CommonErrorCode.INVALID_FILE_EXTENSION;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileExtensionValidator implements ConstraintValidator<FileExtension, MultipartFile> {

    private String[] acceptedExtensions;

    @Override
    public void initialize(FileExtension constraintAnnotation) {
        this.acceptedExtensions = constraintAnnotation.acceptedExtensions();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        String filename = file.getOriginalFilename();
        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        boolean isValid = false;

        for (String ext : acceptedExtensions) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            String allowedFileExtensions = String.join(",", acceptedExtensions);
            String errorMessage = String.format("%s (허용된 형식: %s)",
                INVALID_FILE_EXTENSION.getMessage(),
                allowedFileExtensions);

            context.buildConstraintViolationWithTemplate(errorMessage)
                .addConstraintViolation();
        }

        return isValid;
    }

}
