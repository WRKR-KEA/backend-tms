package com.wrkr.tickety.domains.ticket.domain.service.guide;

import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.adapter.GuidePersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GuideGetService {

    private final GuidePersistenceAdapter guidePersistenceAdapter;

    public Guide getGuideContentByCategory(String cryptoCategoryId) {

        long categoryId = PkCrypto.decrypt(cryptoCategoryId);

        Guide guide = guidePersistenceAdapter.findByCategoryId(categoryId)
            .orElseThrow(() -> ApplicationException.from(GuideErrorCode.GUIDE_NOT_EXIST));

        return guide;
    }

    public Boolean existsByCategoryId(Long categoryId) {
        return guidePersistenceAdapter.existsByCategoryId(categoryId);
    }

}
