drop table if exists oauth1_tokens;

/**
Create the token store for oauth1 tokens.
 */
create table oauth1_tokens (
  token varchar(255) not null,
  callbackUrl varchar(255) default '',
  verifier varchar(255) default '',
  secret varchar(255) not null,
  consumerKey varchar(255) not null,
  isAccessToken bit(1) not null,
  tokenTimestamp bigint,
  userAuthentication blob,

  primary key (token)
);

INSERT INTO oauth1_tokens (token, callbackUrl, verifier, secret, consumerKey, isAccessToken, tokenTimestamp, userAuthentication)
VALUES
	('dummy', NULL, NULL, 'vgjbYn7jGSlWiCVqvjcpkUET2SBiA0DAzl/fBfq49CFuHzQug21dLkgnMfMuIB6kl8kajoWmpc0Ob8OdvOH3rELxgb5Ax4CBL1EKGjKgmd4=', 'https://testsp.dev.surfconext.nl/shibboleth', 1, 1339591034217, null);
