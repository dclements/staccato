package com.readytalk.staccato.database.migration.validation.javax;

import static org.mockito.Mockito.mock;

import org.testng.Assert;
import org.testng.annotations.Test;

public class VersionValidatorTest {

	@Test
	public void testValidWithNotNull() {

		// test that a properly formatted string returns valid
		{
			String testVer = "1.0";
			VersionValidator formatValidator = new VersionValidator();
			formatValidator.initialize(makeConstraint());
			Assert.assertTrue(formatValidator.isValid(testVer, null));
		}
	}

	@Test
	public void testValidWithEmptyStringAndNull() {

		// test invalid with null
		{
			String testVer = null;
			VersionValidator formatValidator = new VersionValidator();
			formatValidator.initialize(makeConstraint());
			Assert.assertTrue(formatValidator.isValid(testVer, null));
		}

		// test invalid with null
		{
			String testVer = "";
			VersionValidator formatValidator = new VersionValidator();
			formatValidator.initialize(makeConstraint());
			Assert.assertTrue(formatValidator.isValid(testVer, null));
		}
	}

	@Test
	public void testInvalidWithMalformedFormat() {
		// test invalid
		{
			String testVer = "foo";
			VersionValidator formatValidator = new VersionValidator();
			formatValidator.initialize(makeConstraint());
			Assert.assertTrue(formatValidator.isValid(testVer, null));
		}
	}

	/**
	 * Convenience method for created an DateFormat constraint
	 *
	 * @return and DateFormat constraint
	 */
	private Version makeConstraint() {
		return mock(Version.class);
	}
}
