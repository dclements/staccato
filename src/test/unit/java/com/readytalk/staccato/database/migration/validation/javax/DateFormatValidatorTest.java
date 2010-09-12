package com.readytalk.staccato.database.migration.validation.javax;

import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author jhumphrey
 */
public class DateFormatValidatorTest {

    @Test
    public void testValidWithNotNull() {

        // test that a properly formatted string returns valid
        {
            String testDate = "2010-05-04T10:57:52-06:00";
            DateFormatValidator formatValidator = new DateFormatValidator();
            formatValidator.initialize(makeConstraint());
            Assert.assertTrue(formatValidator.isValid(testDate, null));
        }
    }

    @Test
    public void testValidWithNullAndEmptyString() {

        // test invalid with null
        {
            String testDate = null;
            DateFormatValidator formatValidator = new DateFormatValidator();
            formatValidator.initialize(makeConstraint());
            Assert.assertTrue(formatValidator.isValid(testDate, null));
        }

        // test valid with empty string
        {
            String testDate = "";
            DateFormatValidator formatValidator = new DateFormatValidator();
            formatValidator.initialize(makeConstraint());
            Assert.assertTrue(formatValidator.isValid(testDate, null));
        }
    }

    @Test
    public void testInvalidWithMalformedFormat() {
        // test that a improperly formatted string returns invalid
        String testDate = "2010-05-04T10:57:52";
        DateFormatValidator formatValidator = new DateFormatValidator();
        formatValidator.initialize(makeConstraint());
        Assert.assertFalse(formatValidator.isValid(testDate, null));
    }

    /**
     * Convenience method for created an DateFormat constraint
     *
     * @return and DateFormat constraint
     */
    private DateFormat makeConstraint() {
        DateFormat dateFormat = EasyMock.createStrictMock(DateFormat.class);
        EasyMock.expect(dateFormat.dateFormat()).andReturn(DateFormat.ISO_DATE_FORMAT);
        EasyMock.replay(dateFormat);
        return dateFormat;
    }
}
