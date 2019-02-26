package org.arthur.compta.lapin;

import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.core.config.Configurator;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.dataaccess.files.FilesManager;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.scene.MainScene;

import com.sun.javafx.application.LauncherImpl;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Classe principale de l'application.
 *
 */
public class ComptaLapin extends Application {
	public static void main(String[] args) {

		// lancement de l'ihm
		LauncherImpl.launchApplication(ComptaLapin.class, ComptaLapinPreloader.class, args);

	}

	@Override
	public void init() throws Exception {

		super.init();

		notifyPreloader(new ComptaPreloaderNotification("Chargement de la configuration"));
		FilesManager.getInstance();
		notifyPreloader(new ComptaPreloaderNotification("Ouverture de la base"));
		DBManager.getInstance();
		notifyPreloader(new ComptaPreloaderNotification("Chargement des comptes"));
		CompteManager.getInstance();
		notifyPreloader(new ComptaPreloaderNotification("Chargement du trimestre courant"));
		TrimestreManager.getInstance().recoverTrimestre();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			// initialisationb du logger
			Path confLog4j2Path = Paths.get(FilesManager.getInstance().getConfFolder().toString(), "log4j2.xml");

			if (!Files.exists(confLog4j2Path, new LinkOption[] {})) {

				InputStream in = ComptaLapin.class
						.getResourceAsStream("/org/arthur/compta/lapin/dataaccess/files/ressources/conf/log4j2.xml");
				Files.copy(in, confLog4j2Path, new CopyOption[] {});

			}
			Configurator.initialize(null, confLog4j2Path.toString());

			// mise en place du titre de la fenêtre
			primaryStage.setTitle("Compta Du Lapin 2.1");
			primaryStage.getIcons().add(ImageLoader.getImage(ImageLoader.LAPIN_IMG));
			primaryStage.getIcons().add(ImageLoader.getImage(ImageLoader.LAPIN32_IMG));

			// remplissage de la fenetre
			MainScene sc = new MainScene();
			primaryStage.setScene(sc);
			// on prend toute la place
			primaryStage.setMaximized(true);

			// sauvegarde des etats des IHM
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					ConfigurationManager.getInstance().save();

				}
			});

			setUserAgentStylesheet(STYLESHEET_MODENA);

			// ouverture de la fenetre
			primaryStage.show();
		} catch (Exception e) {
			ExceptionDisplayService.showException(e);
		}

	}

	@Override
	public void stop() throws Exception {

		super.stop();

		DBManager.getInstance().stop();
	}

}
