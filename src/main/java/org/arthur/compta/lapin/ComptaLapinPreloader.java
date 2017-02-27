package org.arthur.compta.lapin;

import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class ComptaLapinPreloader extends Preloader {

	/**
	 * Parent de l'IHM du preloader (splashScreen)
	 */
	private Stage preloaderStage;

	private Label loadingLdl;

	@Override
	public void start(Stage primaryStage) throws Exception {

		this.preloaderStage = primaryStage;

		GridPane loading = new GridPane();

		//image du lapin
		Image image = new Image("org/arthur/compta/lapin/presentation/resource/img/bunny.jpg");
		ImageView imView = new ImageView(image);
		loading.add(imView, 0, 0);
		
		
		//barre de progression
		ProgressBar progressBar = new ProgressBar();
		GridPane.setFillWidth(progressBar, Boolean.TRUE);
		loading.add(progressBar, 0, 1);
		
		

		// un petit texte
		loadingLdl = new Label("Chargement en cours...");
		loading.add(loadingLdl, 0, 2);

		// création de la scene
		Scene scene = new Scene(loading);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
		if (stateChangeNotification.getType() == Type.BEFORE_START) {

			preloaderStage.close();

		}

	}

	@Override
	public void handleApplicationNotification(PreloaderNotification info) {

		loadingLdl.setText("Ca va s'ouvrir");

	}

}
