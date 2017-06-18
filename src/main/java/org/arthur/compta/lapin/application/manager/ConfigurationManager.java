package org.arthur.compta.lapin.application.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import org.arthur.compta.lapin.application.exception.ComptaException;
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
		_config = new Properties() {
			/** generated */
			private static final long serialVersionUID = 3700533144616449180L;

			@Override

			public synchronized Enumeration<Object> keys() {
				return Collections.enumeration(new TreeSet<Object>(super.keySet()));
			}
		};

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
	 * Sauve la propriété
	 * 
	 * @param key
	 * @param value
	 */
	public void setProp(String key, String value) {
		_config.setProperty(key, value);

	}

	/**
	 * Retourne la propriété
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProp(String key, String defaultValue) {
		return _config.getProperty(key, defaultValue);
	}

	/**
	 * Positionne une feuille de style si aucune n'est présente
	 * 
	 * @throws ComptaException
	 *             Echec de l'écriture de la feuille de style
	 */
	public Path installStyleSheet() throws ComptaException {

		Path confPath = FilesManager.getInstance().getConfFolder();

		Path stylePath = Paths.get(confPath.toString(), "StyleSheet.css");

		if (!Files.exists(stylePath, new LinkOption[] {})) {

			try (InputStream io = getClass().getClassLoader()
					.getResourceAsStream("org/arthur/compta/lapin/presentation/resource/css/StyleSheet.css");
					FileOutputStream fos = new FileOutputStream(stylePath.toFile());

			) {

				int b = io.read();
				while (b != -1) {

					fos.write(b);
					b = io.read();

				}
			} catch (Exception e) {
				throw new ComptaException("Impossible d'ecrire la feuille de style", e);
			}

		}

		return stylePath;

	}

}
