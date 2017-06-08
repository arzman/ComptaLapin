package org.arthur.compta.lapin.presentation.operation.dialog;

import java.util.Calendar;

import org.arthur.compta.lapin.presentation.model.OperationSearchResult;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Fenetre de recherche d'opération
 *
 */
public class SearchOperationDialog extends Dialog<String> {

	/** champ de saisie du nom */
	private TextField _libTxt;
	/** champ de saisie du montant */
	private TextField _montantTxt;
	/** champ de saisie du montant */
	private TextField _toleranceTxt;

	private ObservableList<OperationSearchResult> _resultatList;

	/** champ de saisie du type */

	public SearchOperationDialog() {

		setTitle("Recherche une opération");
		setResizable(true);

		_resultatList = FXCollections.observableArrayList();

		GridPane root = new GridPane();
		getDialogPane().setContent(root);

		ColumnConstraints col0Cons = new ColumnConstraints();
		col0Cons.setFillWidth(true);
		col0Cons.setHgrow(Priority.SOMETIMES);
		root.getColumnConstraints().add(col0Cons);

		ColumnConstraints col1Cons = new ColumnConstraints();
		col1Cons.setFillWidth(true);
		col1Cons.setHgrow(Priority.ALWAYS);
		root.getColumnConstraints().add(col1Cons);

		// création des zones de saisie des critères de recherche
		createCriteriaFields(root);
		// créations de la zone de résultat
		createSearchRes(root);
		// création des buttons
		createButtonBar();

	}

	/**
	 * Création des champs de recherche
	 * 
	 * @param root
	 *            le parent des IHM
	 */
	private void createCriteriaFields(GridPane root) {

		// saisie du nom
		Label nomLdl = new Label("Libellé contient : ");
		root.add(nomLdl, 0, 0);
		_libTxt = new TextField();
		root.add(_libTxt, 1, 0);

		// saisie du montant
		Label montantLdl = new Label("Montant égale : ");
		root.add(montantLdl, 0, 1);
		_montantTxt = new TextField();
		root.add(_montantTxt, 1, 1);

		// saisie de la tolérance du montant
		Label tolLbl = new Label(" + / - : ");
		root.add(tolLbl, 0, 2);
		_toleranceTxt = new TextField();
		root.add(_toleranceTxt, 1, 2);

	}

	/**
	 * Crée la zone d'affichage des résultats d'un recherche d'opération
	 * 
	 * @param root
	 *            le parent de l'IHM
	 */
	private void createSearchRes(GridPane root) {

		// le bouton de recherche
		Button searchBut = new Button("Rechercher");
		root.add(searchBut, 0, 3);

		// le tableau de résultat
		TableView<OperationSearchResult> _resTable = new TableView<>();
		_resTable.setItems(_resultatList);

		// libellé
		TableColumn<OperationSearchResult, String> colLib = new TableColumn<OperationSearchResult, String>();
		colLib.setText("Libellé");
		colLib.setResizable(true);
		colLib.setSortable(true);

		// montant
		TableColumn<OperationSearchResult, Double> colMontant = new TableColumn<OperationSearchResult, Double>();
		colMontant.setText("Montant");
		colMontant.setResizable(true);
		colMontant.setSortable(true);

		// date
		TableColumn<OperationSearchResult, Calendar> colDate = new TableColumn<OperationSearchResult, Calendar>();
		colDate.setText("Mois");
		colDate.setResizable(true);
		colDate.setSortable(true);

		// ajout des colonnes
		_resTable.getColumns().add(colLib);
		_resTable.getColumns().add(colMontant);
		_resTable.getColumns().add(colDate);

		root.add(_resTable, 0, 4, 2, 1);

	}

	/**
	 * Création des boutons
	 */
	private void createButtonBar() {
		// bouton ok
		ButtonType okButton = new ButtonType("Fermer", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().add(okButton);

	}

}
