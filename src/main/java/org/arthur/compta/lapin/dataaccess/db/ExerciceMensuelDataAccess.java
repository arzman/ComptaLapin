package org.arthur.compta.lapin.dataaccess.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.model.ExerciceMensuel;

public class ExerciceMensuelDataAccess extends ComptaDataAccess {

	public ExerciceMensuelDataAccess() {
		super();
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
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query,
				Statement.RETURN_GENERATED_KEYS)) {
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
	 * Retourne une liste des années des exercices
	 * 
	 * @return la liste
	 * @throws ComptaException la récupération a échouée
	 */
	public List<String> getAllAnnees() throws ComptaException {

		ArrayList<String> res = new ArrayList<>();

		String query = "SELECT DISTINCT YEAR(date_debut) as y FROM EXERCICE_MENSUEL;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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
		PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query);
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

		return new ExerciceMensuel(queryRes.getInt("id"), queryRes.getDate("date_debut").toLocalDate(),
				queryRes.getDate("date_fin").toLocalDate(), queryRes.getDouble("resultat_moyen_prevu"));
	}

	/**
	 * Supprime un exercice mensuel de la base. Les opérations sont également
	 * supprimées.
	 * 
	 * @param idMois l'id de l'exercice mensuel à supprimer
	 * @throws ComptaException Echec de la suppression
	 */
	public void removeExcerciceMensuel(String idMois) throws ComptaException {

		// suppression des opérations de l'exercice
		String queryOp = "DELETE FROM OPERATION WHERE mois_id=? ;";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(queryOp)) {
			stmt.setInt(1, Integer.parseInt(idMois));
			executeUpdate(stmt);

			// suppression de l'exercie
			String query = "DELETE FROM EXERCICE_MENSUEL WHERE ID=? ;";
			try (PreparedStatement stmt2 = DBManager.getInstance().getConnexion().prepareStatement(query)) {
				stmt2.setInt(1, Integer.parseInt(idMois));
				executeUpdate(stmt2);
			} catch (Exception e) {
				throw new ComptaException("Impossible de supprimer l'exercice mensuel", e);
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de supprimer les opérations de l'exercice", e);
		}

	}

}
