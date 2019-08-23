DROP SCHEMA IF EXISTS loadtest;
CREATE SCHEMA loadtest;
USE loadtest;

DROP TABLE IF EXISTS user;
CREATE TABLE user
(
  `user_id`     bigint(20) NOT NULL,
  `user_name`   VARCHAR(255) NOT NULL,
  `user_age`    bigint(20) NOT NULL,
  `created_at`  datetime(6) NOT NULL,
  `updated_at`  datetime(6) NOT NULL,
  PRIMARY KEY  `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;