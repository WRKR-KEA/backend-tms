package com.wrkr.tickety.domains.ticket.domain.service.Guide;

import com.wrkr.tickety.domains.ticket.application.dto.request.GuideUpdateRequest;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.adapter.GuidePersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class GuideUpdateService {

    private final PkCrypto pkCrypto;
    private final GuidePersistenceAdapter guidePersistenceAdapter;

    public Guide updateGuide(String cryptoGuideId, GuideUpdateRequest guideUpdateRequest) {
        long guideId = pkCrypto.decryptValue(cryptoGuideId);
        Guide guide = guidePersistenceAdapter.findById(guideId)
                .orElseThrow(() -> ApplicationException.from(GuideErrorCode.GUIDE_NOT_EXIST));

        guide.updateContent(guideUpdateRequest.content());

        return guidePersistenceAdapter.save(guide);
    }
}
