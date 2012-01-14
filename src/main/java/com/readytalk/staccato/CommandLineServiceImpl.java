package com.readytalk.staccato;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.inject.Inject;
import com.readytalk.staccato.database.migration.MigrationException;
import com.readytalk.staccato.database.migration.validation.MigrationValidationException;

public class CommandLineServiceImpl implements CommandLineService {

	final private CommandLineParser parser;

	final private HelpFormatter helpFormatter;

	final Options options = new Options();

	@Inject
	public CommandLineServiceImpl(CommandLineParser parser, HelpFormatter helpFormatter) {
		this.parser = parser;
		this.helpFormatter = helpFormatter;
		init();
	}

	private void init() {

		StaccatoOptions.Arg[] staccatoArgs = StaccatoOptions.Arg.values();

		for (StaccatoOptions.Arg staccatoArg : staccatoArgs) {
			Option opt = new Option(staccatoArg.opt, staccatoArg.longOpt, true, staccatoArg.desc);
			opt.setRequired(staccatoArg.required);
			options.addOption(opt);
		}

	}

	@Override
	public StaccatoOptions parse(String... args) throws MigrationValidationException {

		if (args.length == 0 || args[0].equals("help")) {
			helpFormatter.printHelp("java -jar staccato.jar [options]", options);
			return null;
		} else {
			try {
				CommandLine cli = parser.parse(options, args);

				// extract options values from the command line
				String jdbcUrl = cli.getOptionValue(StaccatoOptions.Arg.JDBC_URL.opt);
				String dbName = cli.getOptionValue(StaccatoOptions.Arg.DB_NAME.opt);
				String dbUser = cli.getOptionValue(StaccatoOptions.Arg.DB_USER.opt);
				String dbPwd = cli.getOptionValue(StaccatoOptions.Arg.DB_PWD.opt);
				String migrationType = cli.getOptionValue(StaccatoOptions.Arg.MIGRATION_TYPE.opt);
				String migrateFromDate = cli.getOptionValue(StaccatoOptions.Arg.MIGRATE_FROM_DATE.opt);
				String migrateToDate = cli.getOptionValue(StaccatoOptions.Arg.MIGRATE_TO_DATE.opt);
				String migrateScript = cli.getOptionValue(StaccatoOptions.Arg.MIGRATE_SCRIPT.opt);
				String migrationsDir = cli.getOptionValue(StaccatoOptions.Arg.MIGRATIONS_DIR.opt);
				String dbSuperUser = cli.getOptionValue(StaccatoOptions.Arg.DB_SUPERUSER.opt);
				String dbSuperUserPwd = cli.getOptionValue(StaccatoOptions.Arg.DB_SUPERUSER_PWD.opt);
				String migrateFromVer = cli.getOptionValue(StaccatoOptions.Arg.MIGRATE_FROM_VER.opt);
				String migrateToVer = cli.getOptionValue(StaccatoOptions.Arg.MIGRATE_TO_VER.opt);
				String rootDb = cli.getOptionValue(StaccatoOptions.Arg.ROOT_DB.opt);
				String migrationJarPath = cli.getOptionValue(StaccatoOptions.Arg.MIGRATION_JAR_PATH.opt);
				String logging = cli.getOptionValue(StaccatoOptions.Arg.LOGGING.opt);

				StaccatoOptions staccatoOptions = new StaccatoOptions();
				staccatoOptions.jdbcUrl = jdbcUrl;
				staccatoOptions.dbName = dbName;
				staccatoOptions.dbUser = dbUser;
				staccatoOptions.dbPwd = dbPwd;
				staccatoOptions.migrationType = migrationType;
				staccatoOptions.migrateFromDate = migrateFromDate;
				staccatoOptions.migrateToDate = migrateToDate;
				staccatoOptions.migrateScript = migrateScript;
				staccatoOptions.migrationsDir = migrationsDir;
				staccatoOptions.dbSuperUser = dbSuperUser;
				staccatoOptions.dbSuperUserPwd = dbSuperUserPwd;
				staccatoOptions.migrateFromVer = migrateFromVer;
				staccatoOptions.migrateToVer = migrateToVer;
				staccatoOptions.rootDb = rootDb;
				staccatoOptions.migrationJarPath = migrationJarPath;

				try {
					Boolean loggingEnabled = new Boolean(logging);
					staccatoOptions.enableLogging = loggingEnabled;
				} catch (Exception e) {
					staccatoOptions.enableLogging = true;
				}

				return staccatoOptions;
			} catch (ParseException e) {
				throw new MigrationException(e);
			}
		}
	}
}
