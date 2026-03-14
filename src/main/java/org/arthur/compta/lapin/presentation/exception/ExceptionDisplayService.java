package org.arthur.compta.lapin.presentation.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service gérant l'affichage des exceptions
 */
public class ExceptionDisplayService {

    private static final Logger logger = LogManager.getLogger(ExceptionDisplayService.class);

    /**
     * Affiche l'exception dans une pop-up
     *
     * @param e
     *            l'exception à afficher
     */
    public static void showException(Exception e) {
        if (e != null) {
            logger.error("Erreur inattendue", e);
            ExceptionDialog excD = new ExceptionDialog(e);
            excD.setVisible(true);
        }
    }

}
