package com.wrkr.tickety.domains.ticket.domain.service.template;

import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.exception.TemplateErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TemplatePersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TemplateGetService {

    private final TemplatePersistenceAdapter templatePersistenceAdapter;

    public Map<Long, Boolean> existsByCategoryIds(List<Long> categoryIds) {
        List<Template> existsTemplateIds = templatePersistenceAdapter.existsByCategoryIds(categoryIds);

        return existsTemplateIds.stream()
            .collect(Collectors.toMap(
                template -> template.getCategory().getCategoryId(),
                template -> true
            ));
    }

    public Template getTemplateByCategoryId(Long categoryId) {
        return templatePersistenceAdapter.findByCategoryId(categoryId)
            .orElseThrow(() -> ApplicationException.from(TemplateErrorCode.TEMPLATE_NOT_EXISTS));
    }

    public Boolean existsByCategoryId(Long categoryId) {
        return templatePersistenceAdapter.existsByCategoryId(categoryId);
    }

    public Template getTemplateByTemplateId(Long templateId) {
        return templatePersistenceAdapter.findByTemplateId(templateId)
            .orElseThrow(() -> ApplicationException.from(TemplateErrorCode.TEMPLATE_NOT_EXISTS));
    }
}
