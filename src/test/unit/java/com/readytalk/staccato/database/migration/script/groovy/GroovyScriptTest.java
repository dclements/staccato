package com.readytalk.staccato.database.migration.script.groovy;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.readytalk.staccato.database.DatabaseType;

/**
 * @author jhumphrey
 */
public class GroovyScriptTest {

  @Test
  public void testNotEquals() {

    DateTime script1Date = new DateTime();
    MutableDateTime futureFromScript1 = new MutableDateTime(script1Date);
    futureFromScript1.addHours(1);

    GroovyScript script1 = new GroovyScript();
    GroovyScript script2 = new GroovyScript();

    // same database types with different dates
    script1.setDatabaseType(DatabaseType.MYSQL);
    script1.setScriptDate(script1Date);
    script2.setDatabaseType(DatabaseType.MYSQL);
    script2.setScriptDate(futureFromScript1.toDateTime());
    Assert.assertFalse(script1.equals(script2));

    // database types NULL, different dates
    script1.setDatabaseType(null);
    script2.setDatabaseType(null);
    Assert.assertFalse(script1.equals(script2));

    // database types different but same dates
    script1.setDatabaseType(DatabaseType.MYSQL);
    script1.setScriptDate(script1Date);
    script2.setDatabaseType(DatabaseType.POSTGRESQL);
    script2.setScriptDate(script1Date);
    Assert.assertFalse(script1.equals(script2));
  }

  @Test
  public void testEquals() {
    DateTime date = new DateTime();

    GroovyScript script1 = new GroovyScript();
    GroovyScript script2 = new GroovyScript();

    // same database types with same dates
    script1.setDatabaseType(DatabaseType.MYSQL);
    script1.setScriptDate(date);
    script2.setDatabaseType(DatabaseType.MYSQL);
    script2.setScriptDate(date);
    Assert.assertTrue(script1.equals(script2));

    // database types NULL, different dates
    script1.setDatabaseType(null);
    script2.setDatabaseType(null);
    Assert.assertTrue(script1.equals(script2));
  }
}
