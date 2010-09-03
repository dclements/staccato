-- creates the migration versions table
create table STACCATO_MIGRATIONS (
  id integer not null AUTO_INCREMENT,
  database_version    varchar(255) not null,  
  script_date       timestamp,
  script_filename   varchar(255) not null,
  script_hash       varchar(255) not null,
  workflow_step     varchar(255) not null,
  primary key (id)
);