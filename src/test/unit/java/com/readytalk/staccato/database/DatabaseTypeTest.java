package com.readytalk.staccato.database;

import java.net.URI;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.migration.MigrationException;

public class DatabaseTypeTest {

	@Test
	public void testSuccessfulGetTypeFromJdbcUri() {
		URI uri = URI.create("jdbc:mysql://localhost:3306/foo");
		DatabaseType type = DatabaseType.getTypeFromJDBCUri(uri);

		Assert.assertEquals(type, DatabaseType.MYSQL);

		uri = URI.create("jdbc:postgresql://localhost:3306/foo");
		type = DatabaseType.getTypeFromJDBCUri(uri);

		Assert.assertEquals(type, DatabaseType.POSTGRESQL);
		
		uri = URI.create("jdbc:hsqldb:mem:testing");
		type = DatabaseType.getTypeFromJDBCUri(uri);

		Assert.assertEquals(type, DatabaseType.HSQLDB);
	}

	@Test
	public void testUnsuccessfulGetTypeFromJdbcUri() {
		URI uri = URI.create("jdbc:oracle:thin:@localhost:1521:orcl");

		try {
			DatabaseType.getTypeFromJDBCUri(uri);
			Assert.fail("should have thrown, no support for: " + uri);
		} catch (MigrationException e) {
			// no-op, test successful
		}
	}
}
