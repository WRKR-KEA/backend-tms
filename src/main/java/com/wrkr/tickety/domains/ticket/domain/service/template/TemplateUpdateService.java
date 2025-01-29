package com.wrkr.tickety.domains.ticket.domain.service.template;

import com.wrkr.tickety.domains.ticket.domain.model.Template;
import com.wrkr.tickety.domains.ticket.persistence.adapter.TemplatePersistenceAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class TemplateUpdateService {

    private final TemplatePersistenceAdapter templatePersistenceAdapter;

    public Template update(Template requestTemplate) { return templatePersistenceAdapter.save(requestTemplate);}
}
