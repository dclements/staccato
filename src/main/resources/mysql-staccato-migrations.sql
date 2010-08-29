-- creates the migration versions table
create table STACCATO_MIGRATIONS (
  id integer not null AUTO_INCREMENT,
  script_date       timestamp,
  script_version    varchar(255) not null,
  script_filename   varchar(255) not null,
  script_hash       varchar(255) not null,
  primary key (id)
);