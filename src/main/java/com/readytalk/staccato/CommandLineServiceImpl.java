package com.readytalk.staccato;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.inject.Inject;
import com.readytalk.staccato.database.migration.MigrationException;

public class CommandLineServiceImpl implements CommandLineService {

	private final CommandLineParser parser;

	private final HelpFormatter helpFormatter;

	private final Options options = new Options();

	@Inject
	public CommandLineServiceImpl(final CommandLineParser _parser, final HelpFormatter _helpFormatter) {
		this.parser = _parser;
		this.helpFormatter = _helpFormatter;
		init();
	}

	private void init() {

		StaccatoOptions.Arg[] staccatoArgs = StaccatoOptions.Arg.values();

		for (StaccatoOptions.Arg staccatoArg : staccatoArgs) {
			
			@SuppressWarnings("static-access")
			Option opt = OptionBuilder.withArgName(staccatoArg.getOpt())
							.withLongOpt(staccatoArg.getLongOpt())
							.hasArg(staccatoArg.isArgOption())
							.withDescription(staccatoArg.getDesc())
							.isRequired(staccatoArg.isRequired()).create(staccatoArg.getOpt());
			
			new Option(staccatoArg.getOpt(), staccatoArg.getLongOpt(), true, staccatoArg.getDesc());
			opt.setRequired(staccatoArg.isRequired());
			options.addOption(opt);
		}

	}

	@Override
	public StaccatoOptions parse(final String... args) {
		if (args.length == 0 || args[0].contains("help") || "-h".equals(args[0])) {
			helpFormatter.printHelp("java -jar staccato.jar [options]", options);
			return null;
		} else {
			try {
				CommandLine cli = parser.parse(options, args);

				// extract options values from the command line
				String jdbcUrl = cli.getOptionValue(StaccatoOptions.Arg.JDBC_URL.getOpt());
				String dbName = cli.getOptionValue(StaccatoOptions.Arg.DB_NAME.getOpt());
				String dbUser = cli.getOptionValue(StaccatoOptions.Arg.DB_USER.getOpt());
				String dbPwd = cli.getOptionValue(StaccatoOptions.Arg.DB_PWD.getOpt());
				String migrationType = cli.getOptionValue(StaccatoOptions.Arg.MIGRATION_TYPE.getOpt());
				String migrateFromDate = cli.getOptionValue(StaccatoOptions.Arg.MIGRATE_FROM_DATE.getOpt());
				String migrateToDate = cli.getOptionValue(StaccatoOptions.Arg.MIGRATE_TO_DATE.getOpt());
				String migrateScript = cli.getOptionValue(StaccatoOptions.Arg.MIGRATE_SCRIPT.getOpt());
				String migrationsDir = cli.getOptionValue(StaccatoOptions.Arg.MIGRATIONS_DIR.getOpt());
				String dbSuperUser = cli.getOptionValue(StaccatoOptions.Arg.DB_SUPERUSER.getOpt());
				String dbSuperUserPwd = cli.getOptionValue(StaccatoOptions.Arg.DB_SUPERUSER_PWD.getOpt());
				String migrateFromVer = cli.getOptionValue(StaccatoOptions.Arg.MIGRATE_FROM_VER.getOpt());
				String migrateToVer = cli.getOptionValue(StaccatoOptions.Arg.MIGRATE_TO_VER.getOpt());
				String rootDb = cli.getOptionValue(StaccatoOptions.Arg.ROOT_DB.getOpt());
				String migrationJarPath = cli.getOptionValue(StaccatoOptions.Arg.MIGRATION_JAR_PATH.getOpt());

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
				staccatoOptions.enableLogging =  cli.hasOption(StaccatoOptions.Arg.LOGGING.getOpt());

				return staccatoOptions;
			} catch (ParseException e) {
				throw new MigrationException(e);
			}
		}
	}
}
