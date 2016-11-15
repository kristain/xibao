package com.android.library.third.ormlite.stmt.mapped;

import java.sql.SQLException;

import com.android.library.third.ormlite.field.FieldType;
import com.android.library.third.ormlite.field.SqlType;
import com.android.library.third.ormlite.logger.Log.Level;
import com.android.library.third.ormlite.stmt.ArgumentHolder;
import com.android.library.third.ormlite.stmt.PreparedDelete;
import com.android.library.third.ormlite.stmt.PreparedQuery;
import com.android.library.third.ormlite.stmt.PreparedUpdate;
import com.android.library.third.ormlite.stmt.StatementBuilder;
import com.android.library.third.ormlite.stmt.StatementBuilder.StatementType;
import com.android.library.third.ormlite.support.CompiledStatement;
import com.android.library.third.ormlite.support.DatabaseConnection;
import com.android.library.third.ormlite.table.TableInfo;

/**
 * Mapped statement used by the {@link StatementBuilder#prepareStatement(Long)} method.
 * 
 * @author graywatson
 */
public class MappedPreparedStmt<T, ID> extends BaseMappedQuery<T, ID> implements PreparedQuery<T>, PreparedDelete<T>,
		PreparedUpdate<T> {

	private final ArgumentHolder[] argHolders;
	private final Long limit;
	private final StatementType type;

	public MappedPreparedStmt(TableInfo<T, ID> tableInfo, String statement, FieldType[] argFieldTypes,
			FieldType[] resultFieldTypes, ArgumentHolder[] argHolders, Long limit, StatementType type) {
		super(tableInfo, statement, argFieldTypes, resultFieldTypes);
		this.argHolders = argHolders;
		// this is an Integer because it may be null
		this.limit = limit;
		this.type = type;
	}

	public CompiledStatement compile(DatabaseConnection databaseConnection, StatementType type) throws SQLException {
		return compile(databaseConnection, type, DatabaseConnection.DEFAULT_RESULT_FLAGS);
	}

	public CompiledStatement compile(DatabaseConnection databaseConnection, StatementType type, int resultFlags)
			throws SQLException {
		if (this.type != type) {
			throw new SQLException("Could not compile this " + this.type
					+ " statement since the caller is expecting a " + type
					+ " statement.  Check your QueryBuilder methods.");
		}
		CompiledStatement stmt = databaseConnection.compileStatement(statement, type, argFieldTypes, resultFlags);
		// this may return null if the stmt had to be closed
		return assignStatementArguments(stmt);
	}

	public String getStatement() {
		return statement;
	}

	public StatementType getType() {
		return type;
	}

	public void setArgumentHolderValue(int index, Object value) throws SQLException {
		if (index < 0) {
			throw new SQLException("argument holder index " + index + " must be >= 0");
		}
		if (argHolders.length <= index) {
			throw new SQLException("argument holder index " + index + " is not valid, only " + argHolders.length
					+ " in statement (index starts at 0)");
		}
		argHolders[index].setValue(value);
	}

	/**
	 * Assign arguments to the statement.
	 * 
	 * @return The statement passed in or null if it had to be closed on error.
	 */
	private CompiledStatement assignStatementArguments(CompiledStatement stmt) throws SQLException {
		boolean ok = false;
		try {
			if (limit != null) {
				// we use this if SQL statement LIMITs are not supported by this database type
				stmt.setMaxRows(limit.intValue());
			}
			// set any arguments if we are logging our object
			Object[] argValues = null;
			if (logger.isLevelEnabled(Level.TRACE) && argHolders.length > 0) {
				argValues = new Object[argHolders.length];
			}
			for (int i = 0; i < argHolders.length; i++) {
				Object argValue = argHolders[i].getSqlArgValue();
				FieldType fieldType = argFieldTypes[i];
				SqlType sqlType;
				if (fieldType == null) {
					sqlType = argHolders[i].getSqlType();
				} else {
					sqlType = fieldType.getSqlType();
				}
				stmt.setObject(i, argValue, sqlType);
				if (argValues != null) {
					argValues[i] = argValue;
				}
			}
			logger.debug("prepared statement '{}' with {} args", statement, argHolders.length);
			if (argValues != null) {
				// need to do the (Object) cast to force args to be a single object
				logger.trace("prepared statement arguments: {}", (Object) argValues);
			}
			ok = true;
			return stmt;
		} finally {
			if (!ok) {
				stmt.close();
			}
		}
	}
}
