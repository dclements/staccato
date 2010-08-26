CREATE USER 'staccato'@'localhost' IDENTIFIED BY 'staccato';
CREATE DATABASE staccato;
GRANT ALL PRIVILEGES ON staccato.* TO 'staccato'@'localhost';
create table foo (
  id                 int,
  bar                varchar(50) not null,

  primary key (id)
);
insert into foo(id, bar) values(1, 'baz');
