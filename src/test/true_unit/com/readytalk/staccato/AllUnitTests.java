package com.readytalk.staccato;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({CommandLineServiceTest.class,
	com.readytalk.staccato.database.AllUnitTests.class,
	com.readytalk.staccato.database.migration.AllUnitTests.class,
	com.readytalk.staccato.database.migration.annotation.AllUnitTests.class,
	com.readytalk.staccato.database.migration.script.groovy.AllUnitTests.class,
	com.readytalk.staccato.database.migration.script.sql.AllUnitTests.class,
	com.readytalk.staccato.database.migration.validation.javax.AllUnitTests.class,
	com.readytalk.staccato.utils.AllUnitTests.class})
public class AllUnitTests {
	
}
