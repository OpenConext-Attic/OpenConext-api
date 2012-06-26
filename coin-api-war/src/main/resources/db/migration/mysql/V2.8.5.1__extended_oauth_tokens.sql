delete from oauth_access_token;
alter table oauth1_tokens add column userId varchar(1000) default null after consumerKey;