package org.arthur.compta.lapin.presentation.trimestre.table;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.service.OperationService;
import org.arthur.compta.lapin.presentation.exception.ExceptionDisplayService;
import org.arthur.compta.lapin.presentation.trimestre.cellfactory.EtatCellFactory;
import org.arthur.compta.lapin.presentation.trimestre.cellfactory.MontantCellFactory;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class OperationTableView<T extends AppOperation> extends TableView<T> {

	public OperationTableView() {

		// Colonne etat
		TableColumn<T, String> colEtat = new TableColumn<>(" ");
		colEtat.setResizable(true);
		colEtat.setEditable(false);
		// bind sur l etat
		colEtat.setCellFactory(new EtatCellFactory<T>());
		colEtat.setCellValueFactory(cellData -> cellData.getValue().etatProperty());
		getColumns().add(colEtat);

		// Colonne du libelle
		TableColumn<T, String> colNom = new TableColumn<>("Libellé");
		colNom.setResizable(true);
		colNom.setEditable(false);
		colNom.setSortable(true);
		// bind sur la nom
		colNom.setCellValueFactory(cellData -> cellData.getValue().libelleProperty());
		getColumns().add(colNom);

		// Colonne du solde
		TableColumn<T, Number> colMontant = new TableColumn<>("Montant");
		colMontant.setResizable(true);
		colMontant.setEditable(false);
		colMontant.setSortable(true);
		colMontant.setCellValueFactory(cellData -> cellData.getValue().montantProperty());
		colMontant.setCellFactory(new MontantCellFactory<T>());
		getColumns().add(colMontant);

		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				// écoute sur le double-clic
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {

					try {
						OperationService.switchEtatOperation(getSelectionModel().getSelectedItem());
					} catch (ComptaException e) {
						ExceptionDisplayService.showException(e);
					}

				}
			}
		});

	}

}
