drop table if exists oauth1_tokens;

create table oauth1_tokens (
  token varchar(255) not null,
  callbackUrl varchar(255) not null,
  verifier varchar(255) not null,
  secret varchar(255) not null,
  consumerKey varchar(255) not null,
  isAccessToken bit(1) not null,
  tokenTimestamp bigint
);