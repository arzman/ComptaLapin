package org.arthur.compta.lapin.dataaccess.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.dataaccess.files.FilesManager;

/**
 * Gère l'accès à la base de donnée
 */
public class DBManager {

	/** l'instance du singleton */
	private static DBManager _instance;

	private final Logger _logger;

	/**
	 * Connexion à la base
	 */
	private Connection _connexionDB;

	/**
	 * Retourne l'instance unique du singleton
	 * 
	 * @return
	 */
	public static DBManager getInstance() {

		if (_instance == null) {

			_instance = new DBManager();
		}

		return _instance;
	}

	/** Le constructeur par défaut */
	private DBManager() {

		_logger = LogManager.getLogger(DBManager.class);

		Path pathToDb = Paths.get(FilesManager.getInstance().getDBFolder().toString(), "db_data");

		try {
			// tentative de connexion à la base
			_connexionDB = DriverManager.getConnection("jdbc:hsqldb:file:" + pathToDb + ";ifexists=true", "sa", "");

		} catch (Throwable e) {
			createDB(pathToDb);
		}

		// on met la base a jour ( si besoin)
		if (Boolean.valueOf(ConfigurationManager.getInstance().getProp("DBManager.checkupdate", Boolean.toString(true)))) {
			try {
				DBUpdateService.checkUpdate(_connexionDB);
			} catch (ComptaException e) {
				_logger.fatal(e);
			}
		}

	}

	/**
	 * Exécute un script SQL "interne"
	 * 
	 * @param connexion
	 *            la connexion à la base
	 * @param script
	 *            le script a executer
	 */
	private void loadScript(Connection connexion, String script) {

		try (InputStream input = getClass().getResourceAsStream("/org/arthur/compta/lapin/dataaccess/db/ressource/" + script);
				InputStreamReader reader = new InputStreamReader(input);
				BufferedReader bReader = new BufferedReader(reader);) {

			StringBuffer sb = new StringBuffer();
			String str = "";
			while ((str = bReader.readLine()) != null) {
				if (!str.startsWith("#") && !str.trim().isEmpty()) {
					sb.append(str + "\n ");
				}
			}

			Statement stmt = connexion.createStatement();

			stmt.executeUpdate(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Méthode de création de la base de donnée
	 *
	 * @param pathToDb
	 *            le fichier de donnée de la base
	 */
	private void createDB(Path pathToDb) {

		// création de la base
		try {
			_connexionDB = DriverManager.getConnection("jdbc:hsqldb:file:" + pathToDb.toString() + ";ifexists=false", "sa", "");

			loadScript(_connexionDB, "create_db.sql");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Retourne une connexion valide
	 * 
	 * @return
	 */
	synchronized Connection getConnexion() {
		return _connexionDB;
	}

	/**
	 * Ferme la base de donnée
	 */
	public void stop() {

		String query = "SHUTDOWN;";

		try {
			getConnexion().createStatement().executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Ferme la base de donnée
	 */
	public void closeDataBase() {

		try (PreparedStatement pst = _connexionDB.prepareStatement("SHUTDOWN")) {
			pst.execute();

		} catch (SQLException e) {
			_logger.fatal(e);
		}

	}

}
