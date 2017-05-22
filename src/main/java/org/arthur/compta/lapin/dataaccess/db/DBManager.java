package org.arthur.compta.lapin.dataaccess.db;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.dataaccess.files.FilesManager;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

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
			connexionDB = DriverManager.getConnection("jdbc:hsqldb:file:" + pathToDb + ";ifexists=true", "sa", "");

		} catch (Exception e) {
			System.out.println("Création de la base de donnée");
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
				if (!str.startsWith("#") && !str.trim().isEmpty()) {
					sb.append(str + "\n ");
				}
			}

			Statement stmt = connexion.createStatement();

			System.out.println(sb.toString());

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
					"sa", "");

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
	public synchronized String addCompte(String nom, double solde, boolean livret, boolean budgetAllowed)
			throws SQLException {

		String id = "";

		// préparation de la requête
		String query = "INSERT INTO COMPTE (nom,solde,is_livret,budget_allowed) VALUES (?,?,?,?);";
		PreparedStatement stmt = connexionDB.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, nom);
		stmt.setDouble(2, solde);
		stmt.setBoolean(3, livret);
		stmt.setBoolean(4, budgetAllowed);
		// execution
		stmt.executeUpdate();

		// récupération de l'id en base du compte créé
		ResultSet res = stmt.getGeneratedKeys();
		if (res.getMetaData().getColumnCount() == 1 && res.next()) {
			id = res.getString(1).trim();
		}

		// libération des ressources JDBC
		stmt.close();
		res.close();

		return id;
	}

	/**
	 * Récupère toutes les informations comptes de la base de donnée
	 * 
	 * @return couple clé : identifiant et valeurs [nom,solde,livret,budget]
	 * @throws SQLException
	 *             Exception sur la récupération en base
	 */
	public synchronized HashMap<String, String[]> getAllCompte() throws SQLException {

		HashMap<String, String[]> infos = new HashMap<String, String[]>();

		// requête sur la table COMPTE
		Statement stmt = connexionDB.createStatement();
		ResultSet res = stmt.executeQuery("SELECT ID,nom,solde,is_livret,budget_allowed FROM COMPTE;");

		if (res.getMetaData().getColumnCount() == 5) {

			while (res.next()) {
				// récupération des champs
				String[] values = new String[4];
				values[0] = res.getString("nom").trim();
				values[1] = String.valueOf(res.getDouble("solde")).trim();
				values[2] = String.valueOf(res.getBoolean("is_livret")).trim();
				values[3] = String.valueOf(res.getBoolean("budget_allowed")).trim();

				infos.put(String.valueOf(res.getInt("ID")).trim(), values);
			}
		}

		// libération des ressources JDBC
		res.close();
		stmt.close();

		return infos;
	}

	/**
	 * Supprime le compte correspondant à l'identifiant applicatif passé en
	 * paramètre
	 * 
	 * @param appId
	 *            l'id
	 * @throws SQLException
	 *             Exception si la requête en base échoue
	 */
	public synchronized void removeCompte(String appId) throws SQLException {

		// préparation de la requête de suppression
		PreparedStatement stmt = connexionDB.prepareStatement("DELETE FROM COMPTE WHERE ID = ?");
		stmt.setString(1, appId);
		// execution
		stmt.executeUpdate();

	}

	/**
	 * Met à jour le compte en base
	 * 
	 * @param appId
	 *            l'id du compte a mettre a jour
	 * @param nom
	 *            le nouveau nom
	 * @param solde
	 *            le nouveau solde
	 * @param isLivret
	 *            le nouveau flag islivret
	 * @param isBudget
	 *            le nouveau flag isBudget
	 * @throws SQLException
	 *             Exception si la requête en base échoue
	 */
	public synchronized void updateCompte(String appId, String nom, double solde, boolean isLivret, boolean isBudget)
			throws SQLException {

		// préparation de la requête
		PreparedStatement stmt = connexionDB
				.prepareStatement("UPDATE COMPTE SET nom=?,solde=?,is_livret=?,budget_allowed=? WHERE ID = ?");
		stmt.setString(1, nom);
		stmt.setDouble(2, solde);
		stmt.setBoolean(3, isLivret);
		stmt.setBoolean(4, isBudget);
		stmt.setString(5, appId);
		// execution
		stmt.executeUpdate();

	}

	/**
	 * Récupère en base l'id du trimestre courant
	 * 
	 * @return les champ du compte courant
	 * @throws SQLException
	 *             Echec de la récupération
	 */
	public synchronized String[] getTrimestreCourantId() throws SQLException {

		String[] res = new String[1];

		// création de la requete
		String query = "SELECT ID_TRIMESTRE FROM CONFIGURATION limit 1";
		PreparedStatement stmt = connexionDB.prepareStatement(query);
		// execution
		ResultSet queryRes = stmt.executeQuery();
		// parse du resultat
		while (queryRes.next()) {

			res[0] = String.valueOf(queryRes.getInt("ID_TRIMESTRE"));

		}

		return res;
	}

	/**
	 * Récupère en base les champ d'un trimestre [ id trimestre, id 1er mois, id
	 * 2eme mois, id 3 eme mois]
	 * 
	 * @return les champ du compte courant
	 * @throws SQLException
	 *             Echec de la récupération
	 */
	public String[] getTrimestreInfo(String appId) throws SQLException {

		String[] res = new String[4];

		// création de requete
		String query = "SELECT ID,premier_mois_id,deux_mois_id,trois_mois_id FROM TRIMESTRE WHERE ID=?";
		PreparedStatement stmt = connexionDB.prepareStatement(query);
		stmt.setInt(1, Integer.parseInt(appId));
		ResultSet queryRes = stmt.executeQuery();

		while (queryRes.next()) {

			res[0] = queryRes.getString(1);
			res[1] = queryRes.getString(2);
			res[2] = queryRes.getString(3);
			res[3] = queryRes.getString(4);

		}

		return res;
	}

	/**
	 * Récupère les champ en base d'un exercice mensuel [ ID , date_debut ,
	 * date_fin]
	 * 
	 * @param id
	 *            l'id de l'exercice
	 * @return les champs en base
	 * @throws SQLException
	 *             Echec de la récupération
	 */
	public String[] getExMensuelInfos(String id) throws SQLException {

		String[] res = new String[3];
		// création de la requete
		String query = "SELECT ID ,date_debut,date_fin FROM EXERCICE_MENSUEL WHERE ID=?";
		PreparedStatement stmt = connexionDB.prepareStatement(query);
		stmt.setInt(1, Integer.parseInt(id));
		// exécution
		ResultSet queryRes = stmt.executeQuery();

		while (queryRes.next()) {
			// parsing du résultat
			res[0] = queryRes.getString("ID");
			res[1] = ApplicationFormatter.databaseDateFormat.format(queryRes.getDate("date_debut"));
			res[0] = ApplicationFormatter.databaseDateFormat.format(queryRes.getDate("date_fin"));

		}

		return res;
	}

	/**
	 * Ajoute un exercice mensuel en base de donnée
	 * 
	 * @param debut
	 *            date de début
	 * @param fin
	 *            date de fin
	 * @return l'identifiant de l'exercice inséré
	 * @throws SQLException
	 *             Echec de l'insertion
	 */
	public String addExerciceMensuel(Calendar debut, Calendar fin) throws SQLException {
		String id = "";

		// préparation de la requête
		String query = "INSERT INTO EXERCICE_MENSUEL (date_debut,date_fin) VALUES (?,?);";
		PreparedStatement stmt = connexionDB.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		stmt.setDate(1, new Date(debut.getTime().getTime()));
		stmt.setDate(2, new Date(fin.getTime().getTime()));

		// execution
		stmt.executeUpdate();

		// récupération de l'id en base du compte créé
		ResultSet res = stmt.getGeneratedKeys();
		if (res.getMetaData().getColumnCount() == 1 && res.next()) {
			id = res.getString(1).trim();
		}

		// libération des ressources JDBC
		stmt.close();
		res.close();

		return id;
	}

	/**
	 * Ajoute un trimestre en base
	 * 
	 * @param idMois1
	 *            identifiant applicatif du premier mois
	 * @param idMois2
	 *            identifiant applicatif du deuxieme mois
	 * @param idMois3
	 *            identifiant applicatif du troisieme mois
	 * @return
	 * @throws ComptaException
	 *             Echec de l'insertion
	 */
	public String addTrimestre(String idMois1, String idMois2, String idMois3) throws ComptaException {

		String id = null;
		// préparation de la requête
		String query = "INSERT INTO TRIMESTRE (premier_mois_id,deux_mois_id,trois_mois_id) VALUES (?,?,?);";
		try (PreparedStatement stmt = connexionDB.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, idMois1);
			stmt.setString(2, idMois2);
			stmt.setString(3, idMois3);

			// execution
			stmt.executeUpdate();

			// récupération de l'id en base du compte créé
			ResultSet res = stmt.getGeneratedKeys();
			if (res.getMetaData().getColumnCount() == 1 && res.next()) {
				id = res.getString(1).trim();
			}
		} catch (Exception e) {
			throw new ComptaException("Impossible d'ajouter un trimestre", e);
		}

		return id;
	}

	/**
	 * Sauvegarde l'id du trimestre courant en base
	 * 
	 * @param appId
	 *            le nouvel id
	 * @throws ComptaException
	 *             Echec de l'insertion
	 */
	public void setTrimestreCourant(String appId) throws ComptaException {

		// préparation de la requête
		String query = "UPDATE CONFIGURATION SET ID_TRIMESTRE=?;";
		try (PreparedStatement stmt = connexionDB.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setInt(1, Integer.parseInt(appId));
			stmt.executeUpdate();

			if (stmt.getUpdateCount() == 0) {
				// pas d'update, on insert
				String query2 = "INSERT INTO CONFIGURATION (date_verif,ID_TRIMESTRE) VALUES (?,?);";
				try (PreparedStatement stmt2 = connexionDB.prepareStatement(query2)) {
					stmt2.setDate(1, new Date(Calendar.getInstance().getTime().getTime()));
					stmt2.setInt(2, Integer.parseInt(appId));
					stmt2.executeUpdate();
				} catch (Exception e) {
					throw new ComptaException("Impossible d'insérer la nouvelle configuration en base", e);
				}
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour la nouvelle configuration en base", e);
		}

	}

	/**
	 * Retourne une liste avec tout les identifiants des trimestres en base
	 * 
	 * @return
	 * @throws ComptaException
	 *             Echec de la récupération
	 */
	public ArrayList<String> getAllTrimestreId() throws ComptaException {

		ArrayList<String> res = new ArrayList<>();

		String query = "SELECT ID FROM TRIMESTRE;";
		try (PreparedStatement stmt = connexionDB.prepareStatement(query);) {
			ResultSet queryRes = stmt.executeQuery();
			while (queryRes.next()) {
				// parsing du résultat
				res.add(queryRes.getString("ID"));

			}
		} catch (Exception e) {
			throw new ComptaException("La récupération en base a échouée", e);
		}

		return res;
	}

	/**
	 * Récupération de la date de début d'un trimestre
	 * 
	 * @param id
	 *            l'id du trimestre
	 * @return
	 * @throws ComptaException
	 *             Echec de la récupération
	 */
	public Date getDateDebutFromTrimestre(String id) throws ComptaException {

		Date res = null;
		// récupération de la date de début du premier exercice mensuel
		String query = "SELECT date_debut FROM EXERCICE_MENSUEL E INNER JOIN TRIMESTRE T ON E.ID=T.premier_mois_id WHERE T.ID=? ;";

		try (PreparedStatement stmt = connexionDB.prepareStatement(query);) {

			stmt.setInt(1, Integer.parseInt(id));
			ResultSet queryRes = stmt.executeQuery();

			while (queryRes.next()) {
				// parsing du résultat
				res = queryRes.getDate("date_debut");
			}

		} catch (Exception e) {
			throw new ComptaException("La récupération en base a échouée", e);
		}

		return res;
	}

	/**
	 * Supprime un trimestre de la base de donnée
	 * 
	 * @param idTrimestrel'id
	 *            du trimestre
	 * @throws SQLException
	 *             Echec de la suppression
	 */
	public void removeTrimestre(String idTrimestre) throws ComptaException {

		// suppression des excercices mensuels
		String queryEM = "SELECT premier_mois_id,deux_mois_id,trois_mois_id FROM TRIMESTRE WHERE ID=?;";
		try (PreparedStatement stmtEM = connexionDB.prepareStatement(queryEM)) {

			stmtEM.setInt(1, Integer.valueOf(idTrimestre));
			ResultSet queryRes = stmtEM.executeQuery();

			// suppression du trimestre
			String query = "DELETE FROM TRIMESTRE WHERE ID=? ;";
			try (PreparedStatement stmt = connexionDB.prepareStatement(query)) {

				stmt.setInt(1, Integer.parseInt(idTrimestre));
				stmt.executeUpdate();
			} catch (Exception e) {
				throw new ComptaException("Impossible de supprimer le trimestre", e);
			}

			while (queryRes.next()) {
				// suppression des exercice mensuel
				String idPremierMois = String.valueOf(queryRes.getInt("premier_mois_id"));
				removeExcerciceMensuel(idPremierMois);
				String idDeuxMois = String.valueOf(queryRes.getInt("deux_mois_id"));
				removeExcerciceMensuel(idDeuxMois);
				String idTroisMois = String.valueOf(queryRes.getInt("trois_mois_id"));
				removeExcerciceMensuel(idTroisMois);
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer le trimestre", e);
		}

	}

	/**
	 * Supprime un exercice mensuel de la base
	 * 
	 * @param idMois
	 *            l'id de l'exercice mensuel à supprimer
	 * @throws ComptaException
	 *             Echec de la suppression
	 */
	private void removeExcerciceMensuel(String idMois) throws ComptaException {
		// suppression de l'exercie
		String query = "DELETE FROM EXERCICE_MENSUEL WHERE ID=? ;";
		try (PreparedStatement stmt = connexionDB.prepareStatement(query)) {
			stmt.setInt(1, Integer.parseInt(idMois));
			stmt.executeUpdate();
		} catch (Exception e) {
			throw new ComptaException("Impossible de supprimer l'exercice mensuel", e);
		}

	}

	/**
	 * Récupère les infos du template de trimestre de la base de donnée
	 * 
	 * key : ID , value
	 * [nom,montant,type,frequence,occurence,compte_source_id,compte_cible_id]
	 * 
	 * @return une map contenant les infos du template
	 * @throws ComptaException
	 */
	public HashMap<String, String[]> loadTemplateInfo() throws ComptaException {

		HashMap<String, String[]> infos = new HashMap<>();

		String query = "SELECT ID,nom,montant,type,frequence,occurence,compte_source_id,compte_cible_id FROM TEMPLATE;";
		try (PreparedStatement stmt = connexionDB.prepareStatement(query)) {

			ResultSet queryRes = stmt.executeQuery();

			while (queryRes.next()) {
				// parsing du résultat
				String[] elt = new String[7];

				elt[0] = queryRes.getString("nom");
				elt[1] = String.valueOf(queryRes.getString("montant"));
				elt[2] = String.valueOf(queryRes.getDouble("type"));
				elt[3] = String.valueOf(queryRes.getString("frequence"));
				elt[4] = String.valueOf(queryRes.getString("occurence"));
				elt[5] = String.valueOf(queryRes.getString("compte_source_id"));
				elt[6] = String.valueOf(queryRes.getString("compte_cible_id"));

				infos.put(queryRes.getString("ID"), elt);
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de recupérer le template", e);
		}

		return infos;
	}

}
