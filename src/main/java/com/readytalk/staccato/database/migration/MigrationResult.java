package com.readytalk.staccato.database.migration;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores the results of a migration where the key is
 * the workflow step annotation and the value is the return value of the method invoked.
 *
 * IMPORTANT NOTE:  Currently, method return values are not used by the system.  This design
 * is implemented for future use only and, therefore, script methods are not required to
 * return non-void values.
 */
public class MigrationResult {

	private final Map<Class<? extends Annotation>, Object> resultMap = new HashMap<Class<? extends Annotation>, Object>();

	public Map<Class<? extends Annotation>, Object> getResultMap() {
		return resultMap;
	}
}
