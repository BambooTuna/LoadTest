DROP SCHEMA IF EXISTS loadtest;
CREATE SCHEMA loadtest;
USE loadtest;

DROP TABLE IF EXISTS user;
CREATE TABLE user
(
  id            bigint(20) NOT NULL,
  name          VARCHAR(255) NOT NULL,
  age           bigint(20) NOT NULL,
  `created_at`  datetime(6) NOT NULL,
  `updated_at`  datetime(6) NOT NULL,
  PRIMARY KEY  id (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;