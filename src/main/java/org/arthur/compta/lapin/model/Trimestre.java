/**
 * 
 */
package org.arthur.compta.lapin.model;

/**
 * Exerice comptable comprennant trois ExerciceMensuel.
 * 
 * @author Arthur
 * 
 */
public class Trimestre {

	/** id */
	private int _id;
	/** Les exercices mensuels ordonnés */
	private Integer[] _exerciceMensuelIds;

	/**
	 * Constructeur par défaut
	 */
	public Trimestre(int id, int premierMoisid, int deuxMoisId, int troisMoisId) {
		_id = id;
		_exerciceMensuelIds = new Integer[] { premierMoisid, deuxMoisId, troisMoisId };

	}

	// GETTER'N'SETTER -------------------------------------------------

	public int getId() {
		return _id;
	}

	public Integer[] getExerciceMensuelIds() {
		return _exerciceMensuelIds;
	}

}
