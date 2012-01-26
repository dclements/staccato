package com.readytalk.staccato.database;
import static com.readytalk.staccato.database.DatabaseType.HSQLDB;
import static com.readytalk.staccato.database.DatabaseType.MYSQL;
import static com.readytalk.staccato.database.DatabaseType.POSTGRESQL;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import javax.validation.constraints.Null;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.readytalk.staccato.database.migration.MigrationException;

@RunWith(Parameterized.class)
public class DatabaseTypeTest {
	private final URI uri;
	private final DatabaseType type;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Parameters
	public static Collection<Object []> data() {
		return Arrays.asList(new Object [] [] {
				{"jdbc:mysql://localhost:3306/foo", MYSQL},
				{"jdbc:postgresql://localhost:5432/foo", POSTGRESQL},
				{"jdbc:hsqldb:mem:staccato", HSQLDB},
				{"jdbc:oracle:thin:@localhost:1521:orcl", null}
		});
	}
	
	public DatabaseTypeTest(String uri, @Null DatabaseType type) {
		this.uri = URI.create(uri);
		this.type = type;
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTypeFromJDBCUri() {
		if(type == null) {
			thrown.expect(MigrationException.class);
		}
		assertEquals(type, DatabaseType.getTypeFromJDBCUri(uri));
	}

}
