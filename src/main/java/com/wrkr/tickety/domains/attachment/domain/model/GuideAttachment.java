package com.wrkr.tickety.domains.attachment.domain.model;

import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.global.entity.BaseTimeEntity;
import com.wrkr.tickety.global.model.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuideAttachment extends BaseTime {

    private Long attachmentId;
    private Guide guide;
    private String fileUrl;
    private String fileName;
    private Long fileSize;

    @Builder
    public GuideAttachment(
            Long attachmentId,
            Guide guide,
            String fileUrl,
            String fileName,
            Long fileSize
    ) {
        this.attachmentId = attachmentId;
        this.guide = guide;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
}
