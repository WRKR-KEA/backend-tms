package com.wrkr.tickety.domains.member.persistence.repository;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long>, MemberQueryDslRepository {

    boolean existsByEmailAndIsDeleted(String email, Boolean isDeleted);

    boolean existsByNickname(String nickname);

    Optional<MemberEntity> findByNicknameAndIsDeleted(String nickname, boolean isDeleted);

    List<MemberEntity> findByRoleAndIsDeletedFalse(Role role);

    Optional<MemberEntity> findByMemberIdAndIsDeleted(Long memberId, boolean isDeleted);
}
