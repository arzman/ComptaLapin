package org.arthur.compta.lapin;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.application.manager.TrimestreManager;
import org.arthur.compta.lapin.dataaccess.db.DBManager;
import org.arthur.compta.lapin.dataaccess.files.FilesManager;
import org.arthur.compta.lapin.presentation.menu.ComptaMenuBar;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;
import org.arthur.compta.lapin.presentation.scene.MainPane;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Point d'entrée de l'application ComptaLapin
 */
public class ComptaLapin {

    /** La fenêtre principale */
    private static JFrame _mainFrame;

    public static JFrame getMainFrame() {
        return _mainFrame;
    }

    public static void main(String[] args) {

        // initialisation de l'accès aux fichiers
        FilesManager.getInstance();
        // initialisation de la base de données
        DBManager.getInstance();
        // initialisation des comptes
        CompteManager.getInstance();
        // récupération du trimestre courant
        try {
            TrimestreManager.getInstance().recoverTrimestre();
        } catch (ComptaException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            _mainFrame = new JFrame("Compta Du Lapin 2.2");
            _mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            _mainFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    ConfigurationManager.getInstance().save();
                    DBManager.getInstance().stop();
                    _mainFrame.dispose();
                    System.exit(0);
                }
            });

            // icône de la fenêtre
            ImageIcon icon = ImageLoader.getImageIcon(ImageLoader.LAPIN_IMG);
            if (icon != null) {
                _mainFrame.setIconImage(icon.getImage());
            }

            MainPane mainPane = new MainPane();
            _mainFrame.setContentPane(mainPane);
            _mainFrame.setJMenuBar(new ComptaMenuBar(mainPane));

            _mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            _mainFrame.setSize(1200, 800);
            _mainFrame.setVisible(true);
        });
    }

}
