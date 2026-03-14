package org.arthur.compta.lapin.application.service;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.model.AppExerciceMensuel;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplate;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElement;
import org.arthur.compta.lapin.application.model.template.TrimestreTemplateElementFrequence;
import org.arthur.compta.lapin.dataaccess.db.OperationDataAccess;
import org.arthur.compta.lapin.dataaccess.db.TrimestreDataAccess;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.OperationType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Accès aux services des templates de trimestre dans l'application
 *
 */
public class TemplateService {

    /** Retourne le template de trimestre */
    public static TrimestreTemplate getTrimestreTemplate() throws ComptaException {
        return TrimestreDataAccess.getInstance().loadTemplateInfo();
    }

    /**
     * Ajoute les opérations du template au mois
     */
    public static void applyTtemplate(AppExerciceMensuel exMen, int num) throws ComptaException {
        TrimestreTemplate tmp = getTrimestreTemplate();

        for (TrimestreTemplateElement elt : tmp.getElements()) {
            int count = 0;

            if (elt.getFreq().equals(TrimestreTemplateElementFrequence.HEBDOMADAIRE)) {
                LocalDate deb = LocalDate.of(exMen.getDateDebut().getYear(), exMen.getDateDebut().getMonthValue(), 1);
                for (int j = 0; j < deb.lengthOfMonth(); j++) {
                    if (deb.getDayOfWeek().getValue() == elt.getOccurence()) {
                        count++;
                    }
                    deb = deb.plusDays(1);
                }
            }

            if (elt.getFreq().equals(TrimestreTemplateElementFrequence.MENSUEL)) {
                count++;
            }

            if (elt.getFreq().equals(TrimestreTemplateElementFrequence.TRIMESTRIEL)) {
                if (elt.getOccurence() == num) {
                    count++;
                }
            }

            for (int i = 0; i < count; i++) {
                createOperationFromTmpElt(exMen, elt);
            }
        }
    }

    private static void createOperationFromTmpElt(AppExerciceMensuel exMen, TrimestreTemplateElement elt)
            throws ComptaException {
        if (elt.getType().equals(OperationType.TRANSFERT.toString())) {
            exMen.addOperation(new AppTransfert(OperationDataAccess.getInstance().addOperation(elt.getNom(),
                    elt.getMontant(), OperationType.valueOf(elt.getType()), EtatOperation.PREVISION,
                    elt.getCompteSource().getAppId(), elt.getCompteCible().getAppId(), exMen.getAppId())));
        } else {
            exMen.addOperation(new AppOperation(OperationDataAccess.getInstance().addOperation(elt.getNom(),
                    elt.getMontant(), OperationType.valueOf(elt.getType()), EtatOperation.PREVISION,
                    elt.getCompteSource().getAppId(), -1, exMen.getAppId())));
        }
    }

    /** Met à jour le modèle de trimestre */
    public static void updateTrimestreTemplate(List<TrimestreTemplateElement> elementList) throws ComptaException {
        TrimestreDataAccess.getInstance().clearTrimTemplate();
        TrimestreDataAccess.getInstance().addTrimstreTempElts(elementList);
    }

    /** Retourne les fréquences possibles pour un élément de template */
    public static List<String> getTemplateEltFreq() {
        List<String> res = new ArrayList<>();
        for (TrimestreTemplateElementFrequence freq : TrimestreTemplateElementFrequence.values()) {
            res.add(freq.toString());
        }
        return res;
    }

    /** Retourne les occurences possible en fonction de la fréquence choisie */
    public static Integer[] getOccurenceForFreq(String freq) {
        if (freq.equals(TrimestreTemplateElementFrequence.HEBDOMADAIRE.toString())) {
            return new Integer[]{2, 3, 4, 5, 6, 7, 1};
        }
        if (freq.equals(TrimestreTemplateElementFrequence.TRIMESTRIEL.toString())) {
            return new Integer[]{0, 1, 2};
        }
        if (freq.equals(TrimestreTemplateElementFrequence.MENSUEL.toString())) {
            return new Integer[]{};
        }
        return new Integer[]{};
    }

    /** Retourne le gain moyen mensuel d'une liste d'éléments de template */
    public static double getGainMoyen(List<TrimestreTemplateElement> _elementList) {
        double gain = 0;
        for (TrimestreTemplateElement elt : _elementList) {
            double mont;
            switch (elt.getFreq()) {
                case HEBDOMADAIRE :
                    mont = elt.getMontant() * 52 / 12.0;
                    break;
                case MENSUEL :
                    mont = elt.getMontant();
                    break;
                case TRIMESTRIEL :
                    mont = elt.getMontant() / 3.0;
                    break;
                default :
                    mont = 0;
                    break;
            }
            switch (OperationType.valueOf(elt.getType())) {
                case DEPENSE :
                    gain = gain - mont;
                    break;
                case RESSOURCE :
                    gain = gain + mont;
                    break;
                default :
                    break;
            }
        }
        return gain;
    }

    public static double getPrevFromtemplate() throws ComptaException {
        return getGainMoyen(getTrimestreTemplate().getElements());
    }

}
