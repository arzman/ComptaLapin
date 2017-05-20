package org.arthur.compta.lapin.application.model;

import java.util.Calendar;

import org.arthur.compta.lapin.model.ExerciceMensuel;

public class AppExerciceMensuel extends AppObject {

	/**
	 * L'exercice encapsulé
	 */
	private ExerciceMensuel _exMensuel;

	public AppExerciceMensuel(ExerciceMensuel exerciceMensuel) {

		_exMensuel = exerciceMensuel;

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
	 * @return
	 */
	public ExerciceMensuel getExcerciceMensuel() {
		return _exMensuel;
	}

}
