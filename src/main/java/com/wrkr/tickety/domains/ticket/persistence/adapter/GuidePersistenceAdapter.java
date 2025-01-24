package com.wrkr.tickety.domains.ticket.persistence.adapter;

import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.persistence.entity.GuideEntity;
import com.wrkr.tickety.domains.ticket.persistence.mapper.GuidePersistenceMapper;
import com.wrkr.tickety.domains.ticket.persistence.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GuidePersistenceAdapter {

    private final GuideRepository guideRepository;
    private final GuidePersistenceMapper guidePersistenceMapper;

    public Guide save(final Guide guide) {
        GuideEntity guideEntity = this.guidePersistenceMapper.toEntity(guide);
        GuideEntity savedEntity = this.guideRepository.save(guideEntity);
        return this.guidePersistenceMapper.toDomain(savedEntity);
    }

    public Optional<Guide> findById(final Long guideId) {
        final Optional<GuideEntity> guideEntity = this.guideRepository.findById(guideId);
        return guideEntity.map(this.guidePersistenceMapper::toDomain);
    }

    public Optional<Guide> findByCategoryId(final Long guideId) {
        final Optional<GuideEntity> guideEntity = this.guideRepository.findByCategory_CategoryId(guideId);
        return guideEntity.map(this.guidePersistenceMapper::toDomain);
    }

    public Boolean existsByCategoryId(final Long guideId) {
        return this.guideRepository.existsByCategory_CategoryId(guideId);
    }

    public void deleteById(final Long guideId) {
        guideRepository.deleteById(guideId);
    }
}
