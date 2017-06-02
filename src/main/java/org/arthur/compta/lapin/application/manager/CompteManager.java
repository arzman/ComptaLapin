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

		// liste observable de compte, le contenu est observé également
		_compteList = FXCollections.observableArrayList();

		try {
			// récupération en base des métadonnée des comptes
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
				
				//ajout des prévisions
				double delta1 = TrimestreManager.getInstance().getDeltaForCompte(id,0);
				appC.soldePrev1Property().set(compte.getSolde() + delta1);
				
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

			// vérification naive du retour
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

	/**
	 * Suppression d'un compte
	 * 
	 * @param appCompte
	 *            le compte a supprimer
	 * @throws ComptaException
	 *             La suppression a échouer
	 */
	public void removeCompte(AppCompte appCompte) throws ComptaException {

		if (appCompte != null) {

			try {
				// suppression en base
				DBManager.getInstance().removeCompte(appCompte.getAppId());
				// suppression de l'appli
				_compteList.remove(appCompte);
			} catch (Exception e) {
				throw new ComptaException("Impossible de supprimer le compte", e);
			}

		}

	}

	/**
	 * Met à jour le compte
	 * 
	 * @param appCompte
	 *            le compte application associé
	 * @param nom
	 *            le nouveau nom
	 * @param solde
	 *            le nouveau solde
	 * @param isLivret
	 *            le nouveau flag isLivret
	 * @param isBudget
	 *            le nouveau floag isBudget
	 * @return le compte applicatif mis à jour
	 * @throws ComptaException
	 *             La mise à jour a échouée
	 */
	public AppCompte updateCompte(AppCompte appCompte, String nom, double solde, boolean isLivret, boolean isBudget)
			throws ComptaException {

		if (appCompte != null) {

			try {
				DBManager.getInstance().updateCompte(appCompte.getAppId(), nom, solde, isLivret, isBudget);

				appCompte.setNom(nom);
				appCompte.setSolde(solde);
				appCompte.setIsLivret(isLivret);
				appCompte.setIsBudget(isBudget);

			} catch (Exception e) {
				throw new ComptaException("Impossible de mettre à jour le compte", e);
			}
		}

		return appCompte;
	}

	/**
	 * Retourne le compte correspondant à l'id
	 * 
	 * @param id
	 * @return
	 */
	public AppCompte getCompte(String id) {
		
		AppCompte res= null;
		
		boolean stop = false;
		for(int i=0;i<_compteList.size() && !stop;i++){
			
			if(_compteList.get(i).getAppId().equals(id)){
				res = _compteList.get(i);
				stop = true;
			}
			
		}
		
		
		return res;
	}

}
