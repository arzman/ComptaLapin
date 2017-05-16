package org.arthur.compta.lapin.presentation.compte.action;

import org.arthur.compta.lapin.presentation.compte.dialog.EditCompteDialog;

/**
 * Modélise l'action d'ajout de compte : Ouvre un pop-up de saisie qui appellera le
 * service d'ajout de Compte
 *
 */
public class AddCompteAction {

	public void execute() {

		EditCompteDialog dia = new EditCompteDialog();
		dia.showAndWait();

	}

}
