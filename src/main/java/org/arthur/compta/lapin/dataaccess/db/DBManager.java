package org.arthur.compta.lapin.dataaccess.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.arthur.compta.lapin.dataaccess.files.FilesManager;

/**
 * Gère l'accès à la base de donnée
 */
public class DBManager {

	/** l'instance du singleton */
	private static DBManager _instance;

	private Connection connexionDB;

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
			connexionDB = DriverManager.getConnection("jdbc:hsqldb:file:" + pathToDb + ";ifexists=true", "SA", "");

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
			connexionDB = DriverManager.getConnection("jdbc:hsqldb:file:" + pathToDb.toString() + ";ifexists=false",
					"SA", "");

			loadScript(connexionDB, "create_db.sql");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Ajout un compte dans la base de donnée
	 * 
	 * @param nom
	 *            le nom du compte
	 * @param solde
	 *            le solde
	 * @param livret
	 *            est livret ?
	 * @param budgetAllowed
	 *            participe aux budgets ?
	 * @return ID en base du compte
	 * @throws SQLException
	 *             Exception en cas de problème lors de l'insertion
	 */
	public String addCompte(String nom, double solde, boolean livret, boolean budgetAllowed) throws SQLException {

		String id = "";

		// insertion du compte en base
		Statement stmt = connexionDB.createStatement();
		ResultSet res = stmt.executeQuery("INSERT INTO COMPTE (nom,solde,is_livret,budget_allowed) VALUES (\'" + nom
				+ "\'," + solde + "," + livret + "," + budgetAllowed + ");CALL IDENTITY();");

		// récupération de l'id en base du compte créé
		if (res.getMetaData().getColumnCount() == 1 && res.next()) {
			id = res.getObject(1).toString().trim();
		}
		
		//libération des ressources JDBC
		stmt.close();
		res.close();		

		return id;
	}

	/**
	 * Récupère toutes les informations comptes de la base de donnée
	 * @return couple clé : identifiant et valeurs [nom,solde,livret,budget]
	 * @throws SQLException Exception sur la récupération en base
	 */
	public HashMap<String, String[]> getAllCompte() throws SQLException {

		HashMap<String, String[]> infos = new HashMap<String, String[]>();

		// requête sur la table COMPTE
		Statement stmt = connexionDB.createStatement();
		ResultSet res = stmt.executeQuery("SELECT ID,nom,solde,is_livret,budget_allowed FROM COMPTE;");

		if (res.getMetaData().getColumnCount() == 5) {

			while (res.next()) {
				//récupération des champs
				String[] values = new String[4];
				values[0] = res.getObject(2).toString().trim();
				values[1] = res.getObject(3).toString().trim();
				values[2] = res.getObject(4).toString().trim();
				values[3] = res.getObject(5).toString().trim();

				infos.put(res.getObject(1).toString().trim(), values);
			}
		}
		
		//libération des ressources JDBC
		stmt.close();
		res.close();
		
		return infos;
	}

}
