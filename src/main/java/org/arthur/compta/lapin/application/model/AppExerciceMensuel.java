package org.arthur.compta.lapin.application.model;

import java.util.Calendar;
import java.util.Random;

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

	public double getResultat() {
		
		Random r = new Random(); 
		double i = (-0.5 + r.nextDouble())*1000; 
		return i;
	}

}
