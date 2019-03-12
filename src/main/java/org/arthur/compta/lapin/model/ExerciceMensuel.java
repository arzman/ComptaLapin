package org.arthur.compta.lapin.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Exercice comptable sur 1 mois. Regroupe toute les opérations sur un mois.
 * 
 */
public class ExerciceMensuel implements Comparable<ExerciceMensuel> {

	/** id */
	private int _id;
	/** Liste des opération durant l'exercice. */
	private List<Integer> _operationList;
	/** Date de début de l'exercice */
	private LocalDate _dateDebut;
	/** Date de fin de l'exercice */
	private LocalDate _dateFin;
	/** Le résultat prévisionnel à la création */
	private double _resPrev;

	/**
	 * 
	 * Constructeur par d�faut
	 */
	public ExerciceMensuel(int id, LocalDate dateDebut, LocalDate dateFin, double resPrev) {

		_operationList = new ArrayList<Integer>();

		_id = id;
		_dateDebut = dateDebut;
		_dateFin = dateFin;
		_resPrev = resPrev;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(ExerciceMensuel o_) {

		return _dateDebut.compareTo(o_._dateDebut);
	}

	// GETTER'N'SETTER -----------------------

	public int getId() {
		return _id;
	}

	public LocalDate getDateDebut() {
		return _dateDebut;
	}

	public LocalDate getDateFin() {
		return _dateFin;
	}

	/**
	 * @return the depensesList
	 */
	public List<Integer> getOperationList() {
		return _operationList;
	}

	/**
	 * Retourne le résultat pévisionnel
	 * 
	 * @return
	 */
	public double getResultatPrev() {
		return _resPrev;
	}

}
