package com.wrkr.tickety.domains.member.persistence.repository;

import com.wrkr.tickety.domains.member.domain.constant.Role;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MemberQueryDslRepository {

    Page<MemberEntity> searchMember(Role role, String query, Pageable pageable);
}
