package com.wrkr.tickety.domains.ticket.domain.service.guide;

import com.wrkr.tickety.domains.ticket.application.dto.response.PkResponse;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.adapter.GuidePersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GuideDeleteService {

    private final PkCrypto pkCrypto;
    private final GuidePersistenceAdapter guidePersistenceAdapter;

    public PkResponse deleteGuide(Long guideId) {

        Guide guideEntity = guidePersistenceAdapter.findById(guideId)
            .orElseThrow(() -> ApplicationException.from(GuideErrorCode.GUIDE_NOT_EXIST));

        guidePersistenceAdapter.deleteById(guideEntity.getGuideId());
        String cryptoGuideId = pkCrypto.encryptValue(guideId);
        return new PkResponse(cryptoGuideId);
    }
}
