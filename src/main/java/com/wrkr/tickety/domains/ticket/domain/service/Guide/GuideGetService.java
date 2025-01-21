package com.wrkr.tickety.domains.ticket.domain.service.Guide;

import com.wrkr.tickety.domains.ticket.domain.GuideDomain;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GuideGetService {

    private final GuideRepository guideRepository;

    public GuideDomain getGuideContentByCategory(String cryptoCategoryId) {
        long categoryId = PkCrypto.decrypt(cryptoCategoryId);
        Guide guideEntity = guideRepository.findByCategory_CategoryId(categoryId)
                .orElseThrow(() -> ApplicationException.from(GuideErrorCode.GUIDE_NOT_EXIST));

        return GuideDomain.toDomain(guideEntity);
    }

    public Boolean existsByCategoryId(Long categoryId) {
        return guideRepository.existsByCategory_CategoryId(categoryId);
    }

}
