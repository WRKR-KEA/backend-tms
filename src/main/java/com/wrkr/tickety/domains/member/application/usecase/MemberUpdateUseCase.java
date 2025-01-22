package com.wrkr.tickety.domains.member.application.usecase;

import com.wrkr.tickety.domains.member.application.dto.request.MemberUpdateRequest;
import com.wrkr.tickety.domains.member.application.dto.response.MemberPkResponse;
import com.wrkr.tickety.domains.member.application.mapper.MemberMapper;
import com.wrkr.tickety.domains.member.domain.model.MemberDomain;
import com.wrkr.tickety.domains.member.persistence.entity.Member;
import com.wrkr.tickety.domains.member.domain.service.MemberGetService;
import com.wrkr.tickety.domains.member.domain.service.MemberUpdateService;
import com.wrkr.tickety.domains.member.persistence.mapper.MemberPersistenceMapper;
import com.wrkr.tickety.global.annotation.architecture.UseCase;
import com.wrkr.tickety.global.utils.PkCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@UseCase
@RequiredArgsConstructor
@Transactional
public class MemberUpdateUseCase {
    private final MemberUpdateService memberUpdateService;
    private final MemberGetService memberGetService;

    public MemberPkResponse modifyMemberInfo(String memberId, MemberUpdateRequest memberUpdateRequest) {
        Member member = memberGetService.byMemberId(PkCrypto.decrypt(memberId));

        MemberDomain memberDomain = MemberPersistenceMapper.toDomain(member);
        memberDomain.modifyMemberInfo(memberUpdateRequest);
        member.modifyMemberInfo(memberDomain);

//        Member modifiedMember = MemberPersistenceMapper.toEntity(memberDomain);

        memberUpdateService.modifyMemberInfo(member);

        return MemberMapper.toMemberPkResponse(PkCrypto.encrypt(member.getMemberId()));
    }

    public void softDeleteMember(List<String> memberIdList) {
        memberIdList.forEach(memberId -> {
            Member member = memberGetService.byMemberId(PkCrypto.decrypt(memberId));

            MemberDomain memberDomain = MemberPersistenceMapper.toDomain(member);
            memberDomain.modifyIsDeleted(true);
            Member modifiedMember = MemberPersistenceMapper.toEntity(memberDomain);

            memberUpdateService.modifyMemberInfo(modifiedMember);
        });
    }
}
