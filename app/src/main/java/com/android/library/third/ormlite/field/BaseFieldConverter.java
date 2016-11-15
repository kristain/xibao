package com.android.library.third.ormlite.field;

import com.android.library.third.ormlite.support.DatabaseResults;

import java.sql.SQLException;

/**
 * Base class for field-converters.
 * 
 * @author graywatson
 */
public abstract class BaseFieldConverter implements com.android.library.third.ormlite.field.FieldConverter {

	/**
	 * @throws SQLException
	 *             If there are problems with the conversion.
	 */
	public Object javaToSqlArg(com.android.library.third.ormlite.field.FieldType fieldType, Object javaObject) throws SQLException {
		// noop pass-thru
		return javaObject;
	}

	public Object resultToJava(com.android.library.third.ormlite.field.FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		Object value = resultToSqlArg(fieldType, results, columnPos);
		if (value == null) {
			return null;
		} else {
			return sqlArgToJava(fieldType, value, columnPos);
		}
	}

	/**
	 * @throws SQLException
	 *             If there are problems with the conversion.
	 */
	public Object sqlArgToJava(com.android.library.third.ormlite.field.FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
		// noop pass-thru
		return sqlArg;
	}

	public boolean isStreamType() {
		return false;
	}
}
