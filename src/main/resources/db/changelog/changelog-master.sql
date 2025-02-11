-- liquibase formatted sql

-- changeset Tomcat:add_column_member_kakaowork_notification
ALTER TABLE member
    ADD COLUMN kakaowork_notification BOOLEAN NOT NULL DEFAULT true;