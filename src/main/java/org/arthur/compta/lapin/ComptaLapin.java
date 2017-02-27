package org.arthur.compta.lapin;

import org.arthur.compta.lapin.presentation.scene.MainScene;

import com.sun.javafx.application.LauncherImpl;

import javafx.application.Application;
import javafx.application.Preloader.PreloaderNotification;
import javafx.application.Preloader.StateChangeNotification;
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
    	LauncherImpl.launchApplication(ComptaLapin.class, ComptaLapinPreloader.class,args);
    	
    }

    
    @Override
    public void init() throws Exception {
    	
    	super.init();
    	
    	Thread.sleep(2000);
    	notifyPreloader( new PreloaderNotification() {
		});
    	Thread.sleep(2000);
    	
    	
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
