package com.readytalk.staccato.database.migration.script.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.script.ScriptService;
import com.readytalk.staccato.utils.Resource;
import com.readytalk.staccato.utils.ResourceLoader;

@Singleton
public class SQLScriptService implements ScriptService<SQLScript> {

	private static final Logger logger = Logger.getLogger(SQLScriptService.class);

	private final ResourceLoader loader;

	@Inject
	public SQLScriptService(ResourceLoader loader) {
		this.loader = loader;
	}

	@Override
	public List<SQLScript> load(String migrationDir, ClassLoader classLoader) {

		logger.debug("Loading sql scripts from migration directory: " + migrationDir);

		Set<Resource> resources = loader.loadRecursively(migrationDir, getScriptFileExtension(), classLoader);

		List<SQLScript> scripts = new ArrayList<SQLScript>();

		for (Resource resource : resources) {
			SQLScript script = new SQLScript();
			script.setFilename(resource.getFilename());
			script.setUrl(resource.getUrl());

			if (scripts.contains(script)) {
				SQLScript collisionScript = null;
				for (SQLScript sqlScript : scripts) {
					if (sqlScript.equals(collisionScript)) {
						collisionScript = sqlScript;
					}
				}
				throw new MigrationException("Unique script violation.  SQL script [" + String.valueOf(script.getUrl()) + "] filename violates" +
				" unique filename constraint.  Another sql file already contains the same filename.");
			}
			scripts.add(script);
		}

		return scripts;
	}

	@Override
	public String getScriptFileExtension() {
		return "sql";
	}
}
