package org.arthur.compta.lapin.presentation.trimestre.cellfactory;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class NumMoisCellComboFactory implements Callback<ListView<Integer>, ListCell<Integer>> {

	@Override
	public ListCell<Integer> call(ListView<Integer> arg0) {
		// TODO Auto-generated method stub
		return new ListCell<Integer>() {

			@Override
			protected void updateItem(Integer item, boolean empty) {

				super.updateItem(item, empty);

				if (empty) {
					setText("");
				} else {

					switch (item) {

					case 0:
						setText("1er Mois");
						break;
					case 1:
						setText("2eme Mois");
						break;
					case 2:
						setText("3eme Mois");
						break;
					default:
						setText("#ERROR#");

					}

				}
			}

		};
	}

}
