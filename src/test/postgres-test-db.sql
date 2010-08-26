-- create user and grant permissions as the postgres user
CREATE USER staccato WITH PASSWORD 'staccato';
CREATE DATABASE staccato WITH OWNER staccato;
GRANT ALL PRIVILEGES ON DATABASE staccato TO staccato;

-- connect to the staccato database with the staccato user and run the below scripts
create table foo (
  id                 serial,
  bar                varchar(50) not null,

  primary key (id)
);
insert into foo(id, bar) values(1, 'baz');