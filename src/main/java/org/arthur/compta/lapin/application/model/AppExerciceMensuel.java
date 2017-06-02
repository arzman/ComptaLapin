package org.arthur.compta.lapin.application.model;

import java.util.Calendar;

import org.arthur.compta.lapin.model.ExerciceMensuel;
import org.arthur.compta.lapin.model.operation.TransfertOperation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppExerciceMensuel extends AppObject {

	/**
	 * L'exercice encapsulé
	 */
	private ExerciceMensuel _exMensuel;

	/** La liste des dépenses */
	private ObservableList<AppOperation> _appDepenseList;
	/** La liste des ressources */
	private ObservableList<AppOperation> _appRessourceList;
	/** La liste des transferts */
	private ObservableList<AppTransfert> _appTransfertList;

	public AppExerciceMensuel(ExerciceMensuel exerciceMensuel) {

		_exMensuel = exerciceMensuel;

		_appDepenseList = FXCollections.observableArrayList();
		_appRessourceList = FXCollections.observableArrayList();
		_appTransfertList = FXCollections.observableArrayList();

	}

	/**
	 * Retourne la date de début de l'exercice
	 * 
	 * @return
	 */
	public Calendar getDateDebut() {
		return _exMensuel.getDateDebut();
	}

	/**
	 * Retourne l'exercice mensuel encapsulé
	 * 
	 * @return
	 */
	public ExerciceMensuel getExcerciceMensuel() {
		return _exMensuel;
	}

	public double getResultat() {

		double sum = 0;

		// res = ressource
		for (AppOperation res : _appRessourceList) {
			sum = sum + res.getMontant();
		}
		// - depenses
		for (AppOperation dep : _appDepenseList) {
			sum = sum - dep.getMontant();
		}
		return sum;
	}

	/**
	 * Ajoute une dépense dans l'exercice mensuel
	 * 
	 * @param dep
	 *            la dépense
	 * @param id
	 *            l'identifiant applicatif
	 */
	public void addDepense(AppOperation appDep) {



		_appDepenseList.add(appDep);
		_exMensuel.getDepensesList().add(appDep.getOperation());

	}

	/**
	 * Retourne la liste des dépenses
	 * 
	 * @return
	 */
	public ObservableList<AppOperation> getDepenses() {
		return _appDepenseList;
	}

	/**
	 * Ajoute une ressource dans l'exercice mensuel
	 * 
	 * @param res
	 *            la ressource
	 * @param id
	 *            l'identifiant applicatif
	 */
	public void addRessource(AppOperation appRes) {

		_appRessourceList.add(appRes);
		_exMensuel.getRessourcesList().add(appRes.getOperation());

	}

	/**
	 * Ajoute un transfert dans l'exercice mensuel
	 * 
	 * @param trans
	 *            le transfert
	 * @param id
	 *            l'identifiant applicatif
	 */
	public void addTransfert(TransfertOperation trans, String id) {

		AppTransfert apptr = new AppTransfert(trans);
		apptr.setAppID(id);

		_appTransfertList.add(apptr);
		_exMensuel.getTransfertList().add(trans);

	}

	/**
	 * Retourne la liste des ressources
	 * 
	 * @return
	 */
	public ObservableList<AppOperation> getRessources() {
		return _appRessourceList;
	}

	/**
	 * Retourne la liste des transferts
	 * 
	 * @return
	 */
	public ObservableList<AppTransfert> getTransferts() {
		return _appTransfertList;
	}

}
