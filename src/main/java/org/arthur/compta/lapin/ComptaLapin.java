package org.arthur.compta.lapin;

import org.arthur.compta.lapin.presentation.scene.MainScene;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;

/**
 * Classe principale de l'application.
 *
 */
public class ComptaLapin extends Application
{
    public static void main( String[] args )
    {
       
    	//lancement de l'ihm
    	launch(args);
    	
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
		//mise en place du titre de la fenêtre
		primaryStage.setTitle("Compta Du Lapin 2.0");
		
		//remplissage de la fenetre
        primaryStage.setScene(new MainScene());
		
		//ouverture de la fenetre
		primaryStage.show();
		
	}
}
