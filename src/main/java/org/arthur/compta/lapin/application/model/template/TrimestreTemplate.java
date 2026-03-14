package org.arthur.compta.lapin.application.model.template;

import java.util.ArrayList;
import java.util.List;

/**
 * Template d'un trimestre
 *
 */
public class TrimestreTemplate {

/** Template des opérations */
private final List<TrimestreTemplateElement> _elementList;

/** Constructeur */
public TrimestreTemplate() {
_elementList = new ArrayList<>();
}

/**
 * Ajoute un élément dans le template
 */
public void addElement(TrimestreTemplateElement elt) {
_elementList.add(elt);
}

/**
 * Retourne la liste des éléments du template
 */
public List<TrimestreTemplateElement> getElements() {
return _elementList;
}

}
