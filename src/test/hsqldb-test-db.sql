-- create user and grant permissions as the postgres user
CREATE USER staccato PASSWORD staccato admin;

-- connect to the staccato database with the staccato user and run the below scripts
--  > psql -u staccato
create table foo (
  id                 int identity,
  bar               varchar(50) not null,
);
insert into foo(id, bar) values(1, 'baz');