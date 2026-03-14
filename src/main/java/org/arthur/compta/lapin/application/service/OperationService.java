package org.arthur.compta.lapin.application.service;

import org.arthur.compta.lapin.application.exception.ComptaException;
import org.arthur.compta.lapin.application.manager.CompteManager;
import org.arthur.compta.lapin.application.model.AppCompte;
import org.arthur.compta.lapin.application.model.AppOperation;
import org.arthur.compta.lapin.application.model.AppTransfert;
import org.arthur.compta.lapin.application.model.OperationSearchResult;
import org.arthur.compta.lapin.dataaccess.db.OperationDataAccess;
import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.OperationType;

import java.util.ArrayList;
import java.util.List;

public class OperationService {

/**
 * Permutte l'état d'une opération et répercute les conséquences
 */
public static void switchEtatOperation(AppOperation appOp) throws ComptaException {
appOp.switchEtat();
CompteManager.getInstance().operationSwitched(appOp);
OperationDataAccess.getInstance().updateOperation(appOp.getDBObject());
}

/**
 * Retourne les types possibles pour une opération
 */
public static List<String> getOperationType() {
List<String> res = new ArrayList<>();
for (OperationType opeType : OperationType.values()) {
res.add(opeType.toString());
}
return res;
}

/**
 * Edite l'opération passée en paramètre
 */
public static AppOperation editOperation(AppOperation _operation, String newLib, double newMontant,
AppCompte newCompteSrc, AppCompte newCompteCibles) throws ComptaException {

boolean toSwitch = false;
if (_operation.getEtat().equals(EtatOperation.PRISE_EN_COMPTE)) {
toSwitch = true;
switchEtatOperation(_operation);
}

_operation.setLibelle(newLib);
_operation.setMontant(newMontant);
_operation.setCompteSrc(newCompteSrc);

if (_operation instanceof AppTransfert) {
((AppTransfert) _operation).setCompteCible(newCompteCibles);
}

OperationDataAccess.getInstance().updateOperation(_operation.getDBObject());

if (toSwitch) {
switchEtatOperation(_operation);
}

CompteManager.getInstance().calculateSoldePrev(newCompteSrc);
CompteManager.getInstance().calculateSoldePrev(newCompteCibles);

return _operation;
}

/**
 * Effectue une recherche sur les opérations.
 */
public static List<OperationSearchResult> doSearch(String lib, String montant, String tolerance) throws ComptaException {
return OperationDataAccess.getInstance().searchOperation(lib, montant, tolerance);
}

}
