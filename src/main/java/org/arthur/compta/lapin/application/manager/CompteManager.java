package org.arthur.compta.lapin.application.manager;

import java.sql.SQLException;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.model.Compte;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Singleton de gestion des AppCompte
 *
 */
public class CompteManager {

	/**
	 * Instance unique du singleton
	 */
	private static CompteManager _instance;

	/**
	 * La liste des comptes géré par l'application
	 */
	private ObservableList<AppCompte> _compteList;

	/**
	 * Constructeur
	 */
	private CompteManager() {

		_compteList = FXCollections.observableArrayList();

		try {
			//récupération en base des métadonnée des comptes
			HashMap<String, String[]> fromPersistancy = DBManager.getInstance().getAllCompte();

			for (String id : fromPersistancy.keySet()) {

				// création du modèle
				Compte compte = new Compte(fromPersistancy.get(id)[0]);
				compte.setSolde(Double.parseDouble(fromPersistancy.get(id)[1]));
				compte.setLivret(Boolean.parseBoolean(fromPersistancy.get(id)[2]));
				compte.setBudgetAllowed(Boolean.parseBoolean(fromPersistancy.get(id)[3]));
				// encapsulation applicative
				AppCompte appC = new AppCompte(compte);
				appC.setAppID(id);
				// ajout dans l'application
				_compteList.add(appC);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Retourne l'instance unique du singleton
	 * 
	 * @return l'instance du singleton
	 */
	public static CompteManager getInstance() {

		if (_instance == null) {
			_instance = new CompteManager();
		}

		return _instance;
	}

	/**
	 * Retourne la liste des comptes applicatif
	 * 
	 * @return la liste des comptes applicatif
	 */
	public ObservableList<AppCompte> getCompteList() {
		return _compteList;
	}

	/**
	 * Création d'un nouveau compte
	 * 
	 * @param nom
	 *            le nom du compte
	 * @param solde
	 *            le solde du compte
	 * @param livret
	 *            est livret ?
	 * @param budgetAllowed
	 *            budget autorisé ?
	 * @return le compte application créé
	 * @throws ComptaException
	 *             Exception si la création a échoué
	 */
	public AppCompte createCompte(String nom, double solde, boolean livret, boolean budgetAllowed)
			throws ComptaException {

		AppCompte appC = null;

		try {
			// ajout en base
			String id = DBManager.getInstance().addCompte(nom, solde, livret, budgetAllowed);

			//vérification naive du retour
			if (id.trim().isEmpty()) {
				throw new ComptaException("Impossible de créer le compte : id applicatif vide");
			}

			// création du modèle
			Compte compte = new Compte(nom);
			compte.setSolde(solde);
			compte.setLivret(livret);
			compte.setBudgetAllowed(budgetAllowed);
			// encapsulation applicative
			appC = new AppCompte(compte);
			appC.setAppID(id);
			// ajout dans l'application
			_compteList.add(appC);
		} catch (Exception e) {
			throw new ComptaException("Impossible de créer le compte", e);
		}

		return appC;
	}

}
