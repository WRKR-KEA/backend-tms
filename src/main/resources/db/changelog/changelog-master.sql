-- liquibase formatted sql

-- changeset Tomcat:add_column_member_kakaowork_notification
ALTER TABLE member
    ADD COLUMN kakaowork_notification BOOLEAN NOT NULL DEFAULT true;

-- changeset Phurray:add_column_category_abbreviation
ALTER TABLE category
    ADD COLUMN abbreviation VARCHAR(255) NOT NULL DEFAULT '';