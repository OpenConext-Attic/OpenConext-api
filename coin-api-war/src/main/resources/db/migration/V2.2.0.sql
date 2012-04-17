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