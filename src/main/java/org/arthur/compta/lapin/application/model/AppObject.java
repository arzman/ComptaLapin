package org.arthur.compta.lapin.application.model;

/**
 * Object applicatif Permet l'encapsulation d'objet métier
 *
 */
public abstract class AppObject<T> {

	/**
	 * Identifiant applicatif du compte.
	 */
	private int _appId;

	/**
	 * Positionne l'identifiant applicatif de l'objet.
	 * 
	 * @param id
	 *            l'identifiant
	 */
	public void setAppID(int id) {
		_appId = id;

	}

	/**
	 * Retourne l'identifiant application de l'objet
	 * 
	 * @return l'identifiant applicatif
	 */
	public int getAppId() {
		return _appId;
	}

	/**
	 * Retourne une instance de l'objet métier encapsulé
	 * 
	 * @return
	 */
	public abstract T getDBObject();

}
