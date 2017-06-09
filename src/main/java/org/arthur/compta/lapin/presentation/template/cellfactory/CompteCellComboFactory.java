package org.arthur.compta.lapin.presentation.template.cellfactory;

import org.arthur.compta.lapin.application.model.AppCompte;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
/**
 * Factory permettant d'afficher des comptes dans un combobox
 *
 */
public class CompteCellComboFactory implements Callback<ListView<AppCompte>, ListCell<AppCompte>> {

	@Override
	public ListCell<AppCompte> call(ListView<AppCompte> param) {
		
		return new ListCell<AppCompte>(){
            @Override
            protected void updateItem(AppCompte item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.getNom());
                }
            }
        } ;
		
	}

}
