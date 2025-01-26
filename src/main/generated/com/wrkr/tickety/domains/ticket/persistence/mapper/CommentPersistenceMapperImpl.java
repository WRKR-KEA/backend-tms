package com.wrkr.tickety.domains.ticket.persistence.mapper;

import com.wrkr.tickety.domains.member.domain.model.Member;
import com.wrkr.tickety.domains.member.persistence.entity.MemberEntity;
import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Comment;
import com.wrkr.tickety.domains.ticket.domain.model.Ticket;
import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.CommentEntity;
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
public class CommentPersistenceMapperImpl implements CommentPersistenceMapper {

    @Override
    public CommentEntity toEntity(Comment domain) {
        if ( domain == null ) {
            return null;
        }

        CommentEntity.CommentEntityBuilder<?, ?> commentEntity = CommentEntity.builder();

        commentEntity.createdAt( domain.getCreatedAt() );
        commentEntity.updatedAt( domain.getUpdatedAt() );
        commentEntity.commentId( domain.getCommentId() );
        commentEntity.ticket( ticketToTicketEntity( domain.getTicket() ) );
        commentEntity.member( memberToMemberEntity( domain.getMember() ) );
        commentEntity.content( domain.getContent() );

        return commentEntity.build();
    }

    @Override
    public Comment toDomain(CommentEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Comment.CommentBuilder<?, ?> comment = Comment.builder();

        comment.createdAt( entity.getCreatedAt() );
        comment.updatedAt( entity.getUpdatedAt() );
        comment.commentId( entity.getCommentId() );
        comment.ticket( ticketEntityToTicket( entity.getTicket() ) );
        comment.member( memberEntityToMember( entity.getMember() ) );
        comment.content( entity.getContent() );

        return comment.build();
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

    protected TicketEntity ticketToTicketEntity(Ticket ticket) {
        if ( ticket == null ) {
            return null;
        }

        TicketEntity.TicketEntityBuilder<?, ?> ticketEntity = TicketEntity.builder();

        ticketEntity.createdAt( ticket.getCreatedAt() );
        ticketEntity.updatedAt( ticket.getUpdatedAt() );
        ticketEntity.ticketId( ticket.getTicketId() );
        ticketEntity.user( memberToMemberEntity( ticket.getUser() ) );
        ticketEntity.manager( memberToMemberEntity( ticket.getManager() ) );
        ticketEntity.category( categoryToCategoryEntity( ticket.getCategory() ) );
        ticketEntity.serialNumber( ticket.getSerialNumber() );
        ticketEntity.title( ticket.getTitle() );
        ticketEntity.content( ticket.getContent() );
        ticketEntity.status( ticket.getStatus() );
        ticketEntity.isPinned( ticket.getIsPinned() );

        return ticketEntity.build();
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

    protected Ticket ticketEntityToTicket(TicketEntity ticketEntity) {
        if ( ticketEntity == null ) {
            return null;
        }

        Ticket.TicketBuilder<?, ?> ticket = Ticket.builder();

        ticket.createdAt( ticketEntity.getCreatedAt() );
        ticket.updatedAt( ticketEntity.getUpdatedAt() );
        ticket.ticketId( ticketEntity.getTicketId() );
        ticket.user( memberEntityToMember( ticketEntity.getUser() ) );
        ticket.manager( memberEntityToMember( ticketEntity.getManager() ) );
        ticket.category( categoryEntityToCategory( ticketEntity.getCategory() ) );
        ticket.serialNumber( ticketEntity.getSerialNumber() );
        ticket.title( ticketEntity.getTitle() );
        ticket.content( ticketEntity.getContent() );
        ticket.status( ticketEntity.getStatus() );
        ticket.isPinned( ticketEntity.getIsPinned() );

        return ticket.build();
    }
}
