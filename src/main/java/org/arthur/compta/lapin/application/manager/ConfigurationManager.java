package org.arthur.compta.lapin.application.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.arthur.compta.lapin.dataaccess.files.FilesManager;

public class ConfigurationManager {

	/**
	 * l'instance du singleton
	 */
	private static ConfigurationManager _instance;

	/**
	 * La configuration de l'application
	 */
	Properties _config;

	/**
	 * Constructeur
	 */
	private ConfigurationManager() {

		// chargement de la configuration
		_config = new Properties();

		InputStream is;
		try {

			Path fileConfig = Paths.get(FilesManager.getInstance().getConfFolder().toString(),
					"configuration.properties");

			is = new FileInputStream(fileConfig.toFile());
			_config.load(is);
		} catch (IOException e) {
			// Rien osef
		}

	}

	/**
	 * Retourne l'unique instance du singleton
	 * 
	 * @return
	 */
	public static ConfigurationManager getInstance() {

		if (_instance == null) {
			_instance = new ConfigurationManager();
		}

		return _instance;
	}

	/**
	 * Enregistre la configuration
	 */
	public void save() {

		OutputStream os;
		try {
			// chemin vers le fichier properties
			Path fileConfig = Paths.get(FilesManager.getInstance().getConfFolder().toString(),
					"configuration.properties");
			// on sauve
			os = new FileOutputStream(fileConfig.toFile());
			_config.store(os, "Toto");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Retourne la position du diviseur gauche-droite de la fenetre principale
	 * 
	 * @return
	 */
	public double getgdPosition() {

		double pos = 0.5;
		String res = _config.getProperty("mainscene.splitgaucheDroite.position", "0.5");

		try {
			pos = Double.parseDouble(res);
		} catch (NumberFormatException e) {
			// rien
		}

		return pos;
	}

	/**
	 * Sauve la position du diviseur gauche-droite de la fenetre principale
	 * 
	 * @param newValue
	 */
	public void setgdPosition(Number newValue) {
		_config.setProperty("mainscene.splitgaucheDroite.position", String.valueOf(newValue));
	}

	/**
	 * Retourne la position du diviseur haut-bas de la fenetre principale
	 * 
	 * @return
	 */
	public double gethbPosition() {
		double pos = 0.5;
		String res = _config.getProperty("mainscene.splitHautBas.position", "0.5");

		try {
			pos = Double.parseDouble(res);
		} catch (NumberFormatException e) {
			// rien
		}

		return pos;
	}

	/**
	 * Sauve la position du diviseur haut-bas de la fenetre principale
	 * 
	 * @param newValue
	 */
	public void sethbPosition(Number newValue) {
		_config.setProperty("mainscene.splitHautBas.position", String.valueOf(newValue));
	}

}
