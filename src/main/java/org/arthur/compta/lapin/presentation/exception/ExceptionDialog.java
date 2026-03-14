package org.arthur.compta.lapin.presentation.exception;

import org.arthur.compta.lapin.ComptaLapin;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Fenêtre affichant une exception
 */
public class ExceptionDialog extends JDialog {

    /**
     * Constructeur
     *
     * @param exc
     *            l'exception à afficher
     */
    public ExceptionDialog(Exception exc) {
        super(ComptaLapin.getMainFrame(), "Problème !!!", true);

        setLayout(new BorderLayout(5, 5));

        // message
        JLabel header = new JLabel(exc.getMessage() != null ? exc.getMessage() : exc.getClass().getSimpleName());
        header.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(header, BorderLayout.NORTH);

        // stacktrace
        StringWriter sw = new StringWriter();
        exc.printStackTrace(new PrintWriter(sw));
        JTextArea textArea = new JTextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // bouton fermer
        JButton closeBtn = new JButton("Fermer");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        setSize(600, 400);
        setLocationRelativeTo(ComptaLapin.getMainFrame());
    }

}
