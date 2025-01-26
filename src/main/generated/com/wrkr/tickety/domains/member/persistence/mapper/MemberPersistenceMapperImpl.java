package com.wrkr.tickety.domains.member.persistence.mapper;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-25T17:50:21+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.2 (Homebrew)"
)
@Component
public class MemberPersistenceMapperImpl implements MemberPersistenceMapper {

    @Override
    public MemberEntity toEntity(Member domain) {
        if ( domain == null ) {
            return null;
        }

        MemberEntity.MemberEntityBuilder<?, ?> memberEntity = MemberEntity.builder();

        memberEntity.createdAt( domain.getCreatedAt() );
        memberEntity.updatedAt( domain.getUpdatedAt() );
        memberEntity.memberId( domain.getMemberId() );
        memberEntity.nickname( domain.getNickname() );
        memberEntity.password( domain.getPassword() );
        memberEntity.name( domain.getName() );
        memberEntity.phone( domain.getPhone() );
        memberEntity.email( domain.getEmail() );
        memberEntity.position( domain.getPosition() );
        memberEntity.profileImage( domain.getProfileImage() );
        memberEntity.role( domain.getRole() );
        memberEntity.agitUrl( domain.getAgitUrl() );
        memberEntity.agitNotification( domain.getAgitNotification() );
        memberEntity.emailNotification( domain.getEmailNotification() );
        memberEntity.serviceNotification( domain.getServiceNotification() );
        memberEntity.isDeleted( domain.getIsDeleted() );

        return memberEntity.build();
    }

    @Override
    public Member toDomain(MemberEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Member.MemberBuilder<?, ?> member = Member.builder();

        member.createdAt( entity.getCreatedAt() );
        member.updatedAt( entity.getUpdatedAt() );
        member.memberId( entity.getMemberId() );
        member.nickname( entity.getNickname() );
        member.password( entity.getPassword() );
        member.name( entity.getName() );
        member.phone( entity.getPhone() );
        member.email( entity.getEmail() );
        member.position( entity.getPosition() );
        member.profileImage( entity.getProfileImage() );
        member.role( entity.getRole() );
        member.agitUrl( entity.getAgitUrl() );
        member.agitNotification( entity.getAgitNotification() );
        member.emailNotification( entity.getEmailNotification() );
        member.serviceNotification( entity.getServiceNotification() );
        member.isDeleted( entity.getIsDeleted() );

        return member.build();
    }
}
