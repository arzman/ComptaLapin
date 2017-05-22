package org.arthur.compta.lapin;

import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.dataaccess.files.FilesManager;
import org.arthur.compta.lapin.presentation.scene.MainScene;

import com.sun.javafx.application.LauncherImpl;

import javafx.application.Application;
import javafx.application.Preloader.PreloaderNotification;
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

		FilesManager.getInstance();
		DBManager.getInstance();
		notifyPreloader(new PreloaderNotification() {
		});

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// mise en place du titre de la fenêtre
		primaryStage.setTitle("Compta Du Lapin 2.0");
		// remplissage de la fenetre
		primaryStage.setScene(new MainScene());
		// on prend toute la place
		primaryStage.setMaximized(true);
		
		//sauvegarde des etats des IHM
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent event) {
				ConfigurationManager.getInstance().save();
				
			}
		});
		// ouverture de la fenetre
		primaryStage.show();

	}

}
