package org.arthur.compta.lapin.dataaccess.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.arthur.compta.lapin.dataaccess.files.FilesManager;

/**
 * Gère l'accès à la base de donnée
 * 
 * @author ARDUFLOT
 *
 */
/**
 * @author ARDUFLOT
 *
 */
public class DBManager {

	/** l'instance du singleton */
	private static DBManager _instance;

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

		Path pathToDb = Paths.get(FilesManager.getInstance().getDBFolder().toString(), "db_data");

		try {
			// tentative de connexion à la base
			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:" + pathToDb + ";ifexists=true", "SA", "");

		} catch (SQLException e) {
			createDB(pathToDb);
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


		try (InputStream input = getClass()
				.getResourceAsStream("/org/arthur/compta/lapin/dataaccess/db/ressource/" + script);

				InputStreamReader reader = new InputStreamReader(input);
				BufferedReader bReader = new BufferedReader(reader);) {

			StringBuffer sb = new StringBuffer();
			String str = "";
			while ((str = bReader.readLine()) != null) {
				if (!str.startsWith("#")) {
					sb.append(str + "\n ");
				}
			}

			Statement stmt = connexion.createStatement();

			stmt.executeUpdate(sb.toString());
		} catch (Exception e) {
			System.err.println("Impossible d'executer" + script + ". Erreur :" + e.getMessage());
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
			Connection c = DriverManager.getConnection("jdbc:hsqldb:file:" + pathToDb.toString() + ";ifexists=false",
					"SA", "");
			
			loadScript(c, "create_db.sql");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
