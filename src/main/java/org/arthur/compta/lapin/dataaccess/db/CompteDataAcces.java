package org.arthur.compta.lapin.dataaccess.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.model.Compte;

public class CompteDataAcces extends ComptaDataAccess {

	/** instance du singleton */
	private static CompteDataAcces _instance;

	public static CompteDataAcces getInstance() {

		if (_instance == null) {
			_instance = new CompteDataAcces();
		}

		return _instance;
	}

	private CompteDataAcces() {
		super();
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
	public Compte addCompte(String nom, double solde, boolean livret, boolean budgetAllowed) throws ComptaException {

		Compte cpt;

		// préparation de la requête
		String query = "INSERT INTO COMPTE (nom,solde,is_livret,budget_allowed) VALUES (?,?,?,?);";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, nom);
			stmt.setDouble(2, solde);
			stmt.setBoolean(3, livret);
			stmt.setBoolean(4, budgetAllowed);
			// execution
			executeUpdate(stmt);

			// récupération de l'id en base du compte créé
			ResultSet res = stmt.getGeneratedKeys();
			res.next();

			cpt = new Compte(res.getInt(1), solde, nom, livret, budgetAllowed);

		} catch (Exception e) {
			throw new ComptaException("Impossible d'ajouter le compte", e);
		}

		return cpt;
	}

	/**
	 * Récupère toutes les informations comptes de la base de donnée
	 * 
	 * @return couple clé : identifiant et valeurs [nom,solde,livret,budget]
	 * @throws ComptaException
	 *             Exception sur la récupération en base
	 */
	public List<Compte> getAllCompte() throws ComptaException {

		ArrayList<Compte> cptList = new ArrayList<Compte>();

		// requête sur la table COMPTE
		String query = "SELECT ID,nom,solde,is_livret,budget_allowed FROM COMPTE;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			ResultSet res = executeQuery(stmt);

			while (res.next()) {

				cptList.add(parseCompteFromRes(res));

			}
		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer les comptes", e);
		}

		return cptList;
	}

	/**
	 * Instancie un Compte depuis la persistance
	 * 
	 * @param res
	 * @return
	 * @throws SQLException
	 */
	private Compte parseCompteFromRes(ResultSet res) throws SQLException {

		return new Compte(res.getInt("id"), res.getDouble("solde"), res.getString("nom"), res.getBoolean("is_livret"), res.getBoolean("budget_allowed"));
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
	public void removeCompte(int appId) throws ComptaException {

		// préparation de la requête de suppression
		PreparedStatement stmt;
		try {
			stmt = DBManager.getInstance().getConnexion().prepareStatement("DELETE FROM COMPTE WHERE ID = ?");
			stmt.setInt(1, appId);
			// execution
			executeUpdate(stmt);
		} catch (SQLException e) {
			throw new ComptaException("Impossible de supprimer le comtpe", e);
		}

	}

	/**
	 * Met à jour le compte en base
	 * 
	 * @param compte
	 *            :le compte a mettre a jour en base
	 *
	 * @throws ComptaException
	 *             Exception si la requête en base échoue
	 */
	public void updateCompte(Compte compte) throws ComptaException {

		// préparation de la requête
		String query = "UPDATE COMPTE SET nom=?,solde=?,is_livret=?,budget_allowed=? WHERE ID = ?";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {
			stmt.setString(1, compte.getNom());
			stmt.setDouble(2, compte.getSolde());
			stmt.setBoolean(3, compte.isLivret());
			stmt.setBoolean(4, compte.isBudgetAllowed());
			stmt.setInt(5, compte.getId());
			// execution
			executeUpdate(stmt);
		} catch (Exception e) {
			throw new ComptaException("Impossible de mettre à jour le compte", e);
		}

	}

}
