package org.arthur.compta.lapin.application.model;

/**
 * Object applicatif Permet l'encapsulation d'objet métier
 *
 */
public class AppObject{

	/**
	 * Identifiant applicatif du compte.
	 */
	private String _appId;

	/**
	 * Positionne l'identifiant applicatif de l'objet.
	 * 
	 * @param id
	 *            l'identifiant
	 */
	public void setAppID(String id) {
		_appId = id;

	}

	/**
	 * Retourne l'identifiant application de l'objet
	 * 
	 * @return l'identifiant applicatif
	 */
	public String getAppId() {
		return _appId;
	}

}
