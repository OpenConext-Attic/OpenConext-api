drop table if exists saml_persistent_id;

create table saml_persistent_id (persistent_id varchar(255), user_uuid varchar(255) not null, service_provider_uuid varchar(255) not null);

insert into saml_persistent_id (persistent_id, user_uuid, service_provider_uuid) values ('persistent', 'user_uuid', 'whatever');
