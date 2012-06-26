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
