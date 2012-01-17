Rough TODO List
===============

This document is not really a task list, but more of a few vague ideas for refactoring and feature development.

Refactoring, Cleanup, and Bug Fixes
-----------------------------------

<dl>
<dt>Unit tests should not require a database or filesystem.</dt>
<dd>Many of the current unit tests are closer to integration tests and require particular file layouts and database configurations.  We can add to these with sets of true unit tests that take better advantage of mocks.</dd>
<dt>Unit/integration tests should not require specific setups of multiple databases.</dt>
<dd>Configuration should be handled within the tests as much as possible, with Assumption tests to check if the tests being
run are actually failing or if they are simply not valid.</dd>
<dt>Integration tests should work consistently when run back-to-back.</dt>
<dd>Sometimes running the current unit tests without a `clean` step results in one or more of the tests failing.</dd>
<dt>Take better advantage of <a href="http://code.google.com/p/google-guice/">guice</a>, especially for unit tests.</dt>
<dd>While the current setup takes heavy advantage of <a href="http://code.google.com/p/google-guice/">guice</a>, it is a little cumbersome to work with for unit testing.</dd>
<dt>Restructuring for easier testing</dt>
<dd>There are a few places where a little refactoring could make the bulk of the code easier to unit test.</dd>
<dt>Better command-line Javadocs/<a href="http://findbugs.sourceforge.net/">Findbugs</a>/<a href="http://checkstyle.sourceforge.net/">Checkstyle</a> support</dt>
<dd>Currently not generating reports for these, could be useful for future development.</dd>
</dl>

Features
--------

<dl>
<dt>Execute In Transactions</dt>
<dd>When scripts run, either in part or in whole, they should be run in a transaction to enable easy rollback if something goes wrong.</dd>
<dt>Alternative Language Support</dt>
<dd>Currently the only real support is for Java and Groovy. It'd be nice to extend this to other languages that run on the JVM, such as Scala.</dd>
<dt>Down Support</dt>
<dd>Currently the emphasis is on upgrading the database. Should also support rollbacks.</dd>
<dt>Maintenance</dt>
<dd>
Working on a database frequently involves occasional maintenance operations such as reindexing. There's a request to put these into a separate table.
</dd>
</dl>

