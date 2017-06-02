package org.arthur.compta.lapin.presentation.trimestre.table;

import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.presentation.trimestre.cellfactory.MontantCellFactory;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OperationTableView<T extends AppOperation> extends TableView<T> {

	public OperationTableView() {

		// Colonne du nom
		TableColumn<T, String> colNom = new TableColumn<>("Libellé");
		colNom.setResizable(true);
		colNom.setEditable(false);
		// bind sur la nom
		colNom.setCellValueFactory(cellData -> cellData.getValue().libelleProperty());
		getColumns().add(colNom);

		// Colonne du solde
		TableColumn<T, Number> colMontant = new TableColumn<>("Montant");
		colMontant.setResizable(true);
		colMontant.setEditable(false);
		colMontant.setCellValueFactory(cellData -> cellData.getValue().montantProperty());
		colMontant.setCellFactory(new MontantCellFactory<T>());
		getColumns().add(colMontant);

	}

}
