package com.wrkr.tickety.domains.ticket.domain.service.guide;

import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.adapter.GuidePersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GuideGetService {

    private final GuidePersistenceAdapter guidePersistenceAdapter;

    public Guide getGuideContentByCategory(Long categoryId) {

        Guide guide = guidePersistenceAdapter.findByCategoryId(categoryId)
            .orElseThrow(() -> ApplicationException.from(GuideErrorCode.GUIDE_NOT_EXIST));

        return guide;
    }


    public Map<Long, Boolean> existsByCategoryIds(List<Long> categoryIds) {
        List<Guide> existsGuides = guidePersistenceAdapter.existsByCategoryIds(categoryIds);

        return existsGuides.stream()
            .collect(Collectors.toMap(
                guide -> guide.getCategory().getCategoryId(),
                guide -> true
            ));

    }
}
