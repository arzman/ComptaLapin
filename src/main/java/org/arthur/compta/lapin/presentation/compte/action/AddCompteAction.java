package org.arthur.compta.lapin.presentation.compte.action;

import java.util.Optional;

import org.arthur.compta.lapin.model.Compte;
import org.arthur.compta.lapin.presentation.compte.dialog.EditCompteDialog;

/**
 * Modélise l'action d'ajout de compte : Ouvre un pop-up de saisie et appelle le service d'ajout de Compte
 * @author ARDUFLOT
 *
 */
public class AddCompteAction {

	public void execute() {
		
		EditCompteDialog dia = new EditCompteDialog();
		
		Optional<Compte> opt = dia.showAndWait();
		
		if(opt.isPresent()){
			
			System.out.println(opt.get());
			
		}
		
	}

}
