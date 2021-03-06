package com.android.library.third.ormlite.field.types;

import java.sql.SQLException;
import java.util.UUID;

import com.android.library.third.ormlite.field.FieldType;
import com.android.library.third.ormlite.field.SqlType;
import com.android.library.third.ormlite.misc.SqlExceptionUtil;
import com.android.library.third.ormlite.support.DatabaseResults;

/**
 * Type that persists a {@link UUID} object.
 * 
 * @author graywatson
 */
public class UuidType extends BaseDataType {

	public static int DEFAULT_WIDTH = 48;

	private static final UuidType singleTon = new UuidType();

	public static UuidType getSingleton() {
		return singleTon;
	}

	private UuidType() {
		super(SqlType.STRING, new Class<?>[] { UUID.class });
	}

	/**
	 * Here for others to subclass.
	 */
	protected UuidType(SqlType sqlType, Class<?>[] classes) {
		super(sqlType, classes);
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
		try {
			return java.util.UUID.fromString(defaultStr);
		} catch (IllegalArgumentException e) {
			throw SqlExceptionUtil.create("Problems with field " + fieldType + " parsing default UUID-string '"
					+ defaultStr + "'", e);
		}
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		return results.getString(columnPos);
	}

	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
		String uuidStr = (String) sqlArg;
		try {
			return java.util.UUID.fromString(uuidStr);
		} catch (IllegalArgumentException e) {
			throw SqlExceptionUtil.create("Problems with column " + columnPos + " parsing UUID-string '" + uuidStr
					+ "'", e);
		}
	}

	@Override
	public Object javaToSqlArg(FieldType fieldType, Object obj) {
		UUID uuid = (UUID) obj;
		return uuid.toString();
	}

	@Override
	public boolean isValidGeneratedType() {
		return true;
	}

	@Override
	public boolean isSelfGeneratedId() {
		return true;
	}

	@Override
	public Object generateId() {
		return java.util.UUID.randomUUID();
	}

	@Override
	public int getDefaultWidth() {
		return DEFAULT_WIDTH;
	}
}
