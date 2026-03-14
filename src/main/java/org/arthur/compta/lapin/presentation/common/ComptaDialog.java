package org.arthur.compta.lapin.presentation.common;

import org.arthur.compta.lapin.ComptaLapin;
import org.arthur.compta.lapin.application.manager.ConfigurationManager;
import org.arthur.compta.lapin.presentation.resource.img.ImageLoader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Fenêtre de base qui enregistre sa taille
 */
public class ComptaDialog extends JDialog {

    /** Bordure rouge pour les erreurs de saisie */
    public static final LineBorder BORDER_ERROR = new LineBorder(Color.RED, 1);

    /** L'identifiant de la fenêtre pour la configuration */
    private final String _id;

    /** Indique si l'utilisateur a validé (OK) */
    protected boolean _confirmed = false;

    public ComptaDialog(String id, String title) {
        super(ComptaLapin.getMainFrame(), title, true);
        _id = id;

        // taille initiale depuis la configuration
        int w = Integer.parseInt(ConfigurationManager.getInstance().getProp(_id + ".size.width", "400"));
        int h = Integer.parseInt(ConfigurationManager.getInstance().getProp(_id + ".size.heigth", "300"));
        setSize(w, h);

        setLocationRelativeTo(ComptaLapin.getMainFrame());
        setResizable(true);

        // icône
        ImageIcon icon = ImageLoader.getImageIcon(ImageLoader.LAPIN_IMG);
        if (icon != null && icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            setIconImage(icon.getImage());
        }

        // sauvegarde de la taille lors du redimensionnement
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ConfigurationManager.getInstance().setProp(_id + ".size.width", String.valueOf(getWidth()));
                ConfigurationManager.getInstance().setProp(_id + ".size.heigth", String.valueOf(getHeight()));
            }
        });
    }

    /** Retourne true si l'utilisateur a cliqué OK */
    public boolean isConfirmed() {
        return _confirmed;
    }

}
