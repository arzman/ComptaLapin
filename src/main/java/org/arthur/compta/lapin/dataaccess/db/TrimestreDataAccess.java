package org.arthur.compta.lapin.dataaccess.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;

public class TrimestreDataAccess extends ComptaDataAccess {

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
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query,
				Statement.RETURN_GENERATED_KEYS)) {
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
	 * AAjoute les éléments de template aau modèle de trimestre en base de donnée
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
	 * Vide le modèle de trimestre
	 * 
	 * @throws ComptaException Echec de la suppression
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
	 * @throws ComptaException Echec de la récupération
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
	 * @param id l'id du trimestre
	 * @return
	 * @throws ComptaException Echec de la récupération
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
	 * @throws SQLException Echec de la récupération
	 */
	public String[] getTrimestreInfo(String appId) throws SQLException {

		String[] res = new String[4];

		// création de requete
		String query = "SELECT ID,premier_mois_id,deux_mois_id,trois_mois_id FROM TRIMESTRE WHERE ID=?";
		PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query);
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
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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
	 * Supprime un trimestre de la base de donnée
	 * 
	 * @param idTrimestrel'id du trimestre
	 * @throws SQLException Echec de la suppression
	 */
	public void removeTrimestre(String idTrimestre) throws ComptaException {

		// suppression des excercices mensuels
		String queryEM = "SELECT premier_mois_id,deux_mois_id,trois_mois_id FROM TRIMESTRE WHERE ID=?;";
		try (PreparedStatement stmtEM = DBManager.getInstance().getConnexion().prepareStatement(queryEM)) {

			stmtEM.setInt(1, Integer.valueOf(idTrimestre));
			ResultSet queryRes = stmtEM.executeQuery();

			// suppression du trimestre
			String query = "DELETE FROM TRIMESTRE WHERE ID=? ;";
			try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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

}
