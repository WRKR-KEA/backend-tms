package com.wrkr.tickety.domains.member.persistence.repository;

import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long>, MemberQueryDslRepository {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<MemberEntity> findByNicknameAndIsDeleted(String nickname, boolean isDeleted);
}
