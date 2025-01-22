package com.wrkr.tickety.domains.member.persistence.repository;

import com.wrkr.tickety.domains.member.persistence.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
