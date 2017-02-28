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
		
		//TODO A lier avec la persistance
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
