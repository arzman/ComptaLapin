package org.arthur.compta.lapin.application.model;

/**
 * Cette classe collecte différentes informations d'un ExerciceMensuel : son id
 * , l'id de son trimestre et sa position dans le trimestre
 *
 */
public class AppExerciceMensuelLightId {

	/**
	 * L'id de l'exercice mensuel
	 */
	private final int _exerciceMensuelId;

	/**
	 * L'id du trimestre
	 */
	private final int _trimestreId;

	/**
	 * La position dans le mois
	 */
	private final int _numMois;

	/**
	 * 
	 * @param exerciceMensuelId
	 * @param appTrimestreId
	 * @param numMois
	 */
	public AppExerciceMensuelLightId(int exerciceMensuelId, int appTrimestreId, int numMois) {

		_exerciceMensuelId = exerciceMensuelId;
		_trimestreId = appTrimestreId;
		_numMois = numMois;

	}

	public int getExerciceMensuelId() {
		return _exerciceMensuelId;
	}

	public int getTrimestreId() {
		return _trimestreId;
	}

	public int getNumMois() {
		return _numMois;
	}

}
