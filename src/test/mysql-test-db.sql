CREATE USER 'database_manager'@'localhost' IDENTIFIED BY 'database_manager';
CREATE DATABASE database_manager_test;
GRANT ALL PRIVILEGES ON database_manager_test.* TO 'database_manager'@'localhost';
create table foo (
  id                 int,
  bar                varchar(50) not null,

  primary key (id)
);
insert into foo(id, bar) values(1, 'baz');
