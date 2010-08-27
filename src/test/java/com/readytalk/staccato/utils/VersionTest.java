package com.readytalk.staccato.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * <p>
 * Version Tester.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class VersionTest {

  @Test
  public void testRegularExpressionString() {
    assertTrue("1.0".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("1.0.1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertFalse("1-0-1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.0".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-A1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-a1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-B1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-b1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-m1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-M1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-alpha1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-alpha".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-ALPHA1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-ALPHA".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-beta1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-beta".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-BETA1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-BETA".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-milestone1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-milestone".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-MILESTONE1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-MILESTONE".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-rc1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-rc".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-RC1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-RC".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-SNAPSHOT".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-snapshot".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-snapshot1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-SNAPSHOT1".matches(Version.REGULAR_EXPRESSION_STRING));

    assertTrue("234.2343.234234-SNAPSHOT1-IB1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-SNAPSHOT1-ib1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-IB1".matches(Version.REGULAR_EXPRESSION_STRING));
    assertTrue("234.2343.234234-ib1".matches(Version.REGULAR_EXPRESSION_STRING));
  }

  @Test
  public void testInts() throws Exception {
    Version v = new Version(1, 1, 2);
    assertEquals(1, v.getMajor());
    assertEquals(1, v.getMinor());
    assertEquals(2, v.getPatch());

    v = new Version(0, 0, 0);
    assertEquals(0, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());

    try {
      new Version(-1, 0, 0);
      fail("Should have failed");
    } catch (Exception e) {
    }

    try {
      new Version(0, -1, 0);
      fail("Should have failed");
    } catch (Exception e) {
    }

    try {
      new Version(0, 0, -1);
      fail("Should have failed");
    } catch (Exception e) {
    }
  }

  @Test
  public void testString() throws Exception {
    Version v = new Version("10.100.2000");
    assertEquals(10, v.getMajor());
    assertEquals(100, v.getMinor());
    assertEquals(2000, v.getPatch());
    assertNull(v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isSnapshot());
    assertTrue(v.isPatchVersion());
    new Version("10.100.2000", true);

    v = new Version("0.0.0");
    assertEquals(0, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertNull(v.getAdditional());
    assertFalse(v.isMinorVersion() || v.isPatchVersion() || v.isSnapshot());
    assertTrue(v.isMajorVersion());
    new Version("0.0.0", true);

    v = new Version("17");
    assertEquals(17, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertNull(v.getAdditional());
    assertFalse(v.isMinorVersion() || v.isPatchVersion() || v.isSnapshot());
    assertTrue(v.isMajorVersion());
    new Version("17", true);

    v = new Version("3.4");
    assertEquals(3, v.getMajor());
    assertEquals(4, v.getMinor());
    assertEquals(0, v.getPatch());
    assertNull(v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isPatchVersion() || v.isSnapshot());
    assertTrue(v.isMinorVersion());
    new Version("3.4", true);

    v = new Version("3.4.8");
    assertEquals(3, v.getMajor());
    assertEquals(4, v.getMinor());
    assertEquals(8, v.getPatch());
    assertNull(v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isSnapshot());
    assertTrue(v.isPatchVersion());
    new Version("3.4.8", true);

    v = new Version("3.4-RC4");
    assertEquals(3, v.getMajor());
    assertEquals(4, v.getMinor());
    assertEquals(0, v.getPatch());
    assertEquals("RC4", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    new Version("3.4-RC4", true);

    v = new Version("3-RC4");
    assertEquals(3, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertEquals("RC4", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    new Version("3-RC4", true);

    v = new Version("3.4.7-RC4");
    assertEquals(3, v.getMajor());
    assertEquals(4, v.getMinor());
    assertEquals(7, v.getPatch());
    assertEquals("RC4", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    new Version("3.4.7-RC4", true);

    v = new Version("3.4.7-SNAPSHOT4");
    assertEquals(3, v.getMajor());
    assertEquals(4, v.getMinor());
    assertEquals(7, v.getPatch());
    assertEquals("SNAPSHOT4", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    new Version("3.4.7-SNAPSHOT4", true);

    v = new Version("1.0.0-ga1");
    assertEquals(1, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertEquals("ga1", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    try {
      new Version("1.0.0-ga1", true);
      fail("Should have failed");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    v = new Version("1.0.0-foo");
    assertEquals(1, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertEquals("foo", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    try {
      new Version("1.0.0-foo", true);
      fail("Should have failed");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    v = new Version("1.0.0-20070530");
    assertEquals(1, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertEquals("20070530", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    try {
      new Version("1.0.0-20070530", true);
      fail("Should have failed");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    v = new Version("1.0-20070530");
    assertEquals(1, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertEquals("20070530", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    try {
      new Version("1.0-20070530", true);
      fail("Should have failed");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    v = new Version("foo");
    assertEquals(0, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertEquals("foo", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    try {
      new Version("foo", true);
      fail("Should have failed");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    v = new Version("0.0.foo");
    assertEquals(0, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertEquals("foo", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    try {
      new Version("0.0.foo", true);
      fail("Should have failed");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    v = new Version("0.foo.0");
    assertEquals(0, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertEquals("foo.0", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    try {
      new Version("0.foo.0", true);
      fail("Should have failed");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    v = new Version("0foo0foo1");
    assertEquals(0, v.getMajor());
    assertEquals(0, v.getMinor());
    assertEquals(0, v.getPatch());
    assertEquals("0foo0foo1", v.getAdditional());
    assertFalse(v.isMajorVersion() || v.isMinorVersion() || v.isPatchVersion());
    assertTrue(v.isSnapshot());
    try {
      new Version("0foo0foo1", true);
      fail("Should have failed");
    } catch (IllegalArgumentException iae) {
      // Expected
    }

    try {
      new Version("-1.0.0");
      fail("Should have failed");
    } catch (Exception e) {
    }

    try {
      new Version("-1.0.0", true);
      fail("Should have failed");
    } catch (Exception e) {
    }

    try {
      new Version("0.-1.0");
      fail("Should have failed");
    } catch (Exception e) {
    }

    try {
      new Version("0.-1.0", true);
      fail("Should have failed");
    } catch (Exception e) {
    }

    try {
      new Version("0.0.-1");
      fail("Should have failed");
    } catch (Exception e) {
    }

    try {
      new Version("0.0.-1", true);
      fail("Should have failed");
    } catch (Exception e) {
    }

    try {
      new Version("1.0.0-");
      fail("Should have failed");
    } catch (Exception e) {
    }

    try {
      new Version("1.0.0-", true);
      fail("Should have failed");
    } catch (Exception e) {
    }
  }

  @Test
  public void testCompare() throws Exception {
    Version v = new Version("1");
    Version v2 = new Version("2");
    assertTrue(v.compareTo(v2) < 0);
    assertTrue(v2.compareTo(v) > 0);

    v = new Version("1.8");
    v2 = new Version("1.7");
    assertTrue(v.compareTo(v2) > 0);
    assertTrue(v2.compareTo(v) < 0);

    v = new Version("1.8.34");
    v2 = new Version("1.8.12");
    assertTrue(v.compareTo(v2) > 0);
    assertTrue(v2.compareTo(v) < 0);

    v = new Version("1");
    v2 = new Version("1");
    assertTrue(v.compareTo(v2) == 0);
    assertTrue(v2.compareTo(v) == 0);

    v = new Version("10.1");
    v2 = new Version("10.1");
    assertTrue(v.compareTo(v2) == 0);
    assertTrue(v2.compareTo(v) == 0);

    v = new Version("22.10.23");
    v2 = new Version("22.10.23");
    assertTrue(v.compareTo(v2) == 0);
    assertTrue(v2.compareTo(v) == 0);

    v = new Version("1.1.0-RC4");
    v2 = new Version("1.1.0-RC5");
    assertTrue(v.compareTo(v2) < 0);
    assertTrue(v2.compareTo(v) > 0);

    v = new Version("1.1.0-alpha4");
    v2 = new Version("1.1.0-alpha5");
    assertTrue(v.compareTo(v2) < 0);
    assertTrue(v2.compareTo(v) > 0);

    v = new Version("1.1.0-alpha4");
    v2 = new Version("1.1.0-M5");
    assertTrue(v.compareTo(v2) < 0);
    assertTrue(v2.compareTo(v) > 0);

    v = new Version("1.1.6-20070215");
    v2 = new Version("1.1.6-20070530");
    assertTrue(v.compareTo(v2) < 0);
    assertTrue(v2.compareTo(v) > 0);

    v = new Version("1.1.6-frank");
    v2 = new Version("1.1.6-bob");
    assertTrue(v2.compareTo(v) < 0);
    assertTrue(v.compareTo(v2) > 0);

    v = new Version("1.1.6-RC");
    v2 = new Version("1.1.6-RC3");
    assertTrue(v2.compareTo(v) < 0);
    assertTrue(v.compareTo(v2) > 0);

    // Integrations
    v = new Version("1.1.6-RC3");
    v2 = new Version("1.1.6-RC3-IB1");
    assertTrue(v2.compareTo(v) < 0);
    assertTrue(v.compareTo(v2) > 0);

    v = new Version("1.1.6-RC3-IB1");
    v2 = new Version("1.1.6-RC3");
    assertTrue(v2.compareTo(v) > 0);
    assertTrue(v.compareTo(v2) < 0);

    v = new Version("1.1.6-RC3-IB1");
    v2 = new Version("1.1.6-RC3-IB2");
    assertTrue(v2.compareTo(v) > 0);
    assertTrue(v.compareTo(v2) < 0);

    v = new Version("1.1.6-IB1");
    v2 = new Version("1.1.6-RC3-IB2");
    assertTrue(v2.compareTo(v) < 0);
    assertTrue(v.compareTo(v2) > 0);

    v = new Version("1.1.6-IB1");
    v2 = new Version("1.1.6-RC3");
    assertTrue(v2.compareTo(v) < 0);
    assertTrue(v.compareTo(v2) > 0);
  }

  @Test
  public void testEquals() {
    Version v = new Version("1");
    Version v2 = new Version("1");
    assertTrue(v.equals(v2));

    v = new Version("1.1");
    v2 = new Version("1.1");
    assertTrue(v.equals(v2));

    v = new Version("1.1.6");
    v2 = new Version("1.1.6");
    assertTrue(v.equals(v2));

    v = new Version("1.1.6-RC4");
    v2 = new Version("1.1.6-RC4");
    assertTrue(v.equals(v2));

    v = new Version("1.1.6-alpha");
    v2 = new Version("1.1.6-alpha");
    assertTrue(v.equals(v2));

    v = new Version("1.1.6-frank");
    v2 = new Version("1.1.6-frank");
    assertTrue(v.equals(v2));
  }

  @Test
  public void testisPatchVersion() {

    {
      Version v = new Version("0.0.1");
      assertTrue(v.isPatchVersion());
    }

    {
      Version v = new Version("0.1.1");
      assertTrue(v.isPatchVersion());
    }

    {
      Version v = new Version("1.0.1");
      assertTrue(v.isPatchVersion());
    }

    {
      Version v = new Version("1.1.1");
      assertTrue(v.isPatchVersion());
    }
  }
}