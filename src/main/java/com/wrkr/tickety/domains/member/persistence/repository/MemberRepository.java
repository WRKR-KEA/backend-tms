package com.wrkr.tickety.domains.member.persistence.repository;

import com.wrkr.tickety.domains.member.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
