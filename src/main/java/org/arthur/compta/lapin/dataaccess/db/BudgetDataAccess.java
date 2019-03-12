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
import org.arthur.compta.lapin.application.model.AppBudget;
import org.arthur.compta.lapin.model.Budget;

public class BudgetDataAccess extends ComptaDataAccess {

	public BudgetDataAccess() {
		super();
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
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query,
				Statement.RETURN_GENERATED_KEYS)) {
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
	 * Ajoute un label de budget recurrent dans la base
	 * 
	 * @param labelRec
	 * @throws ComptaException
	 */
	public void addLabelRecurrent(String labelRec) throws ComptaException {

		String query = "INSERT INTO LABEL_BUDGET_RECURRENT (label) VALUES (?) ;";
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			stmt.setString(1, labelRec);
			executeUpdate(stmt);

		} catch (SQLException e) {
			throw new ComptaException("Impossible d'ajouter le label des budgets récurrents", e);
		}

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

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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
	 * Récupère les budget
	 * 
	 * @return couple clé : identifiant et valeurs
	 *         [nom,objectif,utilise,priority,is_actif]
	 * @throws ComptaException Echec de la récupération
	 */
	public List<Budget> getAllBudget() throws ComptaException {

		ArrayList<Budget> res = new ArrayList<>();
		String query = "SELECT ID,nom,objectif,utilise,priority,is_actif,label_recurrent,date_recurrent FROM BUDGET;";

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

			ResultSet queryRes = executeQuery(stmt);

			while (queryRes.next()) {

				// parse et ajout
				res.add(parseBudgetFromRes(queryRes));
			}

		} catch (Exception e) {
			throw new ComptaException("Impossible de récupérer tous les comptes de la base", e);
		}

		return res;
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

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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
	 * 
	 * 
	 * @param queryRes
	 * @return
	 * @throws SQLException
	 */
	private Budget parseBudgetFromRes(ResultSet queryRes) throws SQLException {

		// parsing du résultat

		return new Budget(queryRes.getInt("id"), queryRes.getDouble("objectif"), queryRes.getDouble("utilise"),
				queryRes.getString("nom"), queryRes.getBoolean("is_actif"), queryRes.getInt("priority"),
				queryRes.getString("label_recurrent"), queryRes.getDate("date_recurrent").toLocalDate());
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

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {
			// suppression des utilisations
			stmt.setString(1, appB.getAppId());
			executeUpdate(stmt);

			// suppression du budget
			String query2 = "DELETE FROM BUDGET WHERE ID=?;";
			PreparedStatement stmt2 = DBManager.getInstance().getConnexion().prepareStatement(query2);

			stmt2.setString(1, appB.getAppId());
			executeUpdate(stmt2);

		} catch (Exception e) {
			throw new ComptaException("Impossible de supprimer les utilisations", e);
		}

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
		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {
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

		try (PreparedStatement stmt = DBManager.getInstance().getConnexion().prepareStatement(query)) {

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

}
