package com.readytalk.staccato.database.migration;

import java.util.List;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.migration.script.sql.SQLScript;

public interface MigrationRuntimeFactory {
	MigrationRuntime create(DatabaseContext databaseContext, List<SQLScript> sqlScripts,
			MigrationType migrationType, boolean loggingEnabled);
}
