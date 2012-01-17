Staccato
========

Staccato is an imperative database versioning tool written in Java.

Compiling, Testing, and Installing Staccato
-------------------------------------------

### Building

Maven is currently the default building solution.  Once you have maven installed, you can type the following from the command line to build the project:

`mvn compile`

### Unit Testing

The built-in unit testing is done with TestNG.  At this time, staccato is built to work with postgres, mysql, and hsqldb.  In order to run the unit test suite successfully with mysql and postgres, you'll have to prepare both databases by executing the sql in the following files:

* `src/test/mysql-test-db.sql`
* `src/test/postgres-test-db.sql`

The HSQLDB usage is file-based with the files checked into version control, so there should not be any additional configuration required there.  `src/test/hsqldb-test-db.sql` contains the necessary commands to recreate the databases, should it be desired. 

Once you have your databases setup successfully, run the following command to execute the unit test suite:

`mvn test`

At this time the mysql tests are temporarily disabled until a more robust solution to testing configuration can be implemented. 

There are also a small number of JUnit based tests that do not have any outside dependencies that can be run directly, but are not part of the automated suite at this point in time.

### Installing a Jar

To create a jar and install it to your local maven repository, execute the following:

`mvn clean install`
