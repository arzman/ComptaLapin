package org.arthur.compta.lapin.presentation.trimestre.table;

import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppTransfert;

import javafx.scene.control.TableColumn;

public class TransfertTableView extends OperationTableView<AppTransfert> {

	public TransfertTableView() {

		super();

		// Colonne du compte source
		TableColumn<AppTransfert, AppCompte> colSource = new TableColumn<>("Source");
		colSource.setResizable(true);
		colSource.setEditable(false);
		// bind sur la nom
		colSource.setCellValueFactory(cellData -> cellData.getValue().compteSourceProperty());
		getColumns().add(colSource);

		// Colonne du compte cible
		TableColumn<AppTransfert, AppCompte> colCible = new TableColumn<>("Cible");
		colCible.setResizable(true);
		colCible.setEditable(false);
		colCible.setCellValueFactory(cellData -> cellData.getValue().compteCibleProperty());
		getColumns().add(colCible);
	}

}
