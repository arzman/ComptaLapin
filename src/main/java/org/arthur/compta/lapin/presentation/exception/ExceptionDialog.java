package org.arthur.compta.lapin.presentation.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Fenêtre affichant une exception
 *
 */
public class ExceptionDialog extends Alert {

	
	/**
	 * exception a afficher
	 */
	private  Exception exception;
	
	/**
	 * Le constructeur
	 * @param exc l'exception a afficher
	 */
	public ExceptionDialog(Exception exc) {
		
		super(AlertType.ERROR);
		
		exception = exc;
		
		//création de la fenetre
		setTitle("Problème !!!");
		setHeaderText(exception.getMessage());
		
		
		// Creation de la zone de détails
		//récupération de la stacktrace
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		exception.printStackTrace(pw);
		String exceptionText = sw.toString();

		//affichage
		Label label = new Label("Voila la stacktrace :");
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		getDialogPane().setExpandableContent(expContent);
	}
	
}
