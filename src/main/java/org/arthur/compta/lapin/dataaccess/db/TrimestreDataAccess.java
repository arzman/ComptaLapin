package org.arthur.compta.lapin.dataaccess.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplate;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;
import org.arthur.compta.lapin.model.Trimestre;

public class TrimestreDataAccess extends ComptaDataAccess {

	/** instance du singleton */
	private static TrimestreDataAccess _instance;

	public static TrimestreDataAccess getInstance() {

		if (_instance == null) {
			_instance = new TrimestreDataAccess();
		}

		return _instance;
	}

	private TrimestreDataAccess() {

		super();
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
	public Trimestre addTrimestre(int idMois1, int idMois2, int idMois3) throws ComptaException {

		int id = -1;
		// préparation de la requête
		String query = "INSERT INTO TRIMESTRE (premier_mois_id,deux_mois_id,trois_mois_id) VALUES (?,?,?);";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, idMois1);
			stmt.setInt(2, idMois2);
			stmt.setInt(3, idMois3);

			// execution
			executeUpdate(stmt);

			// récupération de l'id en base du compte créé
			ResultSet res = stmt.getGeneratedKeys();
			if (res.getMetaData().getColumnCount() == 1 && res.next()) {
				id = res.getInt(1);
			}
		} catch (Exception e) {
			throw new ComptaException("Impossible d'ajouter un trimestre", e);
		}

		return new Trimestre(id, idMois1, idMois2, idMois3);
	}

	/**
	 * AAjoute les éléments de template aau modèle de trimestre en base de
	 * donnée
	 * 
	 * @param elements
	 * @throws ComptaException
	 */
	public void addTrimstreTempElts(List<TrimestreTemplateElement> elements) throws ComptaException {

		String query = "INSERT INTO TEMPLATE (nom,montant,type_ope,frequence,occurence,compte_source_id,compte_cible_id) VALUES (?,?,?,?,?,?,?);";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			for (TrimestreTemplateElement elt : elements) {

				stmt.setString(1, elt.getNom());
				stmt.setDouble(2, elt.getMontant());
				stmt.setString(3, elt.getType());
				stmt.setString(4, elt.getFreq().toString());
				stmt.setInt(5, elt.getOccurence());
				stmt.setInt(6, elt.getCompteSource().getAppId());
				if (elt.getCompteCible() != null) {
					stmt.setInt(7, elt.getCompteCible().getAppId());
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
	 * Vide le modèle de trimestre
	 * 
	 * @throws ComptaException
	 *             Echec de la suppression
	 */
	public void clearTrimTemplate() throws ComptaException {

		String query = "DELETE FROM TEMPLATE;";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {
			executeUpdate(stmt);
		} catch (Exception e) {
			throw new ComptaException("Impossible de vider les templates", e);
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
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query);) {
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
	 * @param id
	 *            l'id du trimestre
	 * @return
	 * @throws ComptaException
	 *             Echec de la récupération
	 */
	public LocalDate getDateDebutFromTrimestre(String id) throws ComptaException {

		LocalDate res = null;
		// récupération de la date de début du premier exercice mensuel
		String query = "SELECT date_debut FROM EXERCICE_MENSUEL E INNER JOIN TRIMESTRE T ON E.ID=T.premier_mois_id WHERE T.ID=? ;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query);) {

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
	 * Récupère en base les champ d'un trimestre [ id trimestre, id 1er mois, id
	 * 2eme mois, id 3 eme mois]
	 * 
	 * @return les champ du compte courant
	 * @throws ComptaException
	 *             Echec de la récupération
	 * 
	 */
	public Trimestre getTrimestreInfo(int appId) throws ComptaException {

		Trimestre res = null;

		try {

			// création de requete
			String query = "SELECT ID,premier_mois_id,deux_mois_id,trois_mois_id FROM TRIMESTRE WHERE ID=?";
			PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query);
			stmt.setInt(1, appId);
			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {

				res = parseTrimestreFromRes(queryRes);

			}

		} catch (SQLException e) {
			throw new ComptaException("Impossible de charger le trimestre", e);
		}

		if (res == null) {
			throw new ComptaException("Impossible de charger le trimestre, inexistant " + appId);
		}

		return res;
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
	public TrimestreTemplate loadTemplateInfo() throws ComptaException {

		TrimestreTemplate infos = new TrimestreTemplate();

		String query = "SELECT ID,nom,montant,type_ope,frequence,occurence,compte_source_id,compte_cible_id FROM TEMPLATE;";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {
				// parsing du résultat
				infos.addElement(parseTrimestreTemplateElement(queryRes));
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de recupérer le template", e);
		}

		return infos;
	}

	/**
	 * 
	 * @param queryRes
	 * @return
	 * @throws SQLException
	 */
	private Trimestre parseTrimestreFromRes(ResultSet queryRes) throws SQLException {

		return new Trimestre(queryRes.getInt("id"), queryRes.getInt("premier_mois_id"), queryRes.getInt("deux_mois_id"), queryRes.getInt("trois_mois_id"));
	}

	private TrimestreTemplateElement parseTrimestreTemplateElement(ResultSet queryRes) throws SQLException {

		TrimestreTemplateElement elt = new TrimestreTemplateElement();
		elt.setNom(queryRes.getString("nom"));
		elt.setMontant(queryRes.getDouble("montant"));
		elt.setType(queryRes.getString("type_ope"));
		elt.setFreq(TrimestreTemplateElementFrequence.valueOf(queryRes.getString("frequence")));
		elt.setOccurence(queryRes.getInt("occurence"));
		elt.setCompteSource(CompteManager.getInstance().getAppCompteFromId(queryRes.getInt("compte_source_id")));
		elt.setCompteCible(CompteManager.getInstance().getAppCompteFromId(queryRes.getInt("compte_cible_id")));

		return elt;
	}

	/**
	 * Supprime un trimestre de la base de donnée, mais pas les exercice
	 * mensuels
	 * 
	 * @param idTrimestrel'id
	 *            du trimestre
	 * @throws SQLException
	 *             Echec de la suppression
	 */
	public void removeTrimestre(int idTrimestre) throws ComptaException {

		// suppression du trimestre
		String query = "DELETE FROM TRIMESTRE WHERE ID=? ;";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			stmt.setInt(1, idTrimestre);
			executeUpdate(stmt);

		} catch (SQLException e) {
			throw new ComptaException("Impossible de supprimer le trimestre", e);
		}

	}

}
