package org.arthur.compta.lapin.application.manager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

import org.arthur.compta.lapin.dataaccess.files.FilesManager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

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
	 * Restaure et sauve les largeurs des colonnes d'un tableau
	 * 
	 * @param table  le tableau
	 * @param prefix prefix pour sauvegarder les paramètres
	 */
	public void setPrefColumnWidth(TableView<?> table, String prefix) {

		// restitution des largeur de colonnes
		for (TableColumn<?, ?> col : table.getColumns()) {

			col.widthProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					setProp(prefix + "." + col.getId(), String.valueOf(newValue));

				}
			});
			col.setPrefWidth(Double.parseDouble(getProp(prefix + "." + col.getId(), "50")));
		}

	}

	/**
	 * Restaure et sauve les largeurs des colonnes d'un tableau-arbre
	 * 
	 * @param tree   l'arbre
	 * @param prefix le prefic pour la sauvegarde des largeurs
	 */
	public void setPrefColumnWidth(TreeTableView<?> tree, String prefix) {

		// restitution des largeur de colonnes
		for (TreeTableColumn<?, ?> col : tree.getColumns()) {

			col.widthProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					setProp(prefix + col.getId(), String.valueOf(newValue));

				}
			});
			col.setPrefWidth(Double.parseDouble(getProp(prefix + col.getId(), "50")));
		}

	}

}
