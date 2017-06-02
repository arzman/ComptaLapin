package org.arthur.compta.lapin;

import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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
		primaryStage.setResizable(false);

		GridPane gridPane = new GridPane();
		ColumnConstraints colCons0 = new ColumnConstraints();
		colCons0.setFillWidth(true);
		colCons0.setHgrow(Priority.ALWAYS);
		colCons0.setHalignment(HPos.CENTER);
		colCons0.setPercentWidth(100);
		gridPane.getColumnConstraints().add(colCons0);

		// image du lapin
		ImageView imView = new ImageView(ImageLoader.getImage("bunny.jpg"));
		gridPane.add(imView, 0, 0);

		// barre de progression
		ProgressBar progressBar = new ProgressBar();
		progressBar.setMaxWidth(Double.MAX_VALUE);
		gridPane.add(progressBar, 0, 1);

		// un petit texte
		loadingLdl = new Label("Chargement en cours...");
		gridPane.add(loadingLdl, 0, 2);

		// création de la scene
		Scene scene = new Scene(gridPane);
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

		if (info instanceof ComptaPreloaderNotification) {
			loadingLdl.setText(((ComptaPreloaderNotification) info).getMessage());
		}

	}

}
