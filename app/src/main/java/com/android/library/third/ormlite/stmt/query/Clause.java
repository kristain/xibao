package com.android.library.third.ormlite.stmt.query;

import java.sql.SQLException;
import java.util.List;

import com.android.library.third.ormlite.db.DatabaseType;
import com.android.library.third.ormlite.stmt.ArgumentHolder;

/**
 * Internal marker class for query clauses.
 * 
 * @author graywatson
 */
public interface Clause {

	/**
	 * Add to the string-builder the appropriate SQL for this clause.
	 * 
	 * @param tableName
	 *            Name of the table to prepend to any column names or null to be ignored.
	 */
	public void appendSql(DatabaseType databaseType, String tableName, StringBuilder sb, List<ArgumentHolder> argList)
			throws SQLException;
}
