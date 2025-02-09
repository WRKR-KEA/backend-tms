package com.wrkr.tickety.domains.member.persistence.mapper;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import com.wrkr.tickety.global.common.mapper.PersistenceMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberPersistenceMapper extends PersistenceMapper<MemberEntity, Member> {

}
