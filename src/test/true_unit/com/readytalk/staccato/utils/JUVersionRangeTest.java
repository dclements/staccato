package com.readytalk.staccato.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * There are a ton of parsing edge cases here, so trying to cover them all
 * so that when this gets refactored, none of them get missed.
 *
 */
public class JUVersionRangeTest {
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	private final Version start = new Version(1,2,3);
	private final Version end = new Version(3,2,1);
	
	private final VersionRange range = new VersionRange(start, end);
	
	private final Version [] versions = new Version [] {start, new Version(2,0,0), end};
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testVersionRangeNullStart() {
		thrown.expect(IllegalArgumentException.class);
		new VersionRange(null, end);
	}
	
	@Test
	public void testVersionRangeReversed() {
		thrown.expect(IllegalArgumentException.class);
		new VersionRange(end, start);
	}

	@Test
	public void testGetStart() {
		assertEquals(start, range.getStart());
	}

	@Test
	public void testGetEnd() {
		assertEquals(end, range.getEnd());
	}

	@Test
	public void testIsInRangeTrue() {
		final Version v = new Version(2,1,1);
		assertTrue(range.isInRange(v));
	}

	@Test
	public void testIsInRangeBefore() {
		final Version v = new Version(0,0,5);
		assertFalse(range.isInRange(v));
	}
	
	@Test
	public void testIsInRangeAfter() {
		final Version v = new Version(10,0,5);
		assertFalse(range.isInRange(v));
	}
	
	@Test
	public void testIsInRangeIndefinite() {
		final VersionRange vr = new VersionRange(start, null);
		final Version v = new Version(30,0,0);
		assertTrue(vr.isInRange(v));
	}
	
	@Test
	public void testIsInRangeStart() {
		assertTrue(range.isInRange(start));
	}
	
	@Test
	public void testIsInRangeEnd() {
		assertTrue(range.isInRange(end));
	}

	@Test
	public void testHighestVersion() {
		assertEquals(end, range.highestVersion(versions));
	}

	@Test
	public void testToString() {
		assertEquals("1.2.3-3.2.1", range.toString());
	}
	
	@Test
	public void testEqualsNull() {
		assertFalse(range.equals(null));
	}
	
	@Test
	public void testEqualsObject() {
		assertFalse(range.equals(new Object()));
	}
	
	@Test
	public void testEqualsSelf() {
		assertTrue(range.equals(range));
	}
	
	@Test
	public void testEqualsTrue() {
		final VersionRange vr = new VersionRange(start, end);
		assertTrue(range.equals(vr));
	}
	
	@Test
	public void testEqualsFalse() {
		final VersionRange vr = new VersionRange(start, null);
		assertFalse(range.equals(vr));
	}
	
	@Test
	public void testHashCodeEquals() {
		final VersionRange vr = new VersionRange(start, end);
		
		assertEquals(range.hashCode(), vr.hashCode());
	}
	
	@Test
	public void testHashCodeNotEquals() {
		final VersionRange vr = new VersionRange(start, null);
		
		assertNotSame(range.hashCode(), vr.hashCode());
	}
	
	@Test
	public void testParseVersionRangeDash() {
		final VersionRange vr = new VersionRange(String.format("%s-%s", start, end));
		
		assertEquals(range, vr);
	}
	
	@Test
	public void testMinorStar() {
		final VersionRange vr = new VersionRange("1.*");
		final String s = String.format("1.0.0-1.%d.%d", Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		assertEquals(new VersionRange(s), vr);
	}
	
	@Test
	public void testMajorStar() {
		final VersionRange vr = new VersionRange("*");
		
		assertEquals(new VersionRange(Version.ZERO, null), vr);
	}
	
	@Test
	public void testPatchStar() {
		final VersionRange vr = new VersionRange("1.0.*");
		final String s = String.format("1.0.0-1.0.%d", Integer.MAX_VALUE);
		
		assertEquals(new VersionRange(s), vr);
	}
	
	@Test
	public void testPatchMissing() {
		final VersionRange vr = new VersionRange("0.0");
		
		assertEquals(new VersionRange(Version.ZERO, Version.ZERO), vr);
	}
	
	@Test
	public void testStartingDash() {
		final VersionRange vr = new VersionRange("-1.0.0");
		
		assertEquals(new VersionRange(Version.ZERO, new Version(1,0,0)), vr);
	}
	
	@Test
	public void testEnding() {
		final VersionRange vr = new VersionRange("0.0.0-");
		
		assertEquals(new VersionRange(Version.ZERO, null), vr);
	}
	
	@Test
	public void testInvalidStart() {
		thrown.expect(IllegalArgumentException.class);
		new VersionRange("0.a.0-1.0.0");
	}
	
	@Test
	public void testInvalidEnd() {
		thrown.expect(IllegalArgumentException.class);
		new VersionRange("0.0.0-1.a.0");
	}

	
	@Test
	public void testDoubleDash() {
		thrown.expect(IllegalArgumentException.class);
		new VersionRange("0.0.0--");
	}
	
	@Test
	public void testDoubleStar() {
		thrown.expect(IllegalArgumentException.class);
		new VersionRange("0.*.*");
	}
}
