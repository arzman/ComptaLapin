package org.arthur.compta.lapin.application.manager;

import org.arthur.compta.lapin.dataaccess.files.FilesManager;

import javax.swing.*;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

public class ConfigurationManager {

    /** l'instance du singleton */
    private static ConfigurationManager _instance;

    /** La configuration de l'application */
    Properties _config;

    /** Constructeur */
    private ConfigurationManager() {

        _config = new Properties() {
            private static final long serialVersionUID = 3700533144616449180L;

            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }
        };

        InputStream is;
        try {
            Path fileConfig = Paths.get(FilesManager.getInstance().getConfFolder().toString(),
                    "configuration.properties");
            is = new FileInputStream(fileConfig.toFile());
            _config.load(is);
        } catch (IOException e) {
            // Rien osef
        }
    }

    /** Retourne l'unique instance du singleton */
    public static ConfigurationManager getInstance() {
        if (_instance == null) {
            _instance = new ConfigurationManager();
        }
        return _instance;
    }

    /** Enregistre la configuration */
    public void save() {
        try {
            Path fileConfig = Paths.get(FilesManager.getInstance().getConfFolder().toString(),
                    "configuration.properties");
            OutputStream os = new FileOutputStream(fileConfig.toFile());
            _config.store(os, "Toto");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Sauve la propriété */
    public void setProp(String key, String value) {
        _config.setProperty(key, value);
    }

    /** Retourne la propriété */
    public String getProp(String key, String defaultValue) {
        return _config.getProperty(key, defaultValue);
    }

    /**
     * Restaure et sauve les largeurs des colonnes d'un tableau Swing
     *
     * @param table
     *            le tableau
     * @param prefix
     *            prefix pour sauvegarder les paramètres
     */
    public void setPrefColumnWidth(JTable table, String prefix) {
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            String key = prefix + ".col" + i;
            int width = Integer.parseInt(getProp(key, "80"));
            col.setPreferredWidth(width);

            table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
                @Override
                public void columnMarginChanged(javax.swing.event.ChangeEvent e) {
                    // sauvegarde des largeurs
                    for (int j = 0; j < table.getColumnModel().getColumnCount(); j++) {
                        setProp(prefix + ".col" + j, String.valueOf(table.getColumnModel().getColumn(j).getWidth()));
                    }
                }
                @Override
                public void columnMoved(TableColumnModelEvent e) {
                }
                @Override
                public void columnAdded(TableColumnModelEvent e) {
                }
                @Override
                public void columnRemoved(TableColumnModelEvent e) {
                }
                @Override
                public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {
                }
            });
            break; // listener suffisant, ajouter 1 fois
        }
    }

}
