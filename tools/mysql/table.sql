DROP SCHEMA IF EXISTS tunacurl;
CREATE SCHEMA tunacurl;
USE tunacurl;

DROP TABLE IF EXISTS test_table;
CREATE TABLE test_table
(
  id mediumint(9) NOT NULL AUTO_INCREMENT,
  text VARCHAR(255) DEFAULT 'Nothing' NOT NULL,
  PRIMARY KEY  id (id)
);