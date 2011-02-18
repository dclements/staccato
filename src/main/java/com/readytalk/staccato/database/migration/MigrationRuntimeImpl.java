package com.readytalk.staccato.database.migration;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.migration.script.sql.SQLScript;
import com.readytalk.staccato.utils.SQLUtils;

/**
 * @author jhumphrey
 */
public class MigrationRuntimeImpl implements MigrationRuntime {

  public static final Logger logger = Logger.getLogger(MigrationRuntimeImpl.class);

  private DatabaseContext databaseContext;
  private List<SQLScript> sqlScripts;
  private MigrationType migrationType;
  private boolean loggingEnabled;

  public MigrationRuntimeImpl(DatabaseContext databaseContext, List<SQLScript> sqlScripts,
    MigrationType migrationType, boolean loggingEnabled) {

    this.databaseContext = databaseContext;
    this.sqlScripts = sqlScripts;
    this.migrationType = migrationType;
    this.loggingEnabled = loggingEnabled;
  }

  @Override
  public DatabaseContext getDatabaseContext() {
    return databaseContext;
  }

  @Override
  public MigrationType getMigrationType() {
    return migrationType;
  }

  @Override
  public boolean isLoggingEnabled() {
    return loggingEnabled;
  }

  @Override
  public List<SQLScript> sqlScripts() {
    return sqlScripts;
  }

  @Override
  public ResultSet executeSQL(String sql) throws SQLException {
    return SQLUtils.execute(databaseContext.getConnection(), sql);
  }

  @Override
  public ResultSet executeSQLFile(String filename) throws SQLException {

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
    rs = SQLUtils.executeSQLFile(databaseContext.getConnection(), scriptUrl);

    return rs;
  }
}
