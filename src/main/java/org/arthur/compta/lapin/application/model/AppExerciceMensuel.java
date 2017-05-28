package org.arthur.compta.lapin.application.model;

import java.util.Calendar;
import java.util.Random;

import org.arthur.compta.lapin.model.ExerciceMensuel;
import org.arthur.compta.lapin.model.operation.Operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppExerciceMensuel extends AppObject {

	/**
	 * L'exercice encapsulé
	 */
	private ExerciceMensuel _exMensuel;

	/** La liste des dépenses */
	private ObservableList<AppOperation<Operation>> _appDepenseList;

	public AppExerciceMensuel(ExerciceMensuel exerciceMensuel) {

		_exMensuel = exerciceMensuel;

		_appDepenseList = FXCollections.observableArrayList();

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
		// TODO...a calculer
		Random r = new Random();
		double i = (-0.5 + r.nextDouble()) * 1000;
		return i;
	}

	/**
	 * Ajoute une dépense dans l'exercice mensuel
	 * @param dep la dépense
	 * @param id l'identifiant applicatif
	 */
	public void addDepense(Operation dep, String id) {

		AppOperation<Operation> appDep = new AppOperation<Operation>(dep);
		appDep.setAppID(id);
		
		_appDepenseList.add(appDep);
		_exMensuel.getDepensesList().add(dep);

	}

}
