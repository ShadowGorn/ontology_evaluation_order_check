package org.swrlapi.example;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;

import java.util.*;

public class OntologyUtil {
    static public OntologyHelper initHelper(List<String> texts) {
        OntologyHelper helper = new OntologyHelper(new Expression(texts));
        return helper;
    }

    static public OntologyHelper initHelper(Expression expression) {
        OntologyHelper helper = new OntologyHelper(expression);
        return helper;
    }

    static public HashMap<Integer, Set<Integer>> getObjectPropertyRelationsByIndex(OntologyHelper helper, String objectProperty) {
        HashMap<Integer, Set<Integer>> relations = new HashMap<>();

        for (OWLNamedIndividual ind : helper.getSortedIndividuals(helper.getIndividuals())) {

            OWLDataProperty dpIndex = helper.getDataProperty("index");
            OWLObjectProperty opProperty = helper.getObjectProperty(objectProperty);

            int index = Integer.parseInt(helper.getDataValue(ind, dpIndex));

            Set<Integer> indIndexes = new HashSet<>();

            for (Node<OWLNamedIndividual> sameOpInd : helper.getReasoner().getObjectPropertyValues(ind, opProperty)) {
                OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();
                int opIndex = Integer.parseInt(helper.getDataValue(opInd, dpIndex));
                indIndexes.add(opIndex);
            }

            relations.put(index, indIndexes);
        }

        return relations;
    }

    /**
     * Not optimal. Check OWL API to find quicker method
     */
    static public boolean checkObjectPropertyExist(OntologyHelper helper, OWLNamedIndividual a, OWLNamedIndividual b, OWLObjectProperty property) {
        for (Node<OWLNamedIndividual> sameOpInd : helper.getReasoner().getObjectPropertyValues(a, property)) {
            OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();
            if (opInd.equals(b)) {
                return true;
            }
        }
        return false;
    }

    static public HashMap<Integer, String> getDataProperties(OntologyHelper helper, String dataProperty) {
        HashMap<Integer, String> properties = new HashMap<>();

        for (OWLNamedIndividual ind : helper.getSortedIndividuals(helper.getIndividuals())) {

            OWLDataProperty dpIndex = helper.getDataProperty("index");

            int index = Integer.parseInt(helper.getDataValue(ind, dpIndex));
            properties.put(index, helper.getDataValue(ind, helper.getDataProperty(dataProperty)));
        }

        return properties;
    }
}
