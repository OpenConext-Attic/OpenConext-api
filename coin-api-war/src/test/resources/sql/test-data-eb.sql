create table saml_persistent_id (
  persistent_id varchar(255) not null,
  user_uuid varchar(255) not null,
  service_provider_uuid varchar(255) not null,
  primary key (persistent_id)
);
insert into saml_persistent_id (persistent_id, user_uuid, service_provider_uuid) values ('d8ec7126a4afa7f7c3bd9f622987c4db1acbcece', 'a97289c0-1c12-11e1-bb07-3716ae36463c', '635cd670-0f5d-11e1-a5b4-c51cf1e48ad3');
insert into saml_persistent_id (persistent_id, user_uuid, service_provider_uuid) values ('persistent', 'user_uuid', '635cd670-0f5d-11e1-a5b4-c51cf1e48ad3');
  