package common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import MyExceptions.SystemMalfunctionException;

public final class ResourceUtils {

	private ResourceUtils() {
		// Can be called via Reflection
		throw new AssertionError("non instantiable!");
	}

	public static void close(Statement... statements) throws SystemMalfunctionException {
		try {
			for (int i = 0; i < statements.length; i++) {
				if (statements[i] != null) {
					statements[i].close();
				}
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("Unable to close statement!" + e.getMessage());
		}
	}
	
	public static void close(ResultSet... resultsSets) throws SystemMalfunctionException {
		try {
			for (int i = 0; i < resultsSets.length; i++) {
				if (resultsSets[i] != null) {
					resultsSets[i].close();
				}
			}
		} catch (SQLException e) {
			throw new SystemMalfunctionException("Unable to close statement!" + e.getMessage());
		}
	}
}
