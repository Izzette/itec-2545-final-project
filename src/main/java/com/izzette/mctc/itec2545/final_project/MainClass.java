package com.izzette.mctc.itec2545.final_project;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.sql.SQLException;

import com.izzette.mctc.itec2545.final_project.CACreatorForm;
import com.izzette.mctc.itec2545.final_project.CARuleData;

/** The main class (launches CACreatorForm). */
public class MainClass {
	private static final String name = "interactive-1d-ca";

	private static String dbPath = null;

	/** THe main method.
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		parseArgs(args);

		try {
			CARuleData.instancize(dbPath);
		} catch (SQLException e) {
			System.err.printf("Failed to connect to database: %s\n", e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		CACreatorForm CACreatorForm = new CACreatorForm();
	}

	private static void parseArgs(String[] args) {
		String shortOpts = "hf:";
		LongOpt[] longOpts = {
			new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h'),
			new LongOpt("db-path", LongOpt.REQUIRED_ARGUMENT, null, 'f')
		};

		Getopt getopt = new Getopt(name, args, shortOpts, longOpts);

		int c;
		while (-1 != (c = getopt.getopt())) {
			switch (c) {
				case 'h':
					showHelp();
					System.exit(0);
					break;
				case 'f':
					dbPath = getopt.getOptarg();
					break;
			}
		}

		if (null == dbPath) {
			System.err.println("db-path is a required option!");
			System.exit(1);
		}
	}

	private static void showHelp() {
		System.out.printf(
			"USAGE: %1$s -h\n" +
			"       %1$s -f <DBPATH>\n" +
			"\n" +
			"OPTIONS:\n" +
			"    -h|--help               Show this message.\n" +
			"    -f|--db-path <DBPATH>   Use DBPATH as the path to the database.\n",
				name);
	}
}

// vim: set ts=4 sw=4 noet syn=java:
