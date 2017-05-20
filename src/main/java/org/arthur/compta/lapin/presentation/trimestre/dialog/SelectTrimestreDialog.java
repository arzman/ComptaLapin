package org.arthur.compta.lapin.presentation.trimestre.dialog;

import java.util.Calendar;
import java.util.HashMap;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

public class SelectTrimestreDialog extends Dialog<String> {
	
	
	
	public SelectTrimestreDialog() {
		
		setTitle("Sélection du trimestre courant");
		
		// création de la zone de sélection
		GridPane contente = new GridPane();
		getDialogPane().setContent(contente);
		
		try {
			// récupératio des trimestres de l'application ainsi que leur date de début
			HashMap<String,Calendar> resumeTrimestre = TrimestreManager.getInstance().getAllTrimestreShortList();
			
			//TODO To be contined...
			ObservableList<String> cont = FXCollections.observableArrayList();
			
			cont.addAll(resumeTrimestre.keySet());
			
			ListView<String> listV = new ListView<>();
			listV.setItems(cont);
			
			contente.add(listV, 0, 0);
			
			
		} catch (ComptaException e) {
			ExceptionDisplayService.showException(e);
		}
		
		
	}

}
