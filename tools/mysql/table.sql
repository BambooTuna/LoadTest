DROP SCHEMA IF EXISTS loadtest;
CREATE SCHEMA loadtest;
USE loadtest;

DROP TABLE IF EXISTS sample_table;
CREATE TABLE sample_table
(
  id mediumint(9) NOT NULL AUTO_INCREMENT,
  text VARCHAR(255) DEFAULT 'Nothing' NOT NULL,
  PRIMARY KEY  id (id)
);