package com.wrkr.tickety.domains.ticket.application.mapper;

import com.wrkr.tickety.domains.attachment.domain.model.GuideAttachment;
import com.wrkr.tickety.domains.attachment.domain.service.GuideAttachmentGetService;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.global.utils.PkCrypto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GuideMapper {

    public GuideResponse guideToGuideResponse(Guide guide, GuideAttachmentGetService guideAttachmentGetService) {

        List<String> attachmentUrls = guideAttachmentGetService.getAttachmentsByGuideId(guide.getGuideId())
            .stream()
            .map(GuideAttachment::getFileUrl)
            .toList();

        return GuideResponse.builder()
            .guideId(PkCrypto.encrypt(guide.getGuideId()))
            .content(guide.getContent())
            .attachmentUrls(attachmentUrls)
            .build();
    }

    public PkResponse guideIdToPkResponse(Guide guide) {
        return new PkResponse(PkCrypto.encrypt(guide.getGuideId()));
    }
}
