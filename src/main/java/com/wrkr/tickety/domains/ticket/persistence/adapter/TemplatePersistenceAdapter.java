package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.persistence.entity.TemplateEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.TemplatePersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.TemplateRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TemplatePersistenceAdapter {

    private final TemplatePersistenceMapper templatePersistenceMapper;
    private final TemplateRepository templateRepository;

    public Optional<Template> findByCategoryId(Long categoryId) {
        Optional<TemplateEntity> templateEntity = templateRepository.findByCategory_CategoryId(categoryId);
        return templateEntity.map(this.templatePersistenceMapper::toDomain);

    }

    public Template save(Template template) {
        TemplateEntity requestTemplateEntity = templatePersistenceMapper.toEntity(template);
        TemplateEntity createdTemplateEntity = templateRepository.save(requestTemplateEntity);
        return templatePersistenceMapper.toDomain(createdTemplateEntity);
    }

    public Template delete(Template template) {
        TemplateEntity deleteTemplateEntity = templatePersistenceMapper.toEntity(template);
        templateRepository.delete(deleteTemplateEntity);
        return templatePersistenceMapper.toDomain(deleteTemplateEntity);
    }

    public List<Template> existsByCategoryIds(List<Long> categoryIds) {
        List<TemplateEntity> templateEntities = templateRepository.findByCategory_CategoryIdIn(categoryIds);
        return templateEntities.stream().map(this.templatePersistenceMapper::toDomain).toList();
    }

    public Boolean existsByCategoryId(Long categoryId) {
        return templateRepository.existsByCategory_CategoryId(categoryId);
    }

    public Optional<Template> findByTemplateId(Long templateId) {
        Optional<TemplateEntity> templateEntity = templateRepository.findById(templateId);
        return templateEntity.map(this.templatePersistenceMapper::toDomain);
    }
}
