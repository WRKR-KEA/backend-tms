package com.wrkr.tickety.global.config.security.xss;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

public class XssSanitizer {

    private static final Safelist PERMITTED_TAGS = Safelist.relaxed()
        .addTags("img")
        .addAttributes("img", "src", "alt", "width", "height")
        .addAttributes("a", "href", "title", "target")
        .addAttributes("iframe", "src", "width", "height", "frameborder", "allow", "allowfullscreen")
        .preserveRelativeLinks(true);

    private static final Pattern BACKTICK_PATTERN = Pattern.compile("(`+)([^`]+?)\\1");

    public static String sanitize(String unsafeInput) {
        if (unsafeInput == null) {
            return null;
        }

        Map<String, String> placeholderMap = new HashMap<>();
        StringBuffer sanitizedBuffer = new StringBuffer();

        // 백틱 내부 내용 임시 저장
        Matcher matcher = BACKTICK_PATTERN.matcher(unsafeInput);
        int count = 0;

        while (matcher.find()) {
            String placeholder = "%%KEEP_MARKDOWN_" + count + "%%";
            placeholderMap.put(placeholder, matcher.group()); // 원본 백틱 내용 저장
            matcher.appendReplacement(sanitizedBuffer, placeholder);
            count++;
        }
        matcher.appendTail(sanitizedBuffer);
        String sanitized = sanitizedBuffer.toString();

        // Jsoup 적용
        sanitized = Jsoup.clean(sanitized, "", PERMITTED_TAGS, new Document.OutputSettings().prettyPrint(false));

        // 백틱 내부 복원
        for (Map.Entry<String, String> entry : placeholderMap.entrySet()) {
            sanitized = sanitized.replace(entry.getKey(), entry.getValue());
        }

        // 빈 내용에 대한 default 메시지 설정
        if (sanitized.isBlank()) {
            sanitized = "[Filtered!]";
        }

        return sanitized;
    }
}
