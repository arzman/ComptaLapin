package org.arthur.compta.lapin;

import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.dataaccess.files.FilesManager;
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

		// mise en place du titre de la fenêtre
		primaryStage.setTitle("Compta Du Lapin 2.0");
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

	}

	@Override
	public void stop() throws Exception {

		super.stop();

		DBManager.getInstance().stop();
	}

}
