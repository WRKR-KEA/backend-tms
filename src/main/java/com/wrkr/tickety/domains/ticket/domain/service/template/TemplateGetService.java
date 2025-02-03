package com.wrkr.tickety.domains.ticket.domain.service.template;

import com.wrkr.tickety.domains.ticket.persistence.repository.TemplateRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TemplatePersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TemplateGetService {

    private final TemplatePersistenceAdapter templatePersistenceAdapter;

    public Map<Long, Boolean> existsByCategoryIds(List<Long> categoryIds) {
        List<Long> existsTemplateIds = templatePersistenceAdapter.existsByCategoryIds(categoryIds);

        return categoryIds.stream()
            .collect(Collectors.toMap(
                categoryId -> categoryId,
                existsTemplateIds::contains
            ));
    }
}
