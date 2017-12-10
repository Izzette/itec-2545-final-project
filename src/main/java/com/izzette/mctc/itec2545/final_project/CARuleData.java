package com.izzette.mctc.itec2545.final_project;

import java.util.ArrayList;
import java.util.List;

import java.sql.*;

/** Manipulate rule database. */
class CARuleData {
	private static CARuleData ruleData = null;

	private final Connection conn;

	/** Create the singleton instance.
	 * @param dbPath The file path to the database.
	 * @throws SQLException If connection to the database or table setup failed.
	 * @throws RuntimeException If the class has already been instancized.
	 */
	static void instancize(String dbPath) throws SQLException {
		if (null != ruleData)
			throw new RuntimeException("Double instancization of CARuleData.");

		ruleData = new CARuleData(dbPath);
	}

	/** Get the singleton instance.
	 * @return The singleton instance.
	 */
	static CARuleData getInstance() {
		return ruleData;
	}

	/** Save a rule in the database.
	 * @param rule The rule to save.
	 * @return A new RuleParams object with the id field set.
	 */
	RuleParams storeRule(RuleParams rule) throws SQLException {
		PreparedStatement storeRuleStatement = conn.prepareStatement(
			"INSERT INTO rules (" +
				"r, k, rule, comment" +
			") VALUES (" +
				"?, ?, ?, ?" +
			")",
				Statement.RETURN_GENERATED_KEYS
		);

		storeRuleStatement.setInt(1, rule.r);
		storeRuleStatement.setInt(2, rule.k);
		storeRuleStatement.setString(3, rule.rule);
		storeRuleStatement.setString(4, rule.comment);

		int id = storeRuleStatement.executeUpdate();

		return new RuleParams(id, rule.r, rule.k, rule.rule, rule.comment);
	}

	/** Ensure the rule with the matching ID is not in the database.
	 * @param rule The rule to delete, must have the id field set.
	 * @throws IllegalArgumentException If the rule does not have the id field set.
	 */
	void deleteRule(RuleParams rule) throws SQLException {
		if (null == rule.id)
			throw new IllegalArgumentException();

		PreparedStatement removeRuleStatement = conn.prepareStatement(
			"DELETE FROM rules " +
			"WHERE id = ?"
		);

		removeRuleStatement.setInt(1, rule.id);

		removeRuleStatement.execute();
	}

	/** Get all the rules in the database.
	 * @return An array of all the rules in the database (id field set).
	 */
	RuleParams[] getAllRules() throws SQLException {
		List<RuleParams> ruleParamsList = new ArrayList<>();

		Statement getAllRulesStatement = conn.createStatement();
		ResultSet getAllRulesResult = getAllRulesStatement.executeQuery(
			"SELECT id, r, k, rule, comment " +
			"FROM rules"
		);

		while (getAllRulesResult.next())
			ruleParamsList.add(new RuleParams(
					getAllRulesResult.getInt("id"),
					getAllRulesResult.getInt("r"),
					getAllRulesResult.getInt("k"),
					getAllRulesResult.getString("rule"),
					getAllRulesResult.getString("comment")));


		RuleParams[] ruleParams = new RuleParams[ruleParamsList.size()];
		return ruleParamsList.toArray(ruleParams);
	}

	private CARuleData(String dbPath) throws SQLException {
		conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbPath));

		prepareTables();
	}

	private void prepareTables() throws SQLException {
		Statement createRulesTableStatement = conn.createStatement();
		createRulesTableStatement.execute(
			"CREATE TABLE IF NOT EXISTS rules (" +
				"id      INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"r       INTEGER NOT NULL, " +
				"k       INTEGER NOT NULL, " +
				"rule    VARCHAR NOT NULL, " +
				"comment VARCHAR NOT NULL" +
			")"
		);
	}

	/** The rule parameters. */
	static class RuleParams {
		/** The unique id for this rule in the database, or null if it was not
		 *  obtained from the database. */
		final Integer id;
		/** The neighbourhood radius (r). */
		final int r;
		/** The number of colors (k). */
		final int k;
		/** The rule as a base 10 string (rule). */
		final String rule;
		/** A comment field for the rule. */
		final String comment;

		/** Create a new rule params object with id set to null.
		 * @param r The neighbourhood radius (r).
		 * @param k The number of colors (k).
		 * @param rule The rule as a base 10 string (rule).
		 * @param comment A comment field for the rule.
		 */
		RuleParams(int r, int k, String rule, String comment) {
			this(null, r, k, rule, comment);
		}

		private RuleParams(Integer id, int r, int k, String rule, String comment) {
			this.id = id;
			this.r = r;
			this.k = k;
			this.rule = rule;
			this.comment = comment;
		}
	}
}

// vim: set ts=4 sw=4 noet syn=java:
