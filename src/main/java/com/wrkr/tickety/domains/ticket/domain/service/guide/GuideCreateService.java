package com.wrkr.tickety.domains.ticket.domain.service.guide;

import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.exception.GuideErrorCode;
import com.wrkr.tickety.domains.ticket.persistence.adapter.GuidePersistenceAdapter;
import com.wrkr.tickety.global.exception.ApplicationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GuideCreateService {

    private final GuidePersistenceAdapter guidePersistenceAdapter;

    public Guide createGuide(Guide guide) {
        boolean isGuideExists = guidePersistenceAdapter.existsByCategoryId(guide.getCategory().getCategoryId());
        if (isGuideExists) throw ApplicationException.from(GuideErrorCode.GUIDE_ALREADY_EXIST);
        return guidePersistenceAdapter.save(guide);
    }
}
