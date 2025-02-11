package com.wrkr.tickety.global.utils.attachment;

import java.util.List;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public class FileValidationUtil {

    private static final int MAX_FILE_COUNT = 5;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "pdf", "xls", "xlsx"
    );

    public static void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        if (files.size() > MAX_FILE_COUNT) {
            throw new IllegalArgumentException("첨부파일 개수는 최대 " + MAX_FILE_COUNT + "개까지 가능합니다.");
        }

        for (MultipartFile file : files) {
            validateFile(file);
        }
    }

    public static void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 최대 " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB까지 가능합니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않은 파일 확장자입니다. [" + extension + "] (허용 확장자: " + ALLOWED_EXTENSIONS + ")");
        }
    }

    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
