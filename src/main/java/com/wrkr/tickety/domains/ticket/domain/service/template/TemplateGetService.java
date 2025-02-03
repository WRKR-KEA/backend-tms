package com.wrkr.tickety.domains.ticket.domain.service.template;

import com.wrkr.tickety.domains.ticket.persistence.repository.TemplateRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class TemplateGetService {

    private final TemplateRepository templateRepository;

    //TODO: adapter 관련 pull하여 로직 추가
    public Map<Long, Boolean> existsByCategoryIds(List<Long> categoryIds) {
        List<Long> existsTemplateIds = templateRepository.findByCategory_CategoryIdIn(categoryIds);

        return categoryIds.stream()
            .collect(Collectors.toMap(
                categoryId -> categoryId,
                existsTemplateIds::contains
            ));
    }
}
