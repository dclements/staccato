Staccato
========

Staccato is an imperative database versioning tool written in Java.

Compiling, Testing, and Installing Staccato
---------------------------------

### Building

Maven is currently the default building solution.  Once you have maven installed, you can type the following from the command line to build the project:

`mvn compile`

### Unit Testing

The built-in unit testing is done with TestNG.  Staccato is built to work with both postgres and mysql so in order to run the unit test suite successfully, you'll have to prepare both databases by executing the sql in the following files:

* src/test/mysql-test-db.sql
* src/test/postgres-test-db.sql

Once you have your databases setup successfully, run the following command to execute the unit test suite:

`mvn test`

At this time the mysql tests are temporarily disabled until a more robust solution to testing can be implemented.  There are also a small number of JUnit based tests that do not have any outside dependencies that can be run directly.

### Installing a Jar

To create a jar and install it to your local maven repository, execute the following:

`mvn clean install`
