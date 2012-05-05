create table oauth_access_token (
  token_id varchar(255),
  token blob,
  authentication_id varchar(255),
  user_name varchar(255),
  client_id varchar(255),
  authentication blob,
  refresh_token varchar(255),

  primary key (token_id)
);

/*
Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.VARCHAR });
 */

create table oauth_refresh_token (
token_id varchar(255),
token blob,
authentication blob,

primary key (token_id)
);

/*
{ Types.VARCHAR,Types.BLOB, Types.BLOB });
*/