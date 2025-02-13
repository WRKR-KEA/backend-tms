-- liquibase formatted sql

-- changeset Tomcat:add_column_member_kakaowork_notification
ALTER TABLE member
    ADD COLUMN kakaowork_notification BOOLEAN NOT NULL DEFAULT true;

-- changeset Phurray:add_column_category_abbreviation
ALTER TABLE category
    ADD COLUMN abbreviation VARCHAR(255) NOT NULL DEFAULT '';

-- changeset Thama:add_constraint_member_notnull_profile_image
ALTER TABLE member
    MODIFY profile_image VARCHAR(255) NOT NULL DEFAULT 'https://i.ibb.co/7Fd4Hhx/tickety-default-image.jpg';
