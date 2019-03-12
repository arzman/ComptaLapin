package org.arthur.compta.lapin.dataaccess.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.OperationSearchResult;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;

public class OperationDataAccess extends ComptaDataAccess {

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
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query,
				PreparedStatement.RETURN_GENERATED_KEYS)) {

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

	public List<Double> getBudgetUsageForMonth(LocalDate date) throws ComptaException {
		ArrayList<Double> res = new ArrayList<>();

		String query = "SELECT montant FROM UTILISATION WHERE date_util>=? AND date_util<?;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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
	 * Retourne la liste des montants des opérations pour le mois passé en paramètre
	 * 
	 * @param date
	 * @return
	 * @throws ComptaException
	 */
	public List<Double> getOperationForMonth(String type, LocalDate date) throws ComptaException {

		ArrayList<Double> res = new ArrayList<>();

		String query = "SELECT O.montant as mont,O.mois_id FROM OPERATION O INNER JOIN EXERCICE_MENSUEL E ON O.mois_id=E.ID WHERE E.date_debut=? AND O.type_ope=?;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {
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

	/**
	 * Change l'exercice mensuel d'une opération
	 * 
	 * @param OpId
	 * @param appEMId
	 * @throws ComptaException
	 */
	public void moveOperation(String OpId, String appEMId) throws ComptaException {

		String query = "UPDATE OPERATION SET mois_id=? WHERE ID=? ;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			stmt.setInt(1, Integer.parseInt(appEMId));
			stmt.setInt(2, Integer.parseInt(OpId));

			// execution
			executeUpdate(stmt);

		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour l'opération", e);
		}

	}

	private Operation parseOperationFromRes(ResultSet queryRes) throws SQLException {

		return new Operation(queryRes.getInt("id"), OperationType.valueOf(queryRes.getString("type_ope")),
				queryRes.getInt("compte_source_id"), queryRes.getString("nom"), queryRes.getDouble("montant"),
				EtatOperation.valueOf(queryRes.getString("etat")), queryRes.getInt("compte_cible_id"));
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
	 * Supprime l'opération de la base
	 * 
	 * @param appOp
	 * @throws ComptaException Echec de la suppression
	 */
	public void removeOperation(AppOperation appOp) throws ComptaException {

		String query = "DELETE FROM OPERATION WHERE ID = ?";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			stmt.setInt(1, Integer.parseInt(appOp.getAppId()));
			executeUpdate(stmt);

		} catch (Exception e) {
			throw new ComptaException("Impossible de supprimer l'opération", e);
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
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query);) {

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

}
