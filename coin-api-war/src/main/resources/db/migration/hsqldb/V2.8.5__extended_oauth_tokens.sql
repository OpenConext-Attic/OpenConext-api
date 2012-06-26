/*
Recreate table, because "alter table add column after ..." does not work in hsqldb.
 */
drop table if exists oauth_access_token;

create table oauth_access_token (
  token_id varchar(255),
  token blob,
  authentication_id varchar(255),
  user_name varchar(255),
  client_id varchar(255),
  client_entity_id varchar(255),
  authentication blob,
  refresh_token varchar(255),

  primary key (token_id)
);


/*
Recreate table, because "alter table add column after ..." does not work in hsqldb.
 */
drop table if exists oauth1_tokens;
create table oauth1_tokens (
  token varchar(255) not null,
  callbackUrl varchar(255) default '',
  verifier varchar(255) default '',
  secret varchar(255) not null,
  consumerKey varchar(255) not null,
  userId varchar(255),
  isAccessToken bit(1) not null,
  tokenTimestamp bigint,
  userAuthentication blob,

  primary key (token)
);

INSERT INTO oauth1_tokens (token, callbackUrl, verifier, secret, consumerKey, userId, isAccessToken, tokenTimestamp,
userAuthentication)
VALUES
	('dummy', NULL, NULL,
	'vgjbYn7jGSlWiCVqvjcpkUET2SBiA0DAzl/fBfq49CFuHzQug21dLkgnMfMuIB6kl8kajoWmpc0Ob8OdvOH3rELxgb5Ax4CBL1EKGjKgmd4=',
	'https://testsp.dev.surfconext.nl/shibboleth', 'dummyUser', 1, 1339591034217, null);
