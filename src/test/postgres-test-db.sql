CREATE USER database_manager WITH PASSWORD 'database_manager';
CREATE DATABASE database_manager_test WITH OWNER database_manager;
GRANT ALL PRIVILEGES ON DATABASE database_manager_test TO database_manager;

-- connect to your database_manager_test with the database_manager user and run the below scripts

create table foo (
  id                 serial,
  bar                varchar(50) not null,

  primary key (id)
);
insert into foo(id, bar) values(1, 'baz');
