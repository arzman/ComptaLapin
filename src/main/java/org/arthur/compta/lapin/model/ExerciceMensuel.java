package org.arthur.compta.lapin.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.arthur.compta.lapin.model.operation.EtatOperation;
import org.arthur.compta.lapin.model.operation.Operation;
import org.arthur.compta.lapin.model.operation.OperationType;
import org.arthur.compta.lapin.model.operation.TransfertOperation;

/**
 * 
 * Exercice comptable sur 1 mois. Regroupe toute les opérations sur un mois.
 * 
 */
public class ExerciceMensuel implements Comparable<ExerciceMensuel> {

	/**
	 * Liste des ressources durant l'exercice.
	 */
	private List<Operation> _ressourcesList;

	/**
	 * Liste des dépenses durant l'exercice.
	 */
	private List<Operation> _depensesList;

	/**
	 * Liste des transferts durant l'exercice.
	 */
	private List<TransfertOperation> _transfertList;
	/**
	 * Date de début de l'exercice
	 */
	private Calendar _dateDebut;
	/**
	 * Date de fin de l'exercice
	 */
	private Calendar _dateFin;

	/**
	 * 
	 * Constructeur par d�faut
	 */
	public ExerciceMensuel() {

		_ressourcesList = new ArrayList<Operation>();
		_depensesList = new ArrayList<Operation>();
		_transfertList = new ArrayList<TransfertOperation>();

	}

	/**
	 * Ajoute une dépense.
	 * 
	 * @param nom
	 *            le nom de la dépense
	 * @param montant
	 *            le montant de la dépense
	 * @return true si l'ajout a été effectué false sinon
	 */
	public boolean ajouterDepense(String nom, double montant, String categorie, Compte compte) {

		return _depensesList.add(new Operation(OperationType.DEPENSE, compte, nom, montant, EtatOperation.PREVISION));

	}

	/**
	 * Ajoute une ressource.
	 * 
	 * @param nom_
	 *            le nom de la ressource
	 * @param montant_
	 *            le montant de la ressource
	 * @param compte
	 *            le compte
	 * @return true si l'ajout a été effectué false sinon
	 */
	public boolean ajouterRessource(String nom, double montant, Compte compte) {

		return _ressourcesList
				.add(new Operation(OperationType.RESSOURCE, compte, nom, montant, EtatOperation.PREVISION));

	}

	/**
	 * Ajoute un transfert.
	 * 
	 * @param nom
	 *            le nom de la ressource
	 * @param montant
	 *            le montant de la ressource
	 * @param compteSource
	 *            le compte source
	 * @param compteCible
	 *            le compte cible
	 * @return true si l'ajout a été effectué false sinon
	 */
	public boolean ajouterTransfert(String nom, double montant, Compte compteSource, Compte compteCible) {

		return _transfertList.add(new TransfertOperation(compteSource, nom, montant, compteCible));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(ExerciceMensuel o_) {

		return _dateDebut.compareTo(o_._dateDebut);
	}

	// GETTER'N'SETTER -----------------------

	public Calendar getDateDebut() {
		return _dateDebut;
	}

	public Calendar getDateFin() {
		return _dateFin;
	}

	/**
	 * @return the depensesList
	 */
	public List<Operation> getDepensesList() {
		return _depensesList;
	}

	/**
	 * @return the _ressourcesList
	 */
	public List<Operation> getRessourcesList() {
		return _ressourcesList;
	}

	/**
	 * @return the transfertList
	 */
	public List<TransfertOperation> getTransfertList() {
		return _transfertList;
	}

	public void setDateDebut(Calendar dateDebut) {
		_dateDebut = dateDebut;
	}

	public void setDateFin(Calendar dateFin) {
		_dateFin = dateFin;
	}

	/**
	 * @param depensesList
	 *            the depensesList to set
	 */
	public void setDepensesList(List<Operation> depensesList) {
		_depensesList = depensesList;
	}

	/**
	 * @param _ressourcesList
	 *            the _ressourcesList to set
	 */
	public void setRessourcesList(List<Operation> _ressourcesList) {
		this._ressourcesList = _ressourcesList;
	}

	/**
	 * @param transfertList
	 *            the transfertList to set
	 */
	public void setTransfertList(List<TransfertOperation> transfertList) {
		_transfertList = transfertList;
	}

}
