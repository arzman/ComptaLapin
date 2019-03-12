package org.arthur.compta.lapin.dataaccess.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.model.Compte;

public class CompteDataAcces extends ComptaDataAccess {

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
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query,
				Statement.RETURN_GENERATED_KEYS)) {
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

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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

		return new Compte(res.getInt("id"), res.getDouble("solde"), res.getString("nom"), res.getBoolean("is_livret"),
				res.getBoolean("budget_allowed"));
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
		PreparedStatement stmt = DBManager.getInstance().getConnexion()
				.prepareStatement("DELETE FROM COMPTE WHERE ID = ?");
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
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {
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

}
