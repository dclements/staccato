package com.readytalk.staccato.database.migration.script.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.script.ScriptService;
import com.readytalk.staccato.utils.Resource;
import com.readytalk.staccato.utils.ResourceLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author jhumphrey
 */
@Singleton
public class SQLScriptService implements ScriptService<SQLScript> {

  private ResourceLoader loader;

  @Inject
  public SQLScriptService(ResourceLoader loader) {
    this.loader = loader;
  }

  @Override
  public List<SQLScript> load(String migrationDir) {
    Set<Resource> resources = loader.loadRecursively(migrationDir, getScriptFileExtension());

    List<SQLScript> scripts = new ArrayList<SQLScript>();

    for (Resource resource : resources) {
      SQLScript script = new SQLScript();
      script.setFilename(resource.getFilename());
      script.setUrl(resource.getUrl());

      if (scripts.contains(script)) {

        if (scripts.contains(script)) {
          SQLScript collisionScript = null;
          for (SQLScript sqlScript : scripts) {
            if (sqlScript.equals(collisionScript)) {
              collisionScript = sqlScript;
            }
          }
          throw new MigrationException("Unique script violation.  SQL script [" + script.getUrl() + "] violates" +
            " unique filename constraint.  Script [" + collisionScript.getUrl().toExternalForm() + "] already contains the same filename");
        }

        throw new MigrationException("Unique script violation.  SQL script [" + script.getUrl() + "] filename violates" +
          " unique filename constraint.  Another sql file already contains the same name");
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
