package com.wrkr.tickety.domains.ticket.domain.service.Guide;

import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GuideDeleteService {
    private final PkCrypto pkCrypto;
    private final GuideRepository guideRepository;

    public PkResponse deleteGuide(String cryptoGuideId) {
        Long guideId = pkCrypto.decryptValue(cryptoGuideId);

        Guide guideEntity = guideRepository.findById(guideId)
                .orElseThrow(() -> ApplicationException.from(GuideErrorCode.GUIDE_NOT_EXIST));

        guideRepository.delete(guideEntity);

        return new PkResponse(cryptoGuideId);
    }
}
