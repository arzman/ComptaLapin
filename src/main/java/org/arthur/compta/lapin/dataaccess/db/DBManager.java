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
import java.sql.Types;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.AppUtilisation;
import org.arthur.compta.lapin.application.model.OperationSearchResult;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;
import org.arthur.compta.lapin.dataaccess.files.FilesManager;
import org.arthur.compta.lapin.model.Budget;
import org.arthur.compta.lapin.model.Compte;
import org.arthur.compta.lapin.model.ExerciceMensuel;
import org.arthur.compta.lapin.model.Utilisation;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;
import org.arthur.compta.lapin.model.operation.TransfertOperation;
import org.arthur.compta.lapin.presentation.utils.ApplicationFormatter;

/**
 * Gère l'accès à la base de donnée
 */
public class DBManager {

	/** l'instance du singleton */
	private static DBManager _instance;

	/**
	 * Connexion à la base
	 */
	private Connection _connexionDB;

	/**
	 * Le Logger
	 */
	private static Logger logger = LogManager.getLogger(DBManager.class);

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
			_connexionDB = DriverManager.getConnection("jdbc:hsqldb:file:" + pathToDb + ";ifexists=true", "sa", "");

		} catch (Throwable e) {
			createDB(pathToDb);
		}

		// on met la base a jour ( si besoin)
		if (Boolean
				.valueOf(ConfigurationManager.getInstance().getProp("DBManager.checkupdate", Boolean.toString(true)))) {
			DBUpdateService.checkUpdate(_connexionDB);
		}

	}

	/**
	 * Exécute un script SQL "interne"
	 * 
	 * @param connexion la connexion à la base
	 * @param script    le script a executer
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

			stmt.executeUpdate(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Méthode de création de la base de donnée
	 *
	 * @param pathToDb le fichier de donnée de la base
	 */
	private void createDB(Path pathToDb) {

		// création de la base
		try {
			_connexionDB = DriverManager.getConnection("jdbc:hsqldb:file:" + pathToDb.toString() + ";ifexists=false",
					"sa", "");

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
	private synchronized Connection getConnexion() {
		return _connexionDB;
	}

	/**
	 * Ajout un compte dans la base de donnée
	 * 
	 * @param nom           le nom du compte
	 * @param solde         le solde
	 * @param livret        est livret ?
	 * @param budgetAllowed participe aux budgets ?
	 * @return ID en base du compte
	 * @throws SQLException Exception en cas de problème lors de l'insertion
	 */
	public String addCompte(String nom, double solde, boolean livret, boolean budgetAllowed) throws ComptaException {

		String id = "";

		// préparation de la requête
		String query = "INSERT INTO COMPTE (nom,solde,is_livret,budget_allowed) VALUES (?,?,?,?);";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, nom);
			stmt.setDouble(2, solde);
			stmt.setBoolean(3, livret);
			stmt.setBoolean(4, budgetAllowed);
			// execution
			executeUpdate(stmt);

			// récupération de l'id en base du compte créé
			ResultSet res = stmt.getGeneratedKeys();
			if (res.getMetaData().getColumnCount() == 1 && res.next()) {
				id = res.getString(1).trim();
			}
		} catch (Exception e) {
			throw new ComptaException("Impossible d'ajouter le compte", e);
		}

		return id;
	}

	/**
	 * Récupère toutes les informations comptes de la base de donnée
	 * 
	 * @return couple clé : identifiant et valeurs [nom,solde,livret,budget]
	 * @throws ComptaException Exception sur la récupération en base
	 */
	public HashMap<String, Compte> getAllCompte() throws ComptaException {

		HashMap<String, Compte> infos = new HashMap<String, Compte>();

		// requête sur la table COMPTE
		String query = "SELECT ID,nom,solde,is_livret,budget_allowed FROM COMPTE;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			ResultSet res = executeQuery(stmt);

			while (res.next()) {

				infos.put(String.valueOf(res.getInt("ID")).trim(), parseCompteFromRes(res));

			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer les comptes", e);
		}

		return infos;
	}

	/**
	 * Instancie un Compte depuis la persistance
	 * 
	 * @param res
	 * @return
	 * @throws SQLException
	 */
	private Compte parseCompteFromRes(ResultSet res) throws SQLException {

		Compte cpt = new Compte();
		// récupération des champs
		cpt.setNom(res.getString("nom"));
		cpt.setSolde(res.getDouble("solde"));
		cpt.setLivret(res.getBoolean("is_livret"));
		cpt.setLivret(res.getBoolean("budget_allowed"));

		return cpt;
	}

	/**
	 * Supprime le compte correspondant à l'identifiant applicatif passé en
	 * paramètre
	 * 
	 * @param appId l'id
	 * @throws SQLException Exception si la requête en base échoue
	 */
	public void removeCompte(String appId) throws SQLException {

		// préparation de la requête de suppression
		PreparedStatement stmt = getConnexion().prepareStatement("DELETE FROM COMPTE WHERE ID = ?");
		stmt.setString(1, appId);
		// execution
		stmt.executeUpdate();

	}

	/**
	 * Met à jour le compte en base
	 * 
	 * @param compte :le compte a mettre a jour en base
	 *
	 * @throws ComptaException Exception si la requête en base échoue
	 */
	public void updateCompte(AppCompte compte) throws ComptaException {

		// préparation de la requête
		String query = "UPDATE COMPTE SET nom=?,solde=?,is_livret=?,budget_allowed=? WHERE ID = ?";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {
			stmt.setString(1, compte.getNom());
			stmt.setDouble(2, compte.getSolde());
			stmt.setBoolean(3, compte.isLivret());
			stmt.setBoolean(4, compte.isBudget());
			stmt.setString(5, compte.getAppId());
			// execution
			executeUpdate(stmt);
		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour le compte");
		}

	}

	/**
	 * Récupère en base l'id du trimestre courant
	 * 
	 * @return les champ du compte courant
	 * @throws ComptaException Echec de la récupération
	 */
	public String getTrimestreCourantId() throws ComptaException {

		String res = "";

		// création de la requete
		String query = "SELECT ID_TRIMESTRE FROM CONFIGURATION limit 1";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {
			// execution
			ResultSet queryRes = executeQuery(stmt);
			// parse du resultat
			while (queryRes.next()) {

				res = String.valueOf(queryRes.getInt("ID_TRIMESTRE"));

			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer l'id du trimestre courant", e);
		}

		return res;
	}

	/**
	 * Récupère en base les champ d'un trimestre [ id trimestre, id 1er mois, id
	 * 2eme mois, id 3 eme mois]
	 * 
	 * @return les champ du compte courant
	 * @throws SQLException Echec de la récupération
	 */
	public String[] getTrimestreInfo(String appId) throws SQLException {

		String[] res = new String[4];

		// création de requete
		String query = "SELECT ID,premier_mois_id,deux_mois_id,trois_mois_id FROM TRIMESTRE WHERE ID=?";
		PreparedStatement stmt = getConnexion().prepareStatement(query);
		stmt.setInt(1, Integer.parseInt(appId));
		ResultSet queryRes = executeQuery(stmt);

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
	 * date_fin,resultat_moyen_prevu]
	 * 
	 * @param id l'id de l'exercice
	 * @return les champs en base
	 * @throws SQLException Echec de la récupération
	 */
	public HashMap<String, ExerciceMensuel> getExMensuelInfos(String id) throws SQLException {

		HashMap<String, ExerciceMensuel> res = new HashMap<>();
		// création de la requete
		String query = "SELECT ID ,date_debut,date_fin,resultat_moyen_prevu FROM EXERCICE_MENSUEL WHERE ID=?";
		PreparedStatement stmt = getConnexion().prepareStatement(query);
		stmt.setInt(1, Integer.parseInt(id));
		// exécution
		ResultSet queryRes = executeQuery(stmt);

		while (queryRes.next()) {
			// parsing du résultat
			res.put(queryRes.getString("ID"), parseExcerciceMensuelFromRes(queryRes));

		}

		return res;
	}

	/**
	 * Instancie un ExerciceMensuel depuis la persistance
	 * 
	 * @param queryRes
	 * @return
	 * @throws SQLException
	 */
	private ExerciceMensuel parseExcerciceMensuelFromRes(ResultSet queryRes) throws SQLException {

		ExerciceMensuel em = new ExerciceMensuel();

		// date de début
		em.setDateDebut(queryRes.getDate("date_debut").toLocalDate());
		// date fin
		em.setDateFin(queryRes.getDate("date_fin").toLocalDate());

		em.setResPrev(queryRes.getDouble("resultat_moyen_prevu"));

		return em;
	}

	/**
	 * Ajoute un exercice mensuel en base de donnée
	 * 
	 * @param debut    date de début
	 * @param fin      date de fin
	 * @param resPrevu : le gain moyen prévisionnel à la création
	 * @return l'identifiant de l'exercice inséré
	 * @throws ComptaException Echec de l'insertion
	 */
	public String addExerciceMensuel(LocalDate debut, LocalDate fin, double resPrevu)
			throws SQLException, ComptaException {
		String id = "";

		// préparation de la requête
		String query = "INSERT INTO EXERCICE_MENSUEL (date_debut,date_fin,resultat_moyen_prevu) VALUES (?,?,?);";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setDate(1, Date.valueOf(debut));
			stmt.setDate(2, Date.valueOf(fin));
			stmt.setDouble(3, resPrevu);

			// execution
			executeUpdate(stmt);

			// récupération de l'id en base du compte créé
			ResultSet res = stmt.getGeneratedKeys();
			if (res.getMetaData().getColumnCount() == 1 && res.next()) {
				id = res.getString(1).trim();
			}
		} catch (Exception e) {
			throw new ComptaException("Impossible d'ajouter un exercice mensuel", e);
		}

		return id;
	}

	/**
	 * Ajoute un trimestre en base
	 * 
	 * @param idMois1 identifiant applicatif du premier mois
	 * @param idMois2 identifiant applicatif du deuxieme mois
	 * @param idMois3 identifiant applicatif du troisieme mois
	 * @return
	 * @throws ComptaException Echec de l'insertion
	 */
	public String addTrimestre(String idMois1, String idMois2, String idMois3) throws ComptaException {

		String id = null;
		// préparation de la requête
		String query = "INSERT INTO TRIMESTRE (premier_mois_id,deux_mois_id,trois_mois_id) VALUES (?,?,?);";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, idMois1);
			stmt.setString(2, idMois2);
			stmt.setString(3, idMois3);

			// execution
			executeUpdate(stmt);

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
	 * @param appId le nouvel id
	 * @throws ComptaException Echec de l'insertion
	 */
	public void setTrimestreCourant(String appId) throws ComptaException {

		// préparation de la requête
		String query = "UPDATE CONFIGURATION SET ID_TRIMESTRE=?;";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setInt(1, Integer.parseInt(appId));
			executeUpdate(stmt);

			if (stmt.getUpdateCount() == 0) {
				// pas d'update, on insert
				String query2 = "INSERT INTO CONFIGURATION (date_verif,ID_TRIMESTRE) VALUES (?,?);";
				try (PreparedStatement stmt2 = getConnexion().prepareStatement(query2)) {
					stmt2.setDate(1, Date.valueOf(LocalDate.now()));
					stmt2.setInt(2, Integer.parseInt(appId));
					executeUpdate(stmt2);
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
	 * @throws ComptaException Echec de la récupération
	 */
	public ArrayList<String> getAllTrimestreId() throws ComptaException {

		ArrayList<String> res = new ArrayList<>();

		String query = "SELECT ID FROM TRIMESTRE;";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query);) {
			ResultSet queryRes = executeQuery(stmt);
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
	 * @param id l'id du trimestre
	 * @return
	 * @throws ComptaException Echec de la récupération
	 */
	public LocalDate getDateDebutFromTrimestre(String id) throws ComptaException {

		LocalDate res = null;
		// récupération de la date de début du premier exercice mensuel
		String query = "SELECT date_debut FROM EXERCICE_MENSUEL E INNER JOIN TRIMESTRE T ON E.ID=T.premier_mois_id WHERE T.ID=? ;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query);) {

			stmt.setInt(1, Integer.parseInt(id));
			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {
				// parsing du résultat
				res = queryRes.getDate("date_debut").toLocalDate();
			}

		} catch (Exception e) {
			throw new ComptaException("La récupération en base a échouée", e);
		}

		return res;
	}

	/**
	 * Supprime un trimestre de la base de donnée
	 * 
	 * @param idTrimestrel'id du trimestre
	 * @throws SQLException Echec de la suppression
	 */
	public void removeTrimestre(String idTrimestre) throws ComptaException {

		// suppression des excercices mensuels
		String queryEM = "SELECT premier_mois_id,deux_mois_id,trois_mois_id FROM TRIMESTRE WHERE ID=?;";
		try (PreparedStatement stmtEM = getConnexion().prepareStatement(queryEM)) {

			stmtEM.setInt(1, Integer.valueOf(idTrimestre));
			ResultSet queryRes = stmtEM.executeQuery();

			// suppression du trimestre
			String query = "DELETE FROM TRIMESTRE WHERE ID=? ;";
			try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

				stmt.setInt(1, Integer.parseInt(idTrimestre));
				executeUpdate(stmt);
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
			throw new ComptaException("Impossible de supprimer le trimestre", e);
		}

	}

	/**
	 * Supprime un exercice mensuel de la base. Les opérations sont également
	 * supprimées.
	 * 
	 * @param idMois l'id de l'exercice mensuel à supprimer
	 * @throws ComptaException Echec de la suppression
	 */
	private void removeExcerciceMensuel(String idMois) throws ComptaException {

		// suppression des opérations de l'exercice
		String queryOp = "DELETE FROM OPERATION WHERE mois_id=? ;";
		try (PreparedStatement stmt = getConnexion().prepareStatement(queryOp)) {
			stmt.setInt(1, Integer.parseInt(idMois));
			executeUpdate(stmt);

			// suppression de l'exercie
			String query = "DELETE FROM EXERCICE_MENSUEL WHERE ID=? ;";
			try (PreparedStatement stmt2 = getConnexion().prepareStatement(query)) {
				stmt2.setInt(1, Integer.parseInt(idMois));
				executeUpdate(stmt2);
			} catch (Exception e) {
				throw new ComptaException("Impossible de supprimer l'exercice mensuel", e);
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de supprimer les opérations de l'exercice", e);
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
	public HashMap<String, TrimestreTemplateElement> loadTemplateInfo() throws ComptaException {

		HashMap<String, TrimestreTemplateElement> infos = new HashMap<>();

		String query = "SELECT ID,nom,montant,type_ope,frequence,occurence,compte_source_id,compte_cible_id FROM TEMPLATE;";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {
				// parsing du résultat
				infos.put(queryRes.getString("ID"), parseTrimestreTemplateElement(queryRes));
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de recupérer le template", e);
		}

		return infos;
	}

	private TrimestreTemplateElement parseTrimestreTemplateElement(ResultSet queryRes) throws SQLException {

		TrimestreTemplateElement elt = new TrimestreTemplateElement();
		elt.setNom(queryRes.getString("nom"));
		elt.setMontant(queryRes.getDouble("montant"));
		elt.setType(queryRes.getString("type_ope"));
		elt.setFreq(TrimestreTemplateElementFrequence.valueOf(queryRes.getString("frequence")));
		elt.setOccurence(queryRes.getInt("occurence"));
		elt.setCompteSource(
				CompteManager.getInstance().getAppCompteFromId(String.valueOf(queryRes.getInt("compte_source_id"))));
		elt.setCompteCible(
				CompteManager.getInstance().getAppCompteFromId(String.valueOf(queryRes.getInt("compte_cible_id"))));

		return elt;
	}

	/**
	 * Vide le modèle de trimestre
	 * 
	 * @throws ComptaException Echec de la suppression
	 */
	public void clearTrimTemplate() throws ComptaException {

		String query = "DELETE FROM TEMPLATE;";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {
			executeUpdate(stmt);
		} catch (Exception e) {
			throw new ComptaException("Impossible de vider les templates", e);
		}

	}

	/**
	 * AAjoute les éléments de template aau modèle de trimestre en base de donnée
	 * 
	 * @param elements
	 * @throws ComptaException
	 */
	public void addTrimstreTempElts(List<TrimestreTemplateElement> elements) throws ComptaException {

		String query = "INSERT INTO TEMPLATE (nom,montant,type_ope,frequence,occurence,compte_source_id,compte_cible_id) VALUES (?,?,?,?,?,?,?);";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			for (TrimestreTemplateElement elt : elements) {

				stmt.setString(1, elt.getNom());
				stmt.setDouble(2, elt.getMontant());
				stmt.setString(3, elt.getType());
				stmt.setString(4, elt.getFreq().toString());
				stmt.setInt(5, elt.getOccurence());
				stmt.setInt(6, Integer.parseInt(elt.getCompteSource().getAppId()));
				if (elt.getCompteCible() != null) {
					stmt.setInt(7, Integer.parseInt(elt.getCompteCible().getAppId()));
				} else {
					stmt.setNull(7, Types.INTEGER);
				}

				stmt.addBatch();

			}

			stmt.executeBatch();

		} catch (Exception e) {
			throw new ComptaException("Impossible d'insérer l'element de le template", e);
		}

	}

	/**
	 * Ajoute une opération en base de donnée
	 * 
	 * @param dep         l'operation
	 * @param compteSrcId l'id du compte
	 * @param appEmId     l'id de l'exercice mensuel
	 * @return l'id de la depense
	 * @throws ComptaException
	 */
	public String addOperation(Operation dep, String compteSrcId, String compteCibleId, String appEmId)
			throws ComptaException {

		String id = null;
		String query = "INSERT INTO OPERATION (nom,montant,type_ope,etat,compte_source_id,compte_cible_id,mois_id) VALUES (?,?,?,?,?,?,?);";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, dep.getNom());
			stmt.setDouble(2, dep.getMontant());
			stmt.setString(3, dep.getType().toString());
			stmt.setString(4, dep.getEtat().toString());
			stmt.setInt(5, Integer.parseInt(compteSrcId));
			if (compteCibleId != null && !compteCibleId.trim().isEmpty()) {
				stmt.setInt(6, Integer.parseInt(compteCibleId));
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			stmt.setInt(7, Integer.parseInt(appEmId));

			executeUpdate(stmt);

			// récupération de l'id en base du compte créé
			ResultSet res = stmt.getGeneratedKeys();
			if (res.getMetaData().getColumnCount() == 1 && res.next()) {
				id = res.getString(1).trim();
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible d'insérer l'opération", e);
		}

		return id;
	}

	/**
	 * Retourne une map contenant les champ en base des dépenses associée à un
	 * exercice
	 * 
	 * clé : id , valeurs :
	 * [nom,montant,type_ope,etat,compte_source_id,compte_cible_id]
	 * 
	 * @param appId
	 * @return
	 * @throws ComptaException
	 */
	public HashMap<String, Operation> getOperationInfo(String exId) throws ComptaException {

		HashMap<String, Operation> map = new HashMap<>();

		// création de la requete
		String query = "SELECT ID,nom,montant,type_ope,etat,compte_source_id,compte_cible_id FROM OPERATION WHERE mois_id=?;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {
			// positionnement du parametre
			stmt.setInt(1, Integer.parseInt(exId));

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {
				map.put(queryRes.getString("ID"), parseOperationFromRes(queryRes));
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer les dépenses", e);
		}

		return map;
	}

	private Operation parseOperationFromRes(ResultSet queryRes) throws SQLException {

		Operation dep = null;
		if (queryRes.getString("type_ope").equals(OperationType.DEPENSE.toString())) {
			dep = new Operation(OperationType.DEPENSE,
					CompteManager.getInstance().getAppCompteFromId(String.valueOf(queryRes.getInt("compte_source_id")))
							.getCompte(),
					queryRes.getString("nom"), queryRes.getDouble("montant"),
					EtatOperation.valueOf(queryRes.getString("etat")));
		}
		if (queryRes.getString("type_ope").equals(OperationType.RESSOURCE.toString())) {
			dep = new Operation(OperationType.RESSOURCE,
					CompteManager.getInstance().getAppCompteFromId(String.valueOf(queryRes.getInt("compte_source_id")))
							.getCompte(),
					queryRes.getString("nom"), queryRes.getDouble("montant"),
					EtatOperation.valueOf(queryRes.getString("etat")));
		}
		if (queryRes.getString("type_ope").equals(OperationType.TRANSFERT.toString())) {
			dep = new TransfertOperation(
					CompteManager.getInstance().getAppCompteFromId(String.valueOf(queryRes.getInt("compte_source_id")))
							.getCompte(),
					queryRes.getString("nom"), queryRes.getDouble("montant"),
					EtatOperation.valueOf(queryRes.getString("etat")), CompteManager.getInstance()
							.getAppCompteFromId(String.valueOf(queryRes.getInt("compte_cible_id"))).getCompte());
		}

		return dep;
	}

	/**
	 * Mise à jour de l'opération en base
	 * 
	 * L'exercice mensuel auquel l'opération est rattaché ne changera pas.
	 * 
	 * @param appOp
	 * @throws ComptaException
	 */
	public void updateOperation(AppOperation appOp) throws ComptaException {

		// création de la requete
		String query = "UPDATE OPERATION SET nom=?,montant=?,type_ope=?,etat=?,compte_source_id=?,compte_cible_id=? WHERE ID=?;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query);) {

			stmt.setString(1, appOp.getLibelle());
			stmt.setDouble(2, appOp.getMontant());
			stmt.setString(3, appOp.getType().toString());
			stmt.setString(4, appOp.getEtat());
			stmt.setInt(5, Integer.parseInt(appOp.getCompteSource().getAppId()));

			if (appOp instanceof AppTransfert) {
				stmt.setInt(6, Integer.parseInt(((AppTransfert) appOp).getCompteCible().getAppId()));
			} else {
				stmt.setNull(6, Types.INTEGER);
			}

			stmt.setInt(7, Integer.parseInt(appOp.getAppId()));

			// execution
			executeUpdate(stmt);
		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre l'opération à jour", e);
		}

	}

	/**
	 * Supprime l'opération de la base
	 * 
	 * @param appOp
	 * @throws ComptaException Echec de la suppression
	 */
	public void removeOperation(AppOperation appOp) throws ComptaException {

		String query = "DELETE FROM OPERATION WHERE ID = ?";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			stmt.setInt(1, Integer.parseInt(appOp.getAppId()));
			executeUpdate(stmt);

		} catch (Exception e) {
			throw new ComptaException("Impossible de supprimer l'opération", e);
		}

	}

	/**
	 * Retourne la date de dernière vérification sauvée en base
	 * 
	 * @return
	 * @throws ComptaException Echec de la récupération
	 */
	public String getDateDerVerif() throws ComptaException {

		String res = "";

		// création de la requete
		String query = "SELECT date_verif FROM CONFIGURATION limit 1";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {
			// execution
			ResultSet queryRes = executeQuery(stmt);
			// parse du resultat
			while (queryRes.next()) {

				res = ApplicationFormatter.databaseDateFormat.format(queryRes.getDate("date_verif").toLocalDate());

			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer la date de dernière vérif", e);
		}

		return res;

	}

	/**
	 * Sauve en base la date de dernière modif
	 * 
	 * @param date
	 * @throws ComptaException Echec de l'écriture en base
	 */
	public void setDateDerVerif(LocalDate date) throws ComptaException {

		String query = "UPDATE CONFIGURATION SET date_verif=?;";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			stmt.setDate(1, Date.valueOf(date));
			executeUpdate(stmt);
		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre la date a jour", e);
		}

	}

	/**
	 * Effectue une recherche d'opération en base de donnée
	 * 
	 * @param lib       le libelle a trouver
	 * @param montant   le montant
	 * @param tolerance la tolérance sur le montant
	 * @return
	 * @throws ComptaException
	 */
	public HashMap<String, OperationSearchResult> searchOperation(String lib, String montant, String tolerance)
			throws ComptaException {

		HashMap<String, OperationSearchResult> res = new HashMap<>();

		String query = "SELECT O.ID,O.nom,O.montant,O.mois_id,E.date_debut FROM OPERATION O INNER JOIN EXERCICE_MENSUEL E ON O.mois_id=E.ID";

		// contiendra les critères
		String[] crit = new String[3];
		// permet la jonction entre le critere de nom et montant
		String con = " AND ";
		if (!lib.isEmpty()) {
			crit[0] = " WHERE O.nom LIKE ?";
		} else {
			// pas de critere sur le libelle....pas de AND
			con = " WHERE ";
		}
		if (!montant.isEmpty()) {

			if (!tolerance.isEmpty()) {
				// encadrement du montant
				crit[1] = con + "O.montant>=?";
				crit[2] = "AND O.montant<=?";

			} else {
				// égalité du montant
				crit[1] = con + "O.montant=?";
			}

		}

		// concatenation des criteres pour faire la requete
		for (String st : crit) {
			if (st != null) {
				query = query + st;
			}
		}
		query = query + ";";

		// lancement de la requête
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			int j = 1;

			if (crit[0] != null) {
				stmt.setString(j, "%" + lib + "%");
				j++;
			}
			if (crit[1] != null) {
				double d1 = Double.parseDouble(montant);
				if (crit[2] != null) {
					// encadrement

					double d2 = Double.parseDouble(tolerance);

					stmt.setDouble(j, d1 - d2);
					j++;
					stmt.setDouble(j, d1 + d2);

				} else {
					stmt.setDouble(j, d1);
				}

			}
			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {

				res.put(queryRes.getString("ID"), parseOperationSearchResultFromRes(queryRes));
			}
		} catch (Exception e) {

			throw new ComptaException("Echec de la recherche des opérations", e);

		}

		return res;
	}

	/**
	 * Instancie une Opération depuis la persistance
	 * 
	 * @param queryRes
	 * @return
	 * @throws SQLException
	 */
	private OperationSearchResult parseOperationSearchResultFromRes(ResultSet queryRes) throws SQLException {

		return new OperationSearchResult(queryRes.getString("nom"), queryRes.getDouble("montant"),
				queryRes.getDate("date_debut").toLocalDate());
	}

	/**
	 * Récupère les budget actifs
	 * 
	 * @return couple clé : identifiant et valeurs [nom,objectif,utilise,priority]
	 * @throws ComptaException Echec de la récupération
	 */
	public HashMap<String, Budget> getActiveBudget() throws ComptaException {

		HashMap<String, Budget> res = new HashMap<>();

		String query = "SELECT ID,nom,objectif,utilise,priority,is_actif,label_recurrent,date_recurrent FROM BUDGET WHERE is_actif=True;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {

				res.put(queryRes.getString("ID"), parseBudgetFromRes(queryRes));
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer tous les comptes de la base", e);
		}

		return res;
	}

	/**
	 * 
	 * 
	 * @param queryRes
	 * @return
	 * @throws SQLException
	 */
	private Budget parseBudgetFromRes(ResultSet queryRes) throws SQLException {

		// parsing du résultat
		Budget elt = new Budget();

		elt.setNom(queryRes.getString("nom"));
		elt.setObjectif(queryRes.getDouble("objectif"));
		elt.setMontantUtilise(queryRes.getDouble("utilise"));
		elt.setPriority(queryRes.getInt("priority"));
		elt.setIsActif(queryRes.getBoolean("is_actif"));
		elt.setLabelRecurrent(queryRes.getString("label_recurrent"));
		elt.setDateRecurrent(queryRes.getDate("date_recurrent").toLocalDate());

		return elt;
	}

	/**
	 * Ajoute un budget dans la base de donnée
	 * 
	 * @param nom            le nom
	 * @param objectif       l'objectif
	 * @param utilise        le montant utilise
	 * @param isActif        est actif ?
	 * @param dateRecurrent
	 * @param labelRecurrent
	 * @return l'id applicatif
	 * @throws ComptaException Echec de l'ajout
	 */
	public String addBudget(String nom, double objectif, double utilise, boolean isActif, int priority,
			String labelRecurrent, LocalDate dateRecurrent) throws ComptaException {
		String id = "";

		// préparation de la requête
		String query = "INSERT INTO BUDGET (nom,objectif,utilise,is_actif,priority,label_recurrent,date_recurrent) VALUES (?,?,?,?,?,?,?);";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, nom);
			stmt.setDouble(2, objectif);
			stmt.setDouble(3, utilise);
			stmt.setBoolean(4, isActif);
			stmt.setInt(5, priority);
			if (labelRecurrent != null) {
				stmt.setString(6, labelRecurrent);
			} else {
				stmt.setString(6, "");
			}
			if (dateRecurrent != null) {
				stmt.setDate(7, Date.valueOf(dateRecurrent));
			} else {
				stmt.setDate(7, Date.valueOf(LocalDate.of(1986, 6, 27)));
			}

			// execution
			executeUpdate(stmt);

			// récupération de l'id en base du compte créé
			ResultSet res = stmt.getGeneratedKeys();
			if (res.getMetaData().getColumnCount() == 1 && res.next()) {
				id = res.getString(1).trim();
			}
		} catch (Exception e) {
			throw new ComptaException("Impossible d'ajouter le budget", e);
		}

		return id;
	}

	/**
	 * Met à jour le budget en base de donnée
	 * 
	 * @param budget le budget
	 * @throws ComptaException Echec de la mise à jour
	 */
	public void updateBudget(AppBudget budget) throws ComptaException {

		// préparation de la requête
		String query = "UPDATE BUDGET SET nom=?,objectif=?,utilise=?,is_actif=?,priority=?,label_recurrent=?,date_recurrent=? WHERE ID = ?";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {
			stmt.setString(1, budget.getNom());
			stmt.setDouble(2, budget.getObjectif());
			stmt.setDouble(3, budget.getMontantUtilise());
			stmt.setBoolean(4, budget.isActif());
			stmt.setInt(5, budget.getPriority());
			stmt.setString(6, budget.getLabelRecurrent());
			stmt.setDate(7, Date.valueOf(budget.getDateRecurrent()));
			stmt.setInt(8, Integer.valueOf(budget.getAppId()));
			// execution
			executeUpdate(stmt);
		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour le budget", e);
		}

	}

	/**
	 * Met à jour les budgets en base de donnée
	 * 
	 * @param budgets les budgets
	 * @throws ComptaException Echec de la mise à jour
	 */
	public void updateBudgets(List<AppBudget> budgets) throws ComptaException {

		// préparation de la requête
		String query = "UPDATE BUDGET SET nom=?,objectif=?,utilise=?,is_actif=?,priority=?,label_recurrent=?,date_recurrent=? WHERE ID = ?";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			for (AppBudget budget : budgets) {

				stmt.setString(1, budget.getNom());
				stmt.setDouble(2, budget.getObjectif());
				stmt.setDouble(3, budget.getMontantUtilise());
				stmt.setBoolean(4, budget.isActif());
				stmt.setInt(5, budget.getPriority());
				stmt.setString(6, budget.getLabelRecurrent());
				stmt.setDate(7, Date.valueOf(budget.getDateRecurrent()));
				stmt.setInt(8, Integer.valueOf(budget.getAppId()));

				stmt.addBatch();
			}
			// execution
			stmt.executeBatch();

		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour les budgets", e);
		}

	}

	/**
	 * Récupère les comptes
	 * 
	 * @return couple clé : identifiant et valeurs
	 *         [nom,objectif,utilise,priority,is_actif]
	 * @throws ComptaException Echec de la récupération
	 */
	public HashMap<String, Budget> getAllBudget() throws ComptaException {

		HashMap<String, Budget> res = new HashMap<>();

		String query = "SELECT ID,nom,objectif,utilise,priority,is_actif,label_recurrent,date_recurrent FROM BUDGET;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {
				// parsing du résultat
				Budget elt = new Budget();

				elt.setNom(queryRes.getString("nom"));
				elt.setObjectif(queryRes.getDouble("objectif"));
				elt.setMontantUtilise(queryRes.getDouble("utilise"));
				elt.setPriority(queryRes.getInt("priority"));
				elt.setIsActif(queryRes.getBoolean("is_actif"));
				elt.setLabelRecurrent(queryRes.getString("label_recurrent"));
				elt.setDateRecurrent(queryRes.getDate("date_recurrent").toLocalDate());

				res.put(queryRes.getString("ID"), elt);
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer tous les comptes de la base", e);
		}

		return res;
	}

	/**
	 * Ajoute une utilisation en base de donnée
	 * 
	 * @param budId  l'id du budget utilisé
	 * @param nom    le nom de l'utilisation
	 * @param montat le montant de l'utilisation
	 * @param date   la date
	 * @throws ComptaException
	 */
	public String addUtilisationForBudget(String budId, String nom, double montat, LocalDate date)
			throws ComptaException {
		String id = "";

		// préparation de la requête
		String query = "INSERT INTO UTILISATION (nom,montant,date_util,budget_id) VALUES (?,?,?,?);";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, nom);
			stmt.setDouble(2, montat);
			stmt.setDate(3, Date.valueOf(date));
			stmt.setInt(4, Integer.parseInt(budId));
			// execution
			executeUpdate(stmt);

			// récupération de l'id en base du compte créé
			ResultSet res = stmt.getGeneratedKeys();
			if (res.getMetaData().getColumnCount() == 1 && res.next()) {
				id = res.getString(1).trim();
			}
		} catch (Exception e) {
			throw new ComptaException("Impossible d'ajouter l'utilisation", e);
		}

		return id;
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
	 * Retourne les champs en base des utilisations du budget
	 * 
	 * @param id l'id du budget
	 * @return
	 * @throws ComptaException Echec de la recupération
	 */
	public HashMap<String, Utilisation> getUtilisationInfos(String id) throws ComptaException {

		HashMap<String, Utilisation> res = new HashMap<>();

		// création de la requete
		String query = "SELECT ID,nom,montant,date_util FROM UTILISATION WHERE budget_id=?";
		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			stmt.setString(1, id);

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {
				// parsing du résultat
				res.put(queryRes.getString("ID"), parseUtilisationFromRes(queryRes));
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer les utilisations du budget", e);
		}

		return res;
	}

	/**
	 * Instancie une Utilisation depuis la persistance
	 * 
	 * @param queryRes
	 * @return
	 * @throws SQLException
	 */
	private Utilisation parseUtilisationFromRes(ResultSet queryRes) throws SQLException {

		return new Utilisation(queryRes.getDouble("montant"), queryRes.getString("nom"),
				queryRes.getDate("date_util").toLocalDate());
	}

	/**
	 * Mets à jour l'utilisation en base de donnée
	 * 
	 * @param utilisation
	 * @throws ComptaException Echec de la mise à jour
	 */
	public void upDateUtilisation(AppUtilisation utilisation) throws ComptaException {
		// préparation de la requête
		String query = "UPDATE UTILISATION SET nom=?,montant=?,date_util=? WHERE ID = ?";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			stmt.setString(1, utilisation.getNom());
			stmt.setDouble(2, utilisation.getMontant());
			stmt.setDate(3, Date.valueOf(utilisation.getDate()));
			stmt.setString(4, utilisation.getAppId());

			// execution
			executeUpdate(stmt);

		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour l'utilisation", e);
		}

	}

	/**
	 * Supprime l'utilisation en base
	 * 
	 * @param util
	 * @throws ComptaException
	 */
	public void removeUtilisation(AppUtilisation util) throws ComptaException {

		String query = "DELETE FROM UTILISATION WHERE ID=?;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			stmt.setString(1, util.getAppId());

			stmt.executeUpdate();
		} catch (Exception e) {
			throw new ComptaException("Impossible de supprimer l'utilisation", e);
		}

	}

	/**
	 * Supprime un budget de la base de donnée
	 * 
	 * @param appB
	 * @throws ComptaException
	 */
	public void removeBudget(AppBudget appB) throws ComptaException {
		// suppression des utilisations
		String query = "DELETE FROM UTILISATION WHERE budget_id=?;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {
			// suppression des utilisations
			stmt.setString(1, appB.getAppId());
			executeUpdate(stmt);

			// suppression du budget
			String query2 = "DELETE FROM BUDGET WHERE ID=?;";
			PreparedStatement stmt2 = getConnexion().prepareStatement(query2);

			stmt2.setString(1, appB.getAppId());
			executeUpdate(stmt2);

		} catch (Exception e) {
			throw new ComptaException("Impossible de supprimer les utilisations", e);
		}

	}

	/**
	 * Retourne une liste des années des exercices
	 * 
	 * @return la liste
	 * @throws ComptaException la récupération a échouée
	 */
	public List<String> getAllAnnees() throws ComptaException {

		ArrayList<String> res = new ArrayList<>();

		String query = "SELECT DISTINCT YEAR(date_debut) as y FROM EXERCICE_MENSUEL;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {

				res.add(queryRes.getString("y"));

			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer toutes les années des exercices");
		}

		return res;

	}

	/**
	 * Retourne la liste des montants des opérations pour le mois passé en paramètre
	 * 
	 * @param date
	 * @return
	 * @throws ComptaException
	 */
	public List<Double> getOperationForMonth(String type, LocalDate date) throws ComptaException {

		ArrayList<Double> res = new ArrayList<>();

		String query = "SELECT O.montant as mont,O.mois_id FROM OPERATION O INNER JOIN EXERCICE_MENSUEL E ON O.mois_id=E.ID WHERE E.date_debut=? AND O.type_ope=?;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			stmt.setDate(1, Date.valueOf(date));
			stmt.setString(2, type);

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {

				res.add(queryRes.getDouble("mont"));

			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer tous les montant des dépenses");
		}

		return res;
	}

	public List<Double> getBudgetUsageForMonth(LocalDate date) throws ComptaException {
		ArrayList<Double> res = new ArrayList<>();

		String query = "SELECT montant FROM UTILISATION WHERE date_util>=? AND date_util<?;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			// on démarre au debut du mois
			LocalDate deb = date.with(ChronoField.DAY_OF_MONTH, 1);
			stmt.setDate(1, Date.valueOf(deb));

			// on termine a la fin
			stmt.setDate(2, Date.valueOf(deb.plusMonths(1)));

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {

				res.add(queryRes.getDouble("montant"));

			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer tous les montant des utilisations", e);
		}

		return res;
	}

	/**
	 * Récupère l'identifiant d'un excercice mensuel
	 * 
	 * @param idTrimestre
	 * @param numMois
	 * @return
	 * @throws ComptaException
	 */
	public int getExerciceMensuelId(String idTrimestre, int numMois) throws ComptaException {

		String field;
		int res = -1;

		switch (numMois) {
		case 0:
			field = "premier_mois_id";
			break;
		case 1:
			field = "deux_mois_id";
			break;
		case 2:
			field = "trois_mois_id";
			break;
		default:
			field = "premier_mois_id";
		}

		String query = "SELECT " + field + " FROM TRIMESTRE WHERE ID=? ;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			stmt.setString(1, idTrimestre);

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {

				res = queryRes.getInt(field);

			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer l'excercice mensuel", e);
		}

		return res;
	}

	/**
	 * Change l'exercice mensuel d'une opération
	 * 
	 * @param OpId
	 * @param appEMId
	 * @throws ComptaException
	 */
	public void moveOperation(String OpId, String appEMId) throws ComptaException {

		String query = "UPDATE OPERATION SET mois_id=? WHERE ID=? ;";

		try (PreparedStatement stmt = getConnexion().prepareStatement(query)) {

			stmt.setInt(1, Integer.parseInt(appEMId));
			stmt.setInt(2, Integer.parseInt(OpId));

			// execution
			executeUpdate(stmt);

		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour l'opération", e);
		}

	}

	/**
	 * Centralisation des updates BD
	 * 
	 * @param stmt
	 * @throws SQLException
	 */
	public void executeUpdate(PreparedStatement stmt) throws SQLException {

		logger.debug(stmt.toString());
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

		logger.debug(stmt.toString());
		return stmt.executeQuery();

	}

	/**
	 * Ferme la base de donnée
	 */
	public void closeDataBase() {

		try (PreparedStatement pst = _connexionDB.prepareStatement("SHUTDOWN")) {
			pst.execute();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Retourne la liste des labels des budgets récurrent
	 * 
	 * @return
	 * @throws ComptaException Problème en base
	 */
	public List<String> getLabelRecurrentList() throws ComptaException {

		ArrayList<String> res = new ArrayList<>();

		String query = "SELECT label FROM LABEL_BUDGET_RECURRENT";

		try (PreparedStatement stmt = _connexionDB.prepareStatement(query)) {

			ResultSet resSet = executeQuery(stmt);

			while (resSet.next()) {

				res.add(resSet.getString("label"));

			}

		} catch (SQLException e) {
			throw new ComptaException("Impossible de récupérer les labels des budgets récurrents", e);
		}

		return res;
	}

	/**
	 * Ajoute un label de budget recurrent dans la base
	 * 
	 * @param labelRec
	 * @throws ComptaException
	 */
	public void addLabelRecurrent(String labelRec) throws ComptaException {

		String query = "INSERT INTO LABEL_BUDGET_RECURRENT (label) VALUES (?) ;";
		try (PreparedStatement stmt = _connexionDB.prepareStatement(query)) {

			stmt.setString(1, labelRec);
			executeUpdate(stmt);

		} catch (SQLException e) {
			throw new ComptaException("Impossible d'ajouter le label des budgets récurrents", e);
		}

	}

}
