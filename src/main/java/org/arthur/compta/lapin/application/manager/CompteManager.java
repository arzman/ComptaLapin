package org.arthur.compta.lapin.application.manager;

import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.model.Compte;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Singleton de gestion des {@link AppCompte}
 *
 */
public class CompteManager {
	
	
	
	private static CompteManager _instance;
	
	private ObservableList<AppCompte> _compteList;
	
	
	
	private CompteManager() {
		
		_compteList = FXCollections.observableArrayList();
		
		Compte compte  = new Compte("Livret");
		compte.setBudgetAllowed(true);
		compte.setSolde(2568.2);
		
		AppCompte appC = new AppCompte(compte);
		_compteList.add(appC);
	}
	
	/**
	 * Retourne l'instance unique du singleton
	 * @return l'instance du singleton
	 */
	public static CompteManager getInstance(){
		
		if(_instance==null){
			_instance = new CompteManager();
		}
		
		return _instance;
	}
	
	/**
	 * Retourne la liste des comptes applicatif
	 * @return la liste des comptes applicatif
	 */
	public ObservableList<AppCompte> getCompteList(){
		return _compteList;
	}
	

}
