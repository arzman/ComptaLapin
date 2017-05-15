package org.arthur.compta.lapin.dataaccess.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gère l'accès aux fichiers par l'application
 *
 */
public class FilesManager {

	/** l'instance du singleton */
	private static FilesManager _instance;

	/** répertoire racine de l'application ( ie là ou est situé le jar) */
	private String _rootFolder;

	private Path _dbFolderPath;

	/** Le constructeur par défaut */
	private FilesManager() {

		_rootFolder = System.getProperty("user.dir");

		try {
			//création du répertoire de configuration
			createConfFolder();
			
			//création du répertoire de la base de donnée
			createDBFolder();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retourne l'instance unique du singletons
	 * 
	 * @return
	 */
	public static FilesManager getInstance() {

		if (_instance == null) {
			_instance = new FilesManager();
		}

		return _instance;

	}

	/**
	 * Vérifie et crée les répertoires/fichiers de configuration de
	 * l'application
	 * 
	 * @throws IOException
	 */
	private void createConfFolder() throws IOException {

		// création du répertoire de conf
		Path confFolderPath = Paths.get(_rootFolder,"conf");

		if (!Files.exists(confFolderPath, new LinkOption[] {})) {

			Files.createDirectories(confFolderPath);
		}

	}
	
	/**
	 * Vérifie et crée les répertoires/fichiers de configuration de
	 * l'application
	 * 
	 * @throws IOException
	 */
	private void createDBFolder() throws IOException {

		// création du répertoire de conf
		_dbFolderPath = Paths.get(_rootFolder,"db");

		if (!Files.exists(_dbFolderPath, new LinkOption[] {})) {

			Files.createDirectories(_dbFolderPath);
		}

	}

	/**
	 * Retourne le chemin vers le dossier dédié à la base de donnée
	 * @return
	 */
	public Path getDBFolder() {

		return _dbFolderPath;
	}

}
