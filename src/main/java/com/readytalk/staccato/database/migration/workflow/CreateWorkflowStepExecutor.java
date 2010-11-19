package com.readytalk.staccato.database.migration.workflow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.readytalk.staccato.database.DatabaseContext;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.annotation.Create;
import com.readytalk.staccato.database.migration.annotation.MigrationAnnotationParser;

/**
 * @author jhumphrey
 */
public class CreateWorkflowStepExecutor implements WorkflowStepExecutor<Create> {

  public static final Logger logger = Logger.getLogger(WorkflowStepExecutorImpl.class);

  Create workflowStep;

  @Override
  public void initialize(Create workflowStep) {
    this.workflowStep = workflowStep;
  }

  @Override
  public Object execute(Object scriptInstance, WorkflowContext context) throws InvocationTargetException, IllegalAccessException {
    DatabaseContext dbCtx = context.getMigrationRuntime().getDatabaseContext();

    // grab the java.sql.Connection
    Connection connection = dbCtx.getConnection();

    // close the existing connection (if connected)
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      throw new MigrationException("An exception occurred while closing the connection to database: " + dbCtx.getFullyQualifiedJdbcUri());
    }

    String baseJdbc = dbCtx.getBaseJdbcUri().toString();
    String rootUser = dbCtx.getSuperUser();
    String rootPwd = dbCtx.getSuperUserPwd();
    String rootDb = dbCtx.getRootDbName();

    String fullyQualifiedRootJdbcUriStr = baseJdbc;
    if (baseJdbc.endsWith("/")) {
      fullyQualifiedRootJdbcUriStr += rootDb;
    } else {
      fullyQualifiedRootJdbcUriStr += "/" + rootDb;
    }

    // connect to root db as root user
    try {
      Class.forName(context.getMigrationRuntime().getDatabaseContext().getDatabaseType().getDriver());
      connection = DriverManager.getConnection(fullyQualifiedRootJdbcUriStr, rootUser, rootPwd);
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    dbCtx.setConnection(connection);

    MigrationAnnotationParser annotationParser = context.getAnnotationParser();
    Method method = annotationParser.getAnnotatedMethod(scriptInstance, workflowStep.annotationType());

    Object result;

    try {
      logger.debug("Invoking workflow step @" + workflowStep.annotationType().getSimpleName() + " on method: " + method.getName());
      // first try invoking without runtime argument
      result = method.invoke(scriptInstance);
    } catch (IllegalArgumentException e) {
      // if exception is thrown then send the runtime to the argument list
      result = method.invoke(scriptInstance, context.getMigrationRuntime());
    }

    // reconnect to the original db
    // connect to root db as root user
    try {
      connection = DriverManager.getConnection(dbCtx.getFullyQualifiedJdbcUri().toString(), dbCtx.getUsername(), dbCtx.getPassword());
    } catch (SQLException e) {
      e.printStackTrace();
    }

    dbCtx.setConnection(connection);

    return result;
  }
}
