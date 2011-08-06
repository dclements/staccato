-- create the following user and grant as root
CREATE USER 'staccato'@'localhost' IDENTIFIED BY 'staccato';
CREATE DATABASE staccato;
GRANT ALL PRIVILEGES ON staccato.* TO 'staccato'@'localhost';

-- this database is used for running @Create unit tests
CREATE DATABASE staccato_root;
GRANT ALL PRIVILEGES ON staccato_root.* TO 'staccato'@'localhost';

-- connect to the new staccato database and run the sql below
-- use staccato;
create table foo (
  id                 int,
  bar                varchar(50) not null,

  primary key (id)
);
insert into foo(id, bar) values(1, 'baz');
