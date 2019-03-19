package org.arthur.compta.lapin.application.model;

import java.time.LocalDate;
import java.util.List;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.model.ExerciceMensuel;
import org.arthur.compta.lapin.model.operation.Operation;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppExerciceMensuel extends AppObject<ExerciceMensuel> {

	/** La liste des dépenses */
	private ObservableList<AppOperation> _appDepenseList;
	/** La liste des ressources */
	private ObservableList<AppOperation> _appRessourceList;
	/** La liste des transferts */
	private ObservableList<AppTransfert> _appTransfertList;
	/** date de début */
	private SimpleObjectProperty<LocalDate> _dateDebutProp;
	/** date de fin */
	private SimpleObjectProperty<LocalDate> _dateFinProp;
	/** Resultat previsionnel */
	private SimpleDoubleProperty _resPrevProp;

	public AppExerciceMensuel(ExerciceMensuel exerciceMensuel) throws ComptaException {

		setAppID(exerciceMensuel.getId());
		_dateDebutProp = new SimpleObjectProperty<LocalDate>(exerciceMensuel.getDateDebut());
		_dateFinProp = new SimpleObjectProperty<LocalDate>(exerciceMensuel.getDateFin());

		_resPrevProp = new SimpleDoubleProperty(exerciceMensuel.getResultatPrev());

		_appDepenseList = FXCollections.observableArrayList();
		_appRessourceList = FXCollections.observableArrayList();
		_appTransfertList = FXCollections.observableArrayList();

		List<Operation> opList = TrimestreManager.getInstance().getOperationForEM(exerciceMensuel.getId());
		for (Operation op : opList) {

			switch (op.getType()) {

			case DEPENSE:
				_appDepenseList.add(new AppOperation(op));
				break;
			case RESSOURCE:
				_appRessourceList.add(new AppOperation(op));
				break;

			case TRANSFERT:
				_appTransfertList.add(new AppTransfert(op));
				break;

			}

		}

	}

	/**
	 * Retourne la date de début de l'exercice
	 * 
	 * @return
	 */
	public LocalDate getDateDebut() {
		return _dateDebutProp.get();
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
	private void addDepense(AppOperation appDep) {

		_appDepenseList.add(appDep);

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
	private void addRessource(AppOperation appRes) {

		_appRessourceList.add(appRes);

	}

	/**
	 * Ajoute un transfert dans l'exercice mensuel
	 * 
	 * @param apptrans
	 *            le transfert
	 */
	private void addTransfert(AppTransfert apptr) {

		_appTransfertList.add(apptr);

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

	/**
	 * Retourne le résultat prévisionnel
	 * 
	 * @return
	 */
	public double getResultatPrev() {
		return _resPrevProp.get();
	}

	public void addOperation(AppOperation apptr) {

		switch (apptr.getType()) {

		case DEPENSE:
			addDepense(apptr);
			break;
		case RESSOURCE:
			addRessource(apptr);
			break;
		case TRANSFERT:
			addTransfert((AppTransfert) apptr);
			break;

		}

	}

	@Override
	public ExerciceMensuel getDBObject() {
		return new ExerciceMensuel(getAppId(), getDateDebut(), getDateDebut(), getResultatPrev());
	}

	public LocalDate getDateFin() {

		return _dateFinProp.get();
	}

}
