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
	private String _exerciceMensuelId;

	/**
	 * L'id du trimestre
	 */
	private String _trimestreId;

	/**
	 * La position dans le mois
	 */
	private int _numMois;

	/**
	 * 
	 * @param exerciceMensuelId
	 * @param appTrimestreId
	 * @param numMois
	 */
	public AppExerciceMensuelLightId(String exerciceMensuelId, String appTrimestreId, int numMois) {

		_exerciceMensuelId = exerciceMensuelId;
		_trimestreId = appTrimestreId;
		_numMois = numMois;

	}

	public String getExerciceMensuelId() {
		return _exerciceMensuelId;
	}

	public String getTrimestreId() {
		return _trimestreId;
	}

	public int getNumMois() {
		return _numMois;
	}

}
