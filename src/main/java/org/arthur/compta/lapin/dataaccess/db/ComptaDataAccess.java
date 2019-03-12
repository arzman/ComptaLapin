package org.arthur.compta.lapin.dataaccess.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ComptaDataAccess {

	/**
	 * Le Logger
	 */
	private final Logger _logger;

	public ComptaDataAccess() {

		_logger = LogManager.getLogger(ComptaDataAccess.class);

	}

	/**
	 * Centralisation des updates BD
	 * 
	 * @param stmt
	 * @throws SQLException
	 */
	protected void executeUpdate(PreparedStatement stmt) throws SQLException {

		_logger.debug(stmt.toString());
		stmt.executeUpdate();

	}

	/**
	 * Centralisation des updates BD
	 * 
	 * @param stmt
	 * @return
	 * @throws SQLException
	 */
	public ResultSet executeQuery(PreparedStatement stmt) throws SQLException {

		_logger.debug(stmt.toString());
		return stmt.executeQuery();

	}

}
