/**
 * 
 */
package org.arthur.compta.lapin.model;

import java.util.Calendar;

/**
 * Exerice comptable comprennant trois ExerciceMensuel.
 * 
 * @author Arthur
 * 
 */
public class Trimestre {

	/**
	 * Les exercices mensuels ordonnés.
	 */
	private ExerciceMensuel[] _exerciceMensuelTable;

	/**
	 * Constructeur par défaut
	 */
	public Trimestre() {
		setExerciceMensuel(new ExerciceMensuel[3]);

	}

	public Calendar getDateDebut() {

		return getExerciceMensuel()[0].getDateDebut();
	}

	// GETTER'N'SETTER -------------------------------------------------

	public Calendar getDateFin() {
		return getExerciceMensuel()[2].getDateDebut();
	}

	public ExerciceMensuel[] getExerciceMensuel() {
		return _exerciceMensuelTable;
	}

	public void setExerciceMensuel(ExerciceMensuel[] exerciceMensuel) {
		this._exerciceMensuelTable = exerciceMensuel;
	}

}
