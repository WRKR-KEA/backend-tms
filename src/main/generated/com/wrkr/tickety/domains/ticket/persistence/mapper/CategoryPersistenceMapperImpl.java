package com.wrkr.tickety.domains.ticket.persistence.mapper;

import com.wrkr.tickety.domains.ticket.domain.model.Category;
import com.wrkr.tickety.domains.ticket.persistence.entity.CategoryEntity;
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
public class CategoryPersistenceMapperImpl implements CategoryPersistenceMapper {

    @Override
    public CategoryEntity toEntity(Category domain) {
        if ( domain == null ) {
            return null;
        }

        CategoryEntity.CategoryEntityBuilder<?, ?> categoryEntity = CategoryEntity.builder();

        categoryEntity.createdAt( domain.getCreatedAt() );
        categoryEntity.updatedAt( domain.getUpdatedAt() );
        categoryEntity.categoryId( domain.getCategoryId() );
        categoryEntity.parent( categoryToCategoryEntity( domain.getParent() ) );
        categoryEntity.name( domain.getName() );
        categoryEntity.seq( domain.getSeq() );
        categoryEntity.isDeleted( domain.getIsDeleted() );
        categoryEntity.deletedAt( domain.getDeletedAt() );
        categoryEntity.children( categoryListToCategoryEntityList( domain.getChildren() ) );

        return categoryEntity.build();
    }

    @Override
    public Category toDomain(CategoryEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Category.CategoryBuilder<?, ?> category = Category.builder();

        category.createdAt( entity.getCreatedAt() );
        category.updatedAt( entity.getUpdatedAt() );
        category.categoryId( entity.getCategoryId() );
        category.parent( categoryEntityToCategory( entity.getParent() ) );
        category.name( entity.getName() );
        category.seq( entity.getSeq() );
        category.isDeleted( entity.getIsDeleted() );
        category.deletedAt( entity.getDeletedAt() );
        category.children( categoryEntityListToCategoryList( entity.getChildren() ) );

        return category.build();
    }

    protected List<CategoryEntity> categoryListToCategoryEntityList(List<Category> list) {
        if ( list == null ) {
            return null;
        }

        List<CategoryEntity> list1 = new ArrayList<CategoryEntity>( list.size() );
        for ( Category category : list ) {
            list1.add( toEntity( category ) );
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
        categoryEntity.parent( toEntity( category.getParent() ) );
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
            list1.add( toDomain( categoryEntity ) );
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
        category.parent( toDomain( categoryEntity.getParent() ) );
        category.name( categoryEntity.getName() );
        category.seq( categoryEntity.getSeq() );
        category.isDeleted( categoryEntity.getIsDeleted() );
        category.deletedAt( categoryEntity.getDeletedAt() );
        category.children( categoryEntityListToCategoryList( categoryEntity.getChildren() ) );

        return category.build();
    }
}
