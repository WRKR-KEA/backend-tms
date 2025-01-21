package com.wrkr.tickety.domains.ticket.domain.service.Guide;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GuideUpdateService {
    private final PkCrypto pkCrypto;
    private final GuideRepository guideRepository;
    private final GuideMapper guideMapper;

    public GuideResponse modifyGuide(String cryptoGuideId, GuideUpdateRequest guideUpdateRequest) {

        long guideId = pkCrypto.decryptValue(cryptoGuideId);
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> ApplicationException.from(GuideErrorCode.GuideNotExist));
        GuideDomain guideDomain = GuideDomain.toDomain(guide);
        guideDomain.updateContent(guideUpdateRequest.getContent());
        guide.updateContent(guideDomain.getContent());
        guideRepository.save(guide);

        return guideMapper.guideToGuideResponse(guideDomain);
    }
}
