drop table if exists api_call_log;

create table api_call_log (
  id bigint,
  log_timestamp timestamp not null,
  user_id varchar(1000) default null,
  spentity_id varchar(1000) default null,
  ip_address varchar(1000) default null,
  api_version varchar(1000) default null,
  resource_url varchar(1000) default null,
  consumer_key varchar(1024) default null
) ;
