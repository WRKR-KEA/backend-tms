package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.exception.CategoryErrorCode;
import com.wrkr.tickety.domains.ticket.exception.TemplateErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.entity.TemplateEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TemplatePersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.CategoryRepository;
import com.wrkr.tickety.domains.ticket.persistence.repository.TemplateRepository;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TemplatePersistenceAdapter {

    private final TemplatePersistenceMapper templatePersistenceMapper;
    private final TemplateRepository templateRepository;
    private final CategoryRepository categoryRepository;

    public Boolean existsByCategory(Long categoryId) {
        return templateRepository.existsByCategory_CategoryId(categoryId);
    }

    public Template findByCategory(Long categoryId) {
        if(!categoryRepository.existsByCategoryIdAndIsDeletedFalseAndParentIsNull(categoryId)) {
            throw new ApplicationException(CategoryErrorCode.CATEGORY_NOT_EXIST);
        }
        TemplateEntity templateEntity = templateRepository.findByCategory_CategoryId(categoryId).orElseThrow(() -> new ApplicationException(TemplateErrorCode.TEMPLATE_NOT_EXIST));
        return templatePersistenceMapper.toDomain(templateEntity);
    }

    public Template save(Template template) {
        if(!categoryRepository.existsByCategoryIdAndIsDeletedFalseAndParentIsNull(template.getCategory().getCategoryId())) {
            throw new ApplicationException(CategoryErrorCode.CATEGORY_NOT_EXIST);
        }
        if(templateRepository.existsByCategory_CategoryId(template.getCategory().getCategoryId())) {
            throw new ApplicationException(TemplateErrorCode.TEMPLATE_ALREADY_EXIST);
        }

        TemplateEntity requestTemplateEntity = templatePersistenceMapper.toEntity(template);
        TemplateEntity createdTemplateEntity = templateRepository.save(requestTemplateEntity);
        return templatePersistenceMapper.toDomain(createdTemplateEntity);
    }
}
