-- creates the migration versions table
create table STACCATO_MIGRATIONS (
  id                serial,
  database_version    varchar(255) not null,  
  script_date       timestamp with time zone not null,
  script_filename   varchar(255) not null,
  script_hash       varchar(255) not null,
  workflow_step     varchar(255) not null,
  primary key (id)
);