package org.arthur.compta.lapin.dataaccess.db;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppUtilisation;
import org.arthur.compta.lapin.model.Utilisation;

public class UtilisationDataAccess extends ComptaDataAccess {

	/** instance du singleton */
	private static UtilisationDataAccess _instance;

	public static UtilisationDataAccess getInstance() {

		if (_instance == null) {
			_instance = new UtilisationDataAccess();
		}

		return _instance;
	}

	public UtilisationDataAccess() {
		super();
	}

	/**
	 * Ajoute une utilisation en base de donnée
	 * 
	 * @param budId
	 *            l'id du budget utilisé
	 * @param nom
	 *            le nom de l'utilisation
	 * @param montat
	 *            le montant de l'utilisation
	 * @param date
	 *            la date
	 * @throws ComptaException
	 */
	public void addUtilisationForBudget(int budId, String nom, double montat, LocalDate date) throws ComptaException {

		// préparation de la requête
		String query = "INSERT INTO UTILISATION (nom,montant,date_util,budget_id) VALUES (?,?,?,?);";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, nom);
			stmt.setDouble(2, montat);
			stmt.setDate(3, Date.valueOf(date));
			stmt.setInt(4, budId);
			// execution
			executeUpdate(stmt);

		} catch (Exception e) {
			throw new ComptaException("Impossible d'ajouter l'utilisation", e);
		}

	}

	/**
	 * Retourne les champs en base des utilisations du budget
	 * 
	 * @param id
	 *            l'id du budget
	 * @return
	 * @throws ComptaException
	 *             Echec de la recupération
	 */
	public List<Utilisation> getUtilisationInfos(int id) throws ComptaException {

		ArrayList<Utilisation> res = new ArrayList<Utilisation>();

		// création de la requete
		String query = "SELECT ID,nom,montant,date_util FROM UTILISATION WHERE budget_id=?";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			stmt.setInt(1, id);

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {
				// parsing du résultat
				res.add(parseUtilisationFromRes(queryRes));
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

		return new Utilisation(queryRes.getInt("id"), queryRes.getDouble("montant"), queryRes.getString("nom"), queryRes.getDate("date_util").toLocalDate());
	}

	/**
	 * Supprime l'utilisation en base
	 * 
	 * @param util
	 * @throws ComptaException
	 */
	public void removeUtilisation(int utilId) throws ComptaException {

		String query = "DELETE FROM UTILISATION WHERE ID=?;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			stmt.setInt(1, utilId);
			executeUpdate(stmt);
		} catch (Exception e) {
			throw new ComptaException("Impossible de supprimer l'utilisation", e);
		}

	}

	/**
	 * Mets à jour l'utilisation en base de donnée
	 * 
	 * @param utilisation
	 * @throws ComptaException
	 *             Echec de la mise à jour
	 */
	public void upDateUtilisation(AppUtilisation utilisation) throws ComptaException {
		// préparation de la requête
		String query = "UPDATE UTILISATION SET nom=?,montant=?,date_util=? WHERE ID = ?";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			stmt.setString(1, utilisation.getNom());
			stmt.setDouble(2, utilisation.getMontant());
			stmt.setDate(3, Date.valueOf(utilisation.getDate()));
			stmt.setInt(4, utilisation.getAppId());

			// execution
			executeUpdate(stmt);

		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour l'utilisation", e);
		}

	}

}
