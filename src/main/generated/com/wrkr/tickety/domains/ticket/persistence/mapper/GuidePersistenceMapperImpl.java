package com.wrkr.tickety.domains.ticket.persistence.mapper;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.domain.model.Guide;
import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
import com.wrkr.tickety.domains.ticket.persistence.entity.GuideEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-25T17:50:21+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.2 (Homebrew)"
)
@Component
public class GuidePersistenceMapperImpl implements GuidePersistenceMapper {

    @Override
    public GuideEntity toEntity(Guide domain) {
        if ( domain == null ) {
            return null;
        }

        GuideEntity.GuideEntityBuilder<?, ?> guideEntity = GuideEntity.builder();

        guideEntity.createdAt( domain.getCreatedAt() );
        guideEntity.updatedAt( domain.getUpdatedAt() );
        guideEntity.guideId( domain.getGuideId() );
        guideEntity.category( categoryToCategoryEntity( domain.getCategory() ) );
        guideEntity.content( domain.getContent() );

        return guideEntity.build();
    }

    @Override
    public Guide toDomain(GuideEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Guide.GuideBuilder<?, ?> guide = Guide.builder();

        guide.createdAt( entity.getCreatedAt() );
        guide.updatedAt( entity.getUpdatedAt() );
        guide.guideId( entity.getGuideId() );
        guide.category( categoryEntityToCategory( entity.getCategory() ) );
        guide.content( entity.getContent() );

        return guide.build();
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
