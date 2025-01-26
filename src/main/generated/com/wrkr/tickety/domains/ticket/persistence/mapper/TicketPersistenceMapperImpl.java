package com.wrkr.tickety.domains.ticket.persistence.mapper;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.TicketEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-25T17:50:20+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.2 (Homebrew)"
)
@Component
public class TicketPersistenceMapperImpl implements TicketPersistenceMapper {

    @Override
    public TicketEntity toEntity(Ticket domain) {
        if ( domain == null ) {
            return null;
        }

        TicketEntity.TicketEntityBuilder<?, ?> ticketEntity = TicketEntity.builder();

        ticketEntity.createdAt( domain.getCreatedAt() );
        ticketEntity.updatedAt( domain.getUpdatedAt() );
        ticketEntity.ticketId( domain.getTicketId() );
        ticketEntity.user( memberToMemberEntity( domain.getUser() ) );
        ticketEntity.manager( memberToMemberEntity( domain.getManager() ) );
        ticketEntity.category( categoryToCategoryEntity( domain.getCategory() ) );
        ticketEntity.serialNumber( domain.getSerialNumber() );
        ticketEntity.title( domain.getTitle() );
        ticketEntity.content( domain.getContent() );
        ticketEntity.status( domain.getStatus() );
        ticketEntity.isPinned( domain.getIsPinned() );

        return ticketEntity.build();
    }

    @Override
    public Ticket toDomain(TicketEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Ticket.TicketBuilder<?, ?> ticket = Ticket.builder();

        ticket.createdAt( entity.getCreatedAt() );
        ticket.updatedAt( entity.getUpdatedAt() );
        ticket.ticketId( entity.getTicketId() );
        ticket.user( memberEntityToMember( entity.getUser() ) );
        ticket.manager( memberEntityToMember( entity.getManager() ) );
        ticket.category( categoryEntityToCategory( entity.getCategory() ) );
        ticket.serialNumber( entity.getSerialNumber() );
        ticket.title( entity.getTitle() );
        ticket.content( entity.getContent() );
        ticket.status( entity.getStatus() );
        ticket.isPinned( entity.getIsPinned() );

        return ticket.build();
    }

    protected MemberEntity memberToMemberEntity(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberEntity.MemberEntityBuilder<?, ?> memberEntity = MemberEntity.builder();

        memberEntity.createdAt( member.getCreatedAt() );
        memberEntity.updatedAt( member.getUpdatedAt() );
        memberEntity.memberId( member.getMemberId() );
        memberEntity.nickname( member.getNickname() );
        memberEntity.password( member.getPassword() );
        memberEntity.name( member.getName() );
        memberEntity.phone( member.getPhone() );
        memberEntity.email( member.getEmail() );
        memberEntity.position( member.getPosition() );
        memberEntity.profileImage( member.getProfileImage() );
        memberEntity.role( member.getRole() );
        memberEntity.agitUrl( member.getAgitUrl() );
        memberEntity.agitNotification( member.getAgitNotification() );
        memberEntity.emailNotification( member.getEmailNotification() );
        memberEntity.serviceNotification( member.getServiceNotification() );
        memberEntity.isDeleted( member.getIsDeleted() );

        return memberEntity.build();
    }

    protected List<CategoryEntity> categoryListToCategoryEntityList(List<Category> list) {
        if ( list == null ) {
            return null;
        }

        List<CategoryEntity> list1 = new ArrayList<CategoryEntity>( list.size() );
        for ( Category category : list ) {
            list1.add( categoryToCategoryEntity( category ) );
        }

        return list1;
    }

    protected CategoryEntity categoryToCategoryEntity(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryEntity.CategoryEntityBuilder<?, ?> categoryEntity = CategoryEntity.builder();

        categoryEntity.createdAt( category.getCreatedAt() );
        categoryEntity.updatedAt( category.getUpdatedAt() );
        categoryEntity.categoryId( category.getCategoryId() );
        categoryEntity.parent( categoryToCategoryEntity( category.getParent() ) );
        categoryEntity.name( category.getName() );
        categoryEntity.seq( category.getSeq() );
        categoryEntity.isDeleted( category.getIsDeleted() );
        categoryEntity.deletedAt( category.getDeletedAt() );
        categoryEntity.children( categoryListToCategoryEntityList( category.getChildren() ) );

        return categoryEntity.build();
    }

    protected Member memberEntityToMember(MemberEntity memberEntity) {
        if ( memberEntity == null ) {
            return null;
        }

        Member.MemberBuilder<?, ?> member = Member.builder();

        member.createdAt( memberEntity.getCreatedAt() );
        member.updatedAt( memberEntity.getUpdatedAt() );
        member.memberId( memberEntity.getMemberId() );
        member.nickname( memberEntity.getNickname() );
        member.password( memberEntity.getPassword() );
        member.name( memberEntity.getName() );
        member.phone( memberEntity.getPhone() );
        member.email( memberEntity.getEmail() );
        member.position( memberEntity.getPosition() );
        member.profileImage( memberEntity.getProfileImage() );
        member.role( memberEntity.getRole() );
        member.agitUrl( memberEntity.getAgitUrl() );
        member.agitNotification( memberEntity.getAgitNotification() );
        member.emailNotification( memberEntity.getEmailNotification() );
        member.serviceNotification( memberEntity.getServiceNotification() );
        member.isDeleted( memberEntity.getIsDeleted() );

        return member.build();
    }

    protected List<Category> categoryEntityListToCategoryList(List<CategoryEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<Category> list1 = new ArrayList<Category>( list.size() );
        for ( CategoryEntity categoryEntity : list ) {
            list1.add( categoryEntityToCategory( categoryEntity ) );
        }

        return list1;
    }

    protected Category categoryEntityToCategory(CategoryEntity categoryEntity) {
        if ( categoryEntity == null ) {
            return null;
        }

        Category.CategoryBuilder<?, ?> category = Category.builder();

        category.createdAt( categoryEntity.getCreatedAt() );
        category.updatedAt( categoryEntity.getUpdatedAt() );
        category.categoryId( categoryEntity.getCategoryId() );
        category.parent( categoryEntityToCategory( categoryEntity.getParent() ) );
        category.name( categoryEntity.getName() );
        category.seq( categoryEntity.getSeq() );
        category.isDeleted( categoryEntity.getIsDeleted() );
        category.deletedAt( categoryEntity.getDeletedAt() );
        category.children( categoryEntityListToCategoryList( categoryEntity.getChildren() ) );

        return category.build();
    }
}
