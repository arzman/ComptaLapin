package org.arthur.compta.lapin.presentation.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

/**
 * Barre de menu de l'application
 *
 */
public class ComptaMenuBar extends MenuBar {

	public ComptaMenuBar() {
		super();

		Menu sysMenu = new Menu("Systeme");
		Menu trimMenu = new Menu("Trimestre");
		Menu compteMenu = new Menu("Compte");
		Menu budMenu = new Menu("Budget");
		getMenus().setAll(sysMenu, trimMenu, compteMenu, budMenu);
	}

}
