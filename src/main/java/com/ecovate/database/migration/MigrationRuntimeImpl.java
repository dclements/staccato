package com.ecovate.database.migration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

import com.ecovate.database.DatabaseContext;
import com.ecovate.database.migration.script.sql.SQLScript;

/**
 * @author jhumphrey
 */
public class MigrationRuntimeImpl implements MigrationRuntime {

  Logger logger = Logger.getLogger(this.getClass().getName());

  private DatabaseContext databaseContext;
  private ProjectContext projectContext;
  private List<SQLScript> sqlScripts;

  public MigrationRuntimeImpl(DatabaseContext databaseContext, ProjectContext projectContext, List<SQLScript> sqlScripts) {
    this.databaseContext = databaseContext;
    this.projectContext = projectContext;
    this.sqlScripts = sqlScripts;
  }

  public DatabaseContext getDatabaseContext() {
    return databaseContext;
  }

  public ProjectContext getProjectContext() {
    return projectContext;
  }

  @Override
  public ResultSet executeSQL(String sql) {

    ResultSet rs = null;
    try {
      Connection conn = databaseContext.getConnection();
      Statement st = conn.createStatement();

      boolean isResultSet = st.execute(sql);
      if (isResultSet) {
        rs = st.getResultSet();
      }

    } catch (Exception e) {

      String truncatedSql = sql;

      if (sql.length() > 100) {
        truncatedSql = sql.substring(0, 100);
      }

      logger.severe("Unable to execute query: [" + truncatedSql + "...]");
      throw new MigrationException("Error occurred while executing sql: " + truncatedSql, e);
    }

    return rs;
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

    if (scriptToExecute != null) {

      try {
        URL scriptUrl = scriptToExecute.getUrl();
        BufferedReader in = new BufferedReader(new InputStreamReader(scriptUrl.openStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
          stringBuilder.append(inputLine).append("\n");
        }
        in.close();

        rs = executeSQL(stringBuilder.toString());

      } catch (IOException e) {
        e.printStackTrace();
        throw new MigrationException("Unable to read script: " + filename, e);
      }

    } else {
      throw new MigrationException("Unable to locate sql script '" + filename + "' in classpath");
    }

    return rs;
  }
}
