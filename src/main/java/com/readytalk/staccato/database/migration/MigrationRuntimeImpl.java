package com.readytalk.staccato.database.migration;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.migration.script.sql.SQLScript;
import com.readytalk.staccato.utils.SQLUtils;
import com.readytalk.staccato.utils.StringUtils;

/**
 * @author jhumphrey
 */
public class MigrationRuntimeImpl implements MigrationRuntime {

  Logger logger = Logger.getLogger(this.getClass().getName());

  private DatabaseContext databaseContext;
  private ProjectContext projectContext;
  private List<SQLScript> sqlScripts;
  private MigrationType migrationType;

  public MigrationRuntimeImpl(DatabaseContext databaseContext, ProjectContext projectContext, List<SQLScript> sqlScripts, MigrationType migrationType) {
    this.databaseContext = databaseContext;
    this.projectContext = projectContext;
    this.sqlScripts = sqlScripts;
    this.migrationType = migrationType;
  }

  @Override
  public DatabaseContext getDatabaseContext() {
    return databaseContext;
  }

  @Override
  public ProjectContext getProjectContext() {
    return projectContext;
  }

  @Override
  public MigrationType getMigrationType() {
    return migrationType;
  }

  @Override
  public List<SQLScript> sqlScripts() {
    return sqlScripts;
  }

  @Override
  public ResultSet executeSQL(String sql) {

    try {
      return SQLUtils.execute(databaseContext.getConnection(), sql);
    } catch (SQLException e) {
      logger.log(Level.SEVERE, "Error occurred while executing sql:" + StringUtils.truncate(100, sql), e);
      return null;
    }
  }

  @Override
  public ResultSet executeSQLFile(String filename) {

    ResultSet rs;

    SQLScript scriptToExecute = null;

    for (SQLScript sqlScript : sqlScripts) {
      if (sqlScript.getFilename().equals(filename)) {
        scriptToExecute = sqlScript;
        break;
      }
    }

    if (scriptToExecute == null) {
      throw new MigrationException("Unable to locate sql script '" + filename + "' in classpath");
    }

    URL scriptUrl = scriptToExecute.getUrl();
    try {
      rs = SQLUtils.executeSQLFile(databaseContext.getConnection(), scriptUrl);
    } catch (SQLException e) {
      throw new MigrationException("Unable to sql script file: " + filename, e);
    }

    return rs;
  }
}
