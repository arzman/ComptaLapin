package org.arthur.compta.lapin.presentation.compte.pane;

import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.presentation.compte.action.AddCompteAction;
import org.arthur.compta.lapin.presentation.compte.cellfactory.SoldeCompteCellFactory;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Panneau d'affichage des comptes
 *
 */
public class ComptePane extends GridPane {

	public ComptePane() {

		// paramétrage du layout
		ColumnConstraints colCons = new ColumnConstraints();
		colCons.setFillWidth(true);
		colCons.setHgrow(Priority.ALWAYS);
		getColumnConstraints().add(colCons);

		setVgap(2);
		setPadding(new Insets(2, 2, 2, 2));
		
		// Création de la barre des boutons
		createButtonBar();

		// Création du tableau de compte
		createCompteTable();

	}

	/**
	 * Création de la barre des boutons
	 */
	private void createButtonBar() {

		GridPane buttonBar = new GridPane();
		buttonBar.setHgap(4);
		add(buttonBar, 0, 0);

		// Bouton d'ajout de Compte
		Button addButton = new Button("");
		addButton.setGraphic(new ImageView(ImageLoader.getImage(ImageLoader.ADD_IMG)));
		addButton.setTooltip(new Tooltip("AJouter un compte"));
		
		buttonBar.add(addButton, 0, 0);
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				
				AddCompteAction act = new AddCompteAction();
				act.execute();
				
			}
		});


	}

	/**
	 * Création du tableau des comptes
	 */
	private void createCompteTable() {

		// Création de la table des comptes
		TableView<AppCompte> table = new TableView<>();
		table.setMaxWidth(Double.MAX_VALUE);
		add(table, 0, 1);

		// Colonne du nom
		TableColumn<AppCompte, String> colNom = new TableColumn<>("Nom");
		colNom.setResizable(true);
		colNom.setEditable(false);
		colNom.setCellValueFactory(new PropertyValueFactory<AppCompte, String>("nomProp"));
		table.getColumns().add(colNom);

		// Colonne du montant
		TableColumn<AppCompte, Double> colMontant = new TableColumn<>("Solde");
		colMontant.setResizable(true);
		colMontant.setEditable(false);
		colMontant.setCellValueFactory(new PropertyValueFactory<>("soldeProp"));
		colMontant.setCellFactory(new SoldeCompteCellFactory());
		table.getColumns().add(colMontant);

		// Colonne solde prevu a la fin du 1er mois
		TableColumn<AppCompte, String> colprev1 = new TableColumn<>("1er Mois");
		colprev1.setResizable(true);
		colprev1.setEditable(false);
		table.getColumns().add(colprev1);

		// Colonne du solde prevu à la fin du 2eme mois
		TableColumn<AppCompte, String> colprev2 = new TableColumn<>("2eme Mois");
		colprev2.setResizable(true);
		colprev2.setEditable(false);
		table.getColumns().add(colprev2);

		// Colonne du solde prévu à la fin du 3eme mois
		TableColumn<AppCompte, String> colprev3 = new TableColumn<>("3eme Mois");
		colprev3.setResizable(true);
		colprev3.setEditable(false);
		table.getColumns().add(colprev3);

		// bind à la liste des comptes
		table.setItems(CompteManager.getInstance().getCompteList());

	}

}
