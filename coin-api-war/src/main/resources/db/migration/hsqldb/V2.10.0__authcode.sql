CREATE TABLE oauth_code (
  code varchar(255) NOT NULL,
  authentication blob NOT NULL,
  PRIMARY KEY (code)
);
