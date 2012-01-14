package com.readytalk.staccato.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.Test;

/**
 * VersionRange Tester.
 */
public class VersionRangeTest {

	@Test
	public void testVersionConstructor() {
		Version start = new Version("1.0");
		Version end = new Version("2.0");
		VersionRange vr = new VersionRange(start, end);
		assertSame(start, vr.getStart());
		assertSame(end, vr.getEnd());

		vr = new VersionRange(start, null);
		assertSame(start, vr.getStart());
		assertNull(vr.getEnd());

		try {
			new VersionRange(end, start);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
		}

		try {
			new VersionRange(null, start);
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testStringConstructor() {
		VersionRange vr = new VersionRange("2.0");
		assertEquals(new Version("2.0.0"), vr.getStart());
		assertEquals(new Version("2.0.0"), vr.getEnd());

		vr = new VersionRange("*");
		assertEquals(new Version("0.0.0"), vr.getStart());
		assertNull(vr.getEnd());

		vr = new VersionRange("2.*");
		assertEquals(new Version("2.0.0"), vr.getStart());
		assertEquals(new Version(2, Integer.MAX_VALUE, Integer.MAX_VALUE), vr.getEnd());

		vr = new VersionRange("2.1.*");
		assertEquals(new Version("2.1.0"), vr.getStart());
		assertEquals(new Version(2, 1, Integer.MAX_VALUE), vr.getEnd());

		vr = new VersionRange("2-3");
		assertEquals(new Version("2.0.0"), vr.getStart());
		assertEquals(new Version("3.0.0"), vr.getEnd());

		vr = new VersionRange("-3");
		assertEquals(new Version("0.0.0"), vr.getStart());
		assertEquals(new Version("3.0.0"), vr.getEnd());

		vr = new VersionRange("2-");
		assertEquals(new Version("2.0.0"), vr.getStart());
		assertNull(vr.getEnd());

		try {
			new VersionRange("2*");
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
		}

		try {
			new VersionRange("*2");
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
		}

		try {
			new VersionRange("*.2");
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
		}

		try {
			new VersionRange("2.*-3.0");
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
		}

		try {
			new VersionRange("2.0.0.*");
			fail("Should have failed");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testEquals() throws Exception {
		VersionRange vr1 = new VersionRange("2.0-3.0");
		VersionRange vr2 = new VersionRange(new Version("2"), new Version("3"));
		assertEquals(vr1, vr2);
	}

	@Test
	public void testIsInRange() throws Exception {
		Version v = new Version("1.0");
		Version v2 = new Version("2.0");
		Version v201 = new Version("2.0.1");
		Version v29 = new Version("2.999999");
		Version v3 = new Version("3.0");
		Version v31 = new Version("3.1");
		Version v4 = new Version("4.0");
		VersionRange vr = new VersionRange("2.*");
		assertFalse(vr.isInRange(v));
		assertTrue(vr.isInRange(v2));
		assertTrue(vr.isInRange(v29));
		assertFalse(vr.isInRange(v3));

		VersionRange vr2 = new VersionRange("2.0-3.0");
		assertFalse(vr2.isInRange(v));
		assertTrue(vr2.isInRange(v2));
		assertTrue(vr2.isInRange(v29));
		assertTrue(vr2.isInRange(v3));
		assertFalse(vr2.isInRange(v31));
		assertFalse(vr2.isInRange(v4));

		vr2 = new VersionRange("2.0.*");
		assertFalse(vr2.isInRange(v));
		assertTrue(vr2.isInRange(v2));
		assertTrue(vr2.isInRange(v201));
		assertFalse(vr2.isInRange(v29));
		assertFalse(vr2.isInRange(v3));
		assertFalse(vr2.isInRange(v31));
		assertFalse(vr2.isInRange(v4));
	}

	@Test
	public void testHighestVersion() throws Exception {
		Version[] va = new Version[]{new Version("10.0"), new Version("2.8"), new Version("1.0"),
				new Version("2.9"), new Version("2.0"), new Version("2.2"), new Version("3.0")};

		VersionRange vr = new VersionRange("2.0-3.0");
		Version result = vr.highestVersion(va);
		assertEquals(new Version("3.0"), result);
	}
}