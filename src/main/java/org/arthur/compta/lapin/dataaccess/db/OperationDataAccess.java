package org.arthur.compta.lapin.dataaccess.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.OperationSearchResult;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;

public class OperationDataAccess extends ComptaDataAccess {

	/** instance du singleton */
	private static OperationDataAccess _instance;

	public static OperationDataAccess getInstance() {

		if (_instance == null) {
			_instance = new OperationDataAccess();
		}

		return _instance;
	}

	public OperationDataAccess() {
		super();
	}

	/**
	 * Ajoute une opération en base de donnée
	 * 
	 * @param dep
	 *            l'operation
	 * @param compteSrcId
	 *            l'id du compte
	 * @param appEmId
	 *            l'id de l'exercice mensuel
	 * @return l'id de la depense
	 * @throws ComptaException
	 */
	public Operation addOperation(String nom, double montant, OperationType type, EtatOperation etat, int compteSrcId, int compteCibleId, int appEmId)
			throws ComptaException {

		int id = -1;
		String query = "INSERT INTO OPERATION (nom,montant,type_ope,etat,compte_source_id,compte_cible_id,mois_id) VALUES (?,?,?,?,?,?,?);";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, nom);
			stmt.setDouble(2, montant);
			stmt.setString(3, type.toString());
			stmt.setString(4, etat.toString());
			stmt.setInt(5, compteSrcId);
			if (compteCibleId != -1) {
				stmt.setInt(6, compteCibleId);
			} else {
				stmt.setNull(6, Types.INTEGER);
			}
			stmt.setInt(7, appEmId);

			executeUpdate(stmt);

			// récupération de l'id en base du compte créé
			ResultSet res = stmt.getGeneratedKeys();
			if (res.getMetaData().getColumnCount() == 1 && res.next()) {
				id = res.getInt(1);
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible d'insérer l'opération", e);
		}

		return new Operation(id, type, compteSrcId, nom, montant, etat, compteCibleId);
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
	 * Retourne la liste des montants des opérations pour le mois passé en
	 * paramètre
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
	public List<Operation> getOperationInfo(int exId) throws ComptaException {

		ArrayList<Operation> map = new ArrayList<>();

		// création de la requete
		String query = "SELECT ID,nom,montant,type_ope,etat,compte_source_id,compte_cible_id FROM OPERATION WHERE mois_id=?;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {
			// positionnement du parametre
			stmt.setInt(1, exId);

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {
				map.add(parseOperationFromRes(queryRes));
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer les opérations", e);
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
	public void moveOperation(int OpId, int appEMId) throws ComptaException {

		String query = "UPDATE OPERATION SET mois_id=? WHERE ID=? ;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			stmt.setInt(1, appEMId);
			stmt.setInt(2, OpId);

			// execution
			executeUpdate(stmt);

		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour l'opération", e);
		}

	}

	/**
	 * 
	 * @param queryRes
	 * @return
	 * @throws SQLException
	 */
	private Operation parseOperationFromRes(ResultSet queryRes) throws SQLException {

		return new Operation(queryRes.getInt("id"), OperationType.valueOf(queryRes.getString("type_ope")), queryRes.getInt("compte_source_id"),
				queryRes.getString("nom"), queryRes.getDouble("montant"), EtatOperation.valueOf(queryRes.getString("etat")),
				queryRes.getInt("compte_cible_id"));
	}

	/**
	 * Instancie une Opération depuis la persistance
	 * 
	 * @param queryRes
	 * @return
	 * @throws SQLException
	 */
	private OperationSearchResult parseOperationSearchResultFromRes(ResultSet queryRes) throws SQLException {

		return new OperationSearchResult(queryRes.getString("nom"), queryRes.getDouble("montant"), queryRes.getDate("date_debut").toLocalDate());
	}

	/**
	 * Supprime l'opération de la base
	 * 
	 * @param appOp
	 * @throws ComptaException
	 *             Echec de la suppression
	 */
	public void removeOperation(int id) throws ComptaException {

		String query = "DELETE FROM OPERATION WHERE ID = ?";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			stmt.setInt(1, id);
			executeUpdate(stmt);

		} catch (Exception e) {
			throw new ComptaException("Impossible de supprimer l'opération", e);
		}

	}

	/**
	 * Effectue une recherche d'opération en base de donnée
	 * 
	 * @param lib
	 *            le libelle a trouver
	 * @param montant
	 *            le montant
	 * @param tolerance
	 *            la tolérance sur le montant
	 * @return
	 * @throws ComptaException
	 */
	public List<OperationSearchResult> searchOperation(String lib, String montant, String tolerance) throws ComptaException {

		ArrayList<OperationSearchResult> res = new ArrayList<>();

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

				res.add(parseOperationSearchResultFromRes(queryRes));
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
	public void updateOperation(Operation appOp) throws ComptaException {

		// création de la requete
		String query = "UPDATE OPERATION SET nom=?,montant=?,type_ope=?,etat=?,compte_source_id=?,compte_cible_id=? WHERE ID=?;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query);) {

			stmt.setString(1, appOp.getNom());
			stmt.setDouble(2, appOp.getMontant());
			stmt.setString(3, appOp.getType().toString());
			stmt.setString(4, appOp.getEtat().toString());
			stmt.setInt(5, appOp.getCompteId());

			if (appOp.getType().equals(OperationType.TRANSFERT)) {
				stmt.setInt(6, appOp.getCibleCompteId());
			} else {
				stmt.setNull(6, Types.INTEGER);
			}

			stmt.setInt(7, appOp.getId());

			// execution
			executeUpdate(stmt);
		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre l'opération à jour", e);
		}

	}

}
