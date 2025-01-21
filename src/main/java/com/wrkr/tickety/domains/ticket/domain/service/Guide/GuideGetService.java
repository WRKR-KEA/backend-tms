package com.wrkr.tickety.domains.ticket.domain.service.Guide;

import com.wrkr.tickety.domains.ticket.application.dto.response.GuideResponse;
import com.wrkr.tickety.domains.ticket.application.mapper.GuideMapper;
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
    private final GuideMapper guideMapper;

    public GuideResponse getGuideContentByCategory(String encryptedCategoryId) {
        long categoryId = PkCrypto.decrypt(encryptedCategoryId);
        Guide guideEntity = guideRepository.findByCategory_CategoryId(categoryId)
                .orElseThrow(() -> ApplicationException.from(GuideErrorCode.GuideNotExist));
        GuideDomain guideDomain = GuideDomain.toDomain(guideEntity);

        return guideMapper.guideToGuideResponse(guideDomain);
    }
}
