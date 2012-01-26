package com.readytalk.staccato.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class VersionTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	/*
	 * TODO: It is possible to make this more flexible and more comprehensive in testing here.
	 * Currently a bit of a hack to iterate over a few of the basic possibilities.
	 */
	@DataPoints
	public static String [] versions() {
		final List<String> retval = new LinkedList<String>();
		
		for(int i=0; i<=9; i++) {
			retval.add(String.format("%d", i));
			for(int j=0; j<=9; j++) {
				retval.add(String.format("%d.%d", i, j));
				for(int k=0; k<=9; k++) {
					retval.add(String.format("%d.%d.%d", i, j, k));
				}
			}
		}
		
		retval.add("1.1.1-alpha1");
		retval.add("2.2.2-beta2");
		retval.add("3.3.3-rc3");
		retval.add("4.4.4-milestone4");
		retval.add("5.5.5-snapshot5");
		
		return retval.toArray(new String [] {});
	}
	
	private final Version incVersion = new Version(1,2,4);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	

	@Test
	public void testCreateNegative() {
		thrown.expect(IllegalArgumentException.class);
		new Version(-1, 0, 0);
	}

	@Theory
	public void testGetMajorString(String ver) {
		Version v = new Version(ver, true);
		
		assertEquals(Integer.valueOf(ver.substring(0, 1)).intValue(), v.getMajor());
	}

	@Theory
	public void testGetMinorString(String ver) {
		Assume.assumeTrue(ver.length() >= 3);
		
		Version v = new Version(ver, true);
		
		assertEquals(Integer.valueOf(ver.substring(2, 3)).intValue(), v.getMinor());
	}
	
	@Test
	public void testGetMinorInteger() {
		final Version v = new Version(1, 2, 3);
		
		assertEquals(3, v.getPatch());
	}
	
	@Theory
	public void testToString(String ver) {
		Assume.assumeTrue(ver.length() >= 5);
		final Version v = new Version(ver, true);
		
		assertEquals(ver, v.toString());
	}
	
	
	@Test
	public void testCreateBadParse() {
		thrown.expect(IllegalArgumentException.class);
		new Version("1.0.0quork", true);
	}
	
	@Test
	public void testCreateBadParseDelimeters() {
		thrown.expect(IllegalArgumentException.class);
		new Version("1.0.-b1", false);
	}
	
	@Test
	public void testCreateBadParseEnding() {
		thrown.expect(IllegalArgumentException.class);
		new Version("1.0.", false);
	}

	@Theory
	public void testGetPatchString(String ver) {
		Assume.assumeTrue(ver.length() >= 5);
		
		Version v = new Version(ver, true);
		
		assertEquals(Integer.valueOf(ver.substring(4, 5)).intValue(), v.getPatch());
	}


	@Theory
	public void testGetAdditional(String ver) {
		Assume.assumeTrue(ver.length() >= 7);
		Version v = new Version(ver, true);
		
		assertEquals(ver.substring(6), v.getAdditional());
	}

	@Test
	public void testIsMajorVersionTrue() {
		Version v = new Version("1.0.0", true);
		
		assertTrue(v.isMajorVersion());
	}
	
	@Test
	public void testIsMajorVersionFalse() {
		Version v = new Version("1.0.1", true);
		
		assertFalse(v.isMajorVersion());
	}

	@Test
	public void testIsMinorVersionTrue() {
		Version v = new Version("1.1.0", true);
		
		assertTrue(v.isMinorVersion());
	}
	
	@Test
	public void testIsMinorVersionFalse() {
		Version v = new Version("1.1.1", true);
		
		assertFalse(v.isMinorVersion());
	}

	@Test
	public void testIsPatchVersionTrue() {
		Version v = new Version("1.1.1", true);
		
		assertTrue(v.isPatchVersion());
	}
	
	@Test
	public void testIsPatchVersionFalse() {
		Version v = new Version("1.1.0", true);
		
		assertFalse(v.isPatchVersion());
	}

	@Test
	public void testIsSnapshotFalse() {
		Version v = new Version("1.1.0", true);
		
		assertFalse(v.isSnapshot());
	}
	
	@Test
	public void testIsSnapshotTrue() {
		Version v = new Version("1.1.0b1", false);
		
		assertTrue(v.isSnapshot());
	}

	@Test
	public void testEqualsIdentity() {
		final Version v1 = new Version("1.1.0", true);
		
		assertTrue(v1.equals(v1));
	}
	
	@Test
	public void testEqualsNull() {
		final Version v1 = new Version("1.1.0", true);
		
		assertFalse(v1.equals(null));
	}
	
	@Test
	public void testEqualsObject() {
		final Version v1 = new Version("1.1.0", true);
		
		assertFalse(v1.equals(new Object()));
	}
	
	@Test
	public void testEqualsTrue() {
		final Version v1 = new Version("1.1.0", true);
		final Version v2 = new Version("1.1.0", true);
		
		assertTrue(v1.equals(v2));
	}
	
	@Test
	public void testEqualsFalse() {
		final Version v1 = new Version("1.1.0", true);
		final Version v2 = new Version("1.2.0", true);
		
		assertFalse(v1.equals(v2));
	}
	
	@Test
	public void testHashCodeEquals() {
		final Version v1 = new Version("1.1.0", true);
		final Version v2 = new Version("1.1.0", true);
		
		assertEquals(v1.hashCode(), v2.hashCode());
	}
	
	@Test
	public void testHashCodeDifferent() {
		final Version v1 = new Version("1.1.0", true);
		final Version v2 = new Version("1.2.0", true);
		
		assertNotSame(v1.hashCode(), v2.hashCode());
	}

	@Test
	public void testCompareToEquals() {
		final Version v1 = new Version("1.1.0-alpha1", true);
		final Version v2 = new Version("1.1.0-alpha1", true);
		assertEquals(0, v1.compareTo(v2));
	}
	
	@Test
	public void testCompareToGt() {
		final Version v1 = new Version("1.2.0", true);
		final Version v2 = new Version("1.1.0", true);
		assertTrue(v1.compareTo(v2) > 0);
	}
	
	@Test
	public void testCompareToLt() {
		final Version v1 = new Version("1.1.0", true);
		final Version v2 = new Version("1.2.0", true);
		assertTrue(v1.compareTo(v2) < 0);
	}
	
	@Test
	public void testMajor() {
		assertEquals(1, incVersion.toMajor().getMajor());
	}
	
	@Test
	public void testMinor() {
		Version t = incVersion.toMinor();
		assertEquals(3, t.getMajor()+t.getMinor());
	}
	
	@Test
	public void testPatch() {
		Version t = incVersion.toPatch();
		assertNull(t.getAdditional());
	}
}
