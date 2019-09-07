DROP SCHEMA IF EXISTS loadtest;
CREATE SCHEMA loadtest;
USE loadtest;

DROP TABLE IF EXISTS user;
CREATE TABLE user
(
  `user_id`                 VARCHAR(255) NOT NULL,
  `advertiser_id`           int NOT NULL,
  `game_install_count`      int NOT NULL,
  `game_login_count`        int NOT NULL,
  `game_paid_count`         int NOT NULL,
  `game_tutorial_count`     int NOT NULL,
  `game_extension_count`    int NOT NULL,
  PRIMARY KEY  `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO user (`user_id`, `advertiser_id`, `game_install_count`, `game_login_count`, `game_paid_count`, `game_tutorial_count`, `game_extension_count`) VALUES ("test_id", 1, 2, 2, 2, 2, 2);

DROP TABLE IF EXISTS budget;
CREATE TABLE budget
(
  `advertiser_id`       int NOT NULL,
  `budget_type`         int NOT NULL,
  `budget_balance`      DOUBLE(7,2) NOT NULL,
  `create_at`           datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO budget (`advertiser_id`, `budget_type`, `budget_balance`, `create_at`) VALUES (1, 0, 10000, "2000-01-01 00:00:01");
INSERT INTO budget (`advertiser_id`, `budget_type`, `budget_balance`, `create_at`) VALUES (1, 1, -100, "2001-01-01 00:00:01");
INSERT INTO budget (`advertiser_id`, `budget_type`, `budget_balance`, `create_at`) VALUES (1, 1, -50, "2002-01-01 00:00:01");