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

-- changeset Wonee:add_notification_create_ddl
CREATE TABLE `notification` (
                                `notification_id` bigint NOT NULL AUTO_INCREMENT,
                                `created_at` datetime(6) NOT NULL,
                                `updated_at` datetime(6) NOT NULL,
                                `content` varchar(255) NOT NULL,
                                `is_read` bit(1) NOT NULL DEFAULT b'0',
                                `member_id` bigint NOT NULL,
                                `profile_image` varchar(255) NOT NULL,
                                `type` enum('COMMENT','REMIND','TICKET') NOT NULL,
                                PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;