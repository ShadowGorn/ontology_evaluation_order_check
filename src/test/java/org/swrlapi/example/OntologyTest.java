package org.swrlapi.example;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OntologyTest {
    OntologyHelper initHelper(List<String> texts) {
        OntologyHelper helper = new OntologyHelper(texts);
        return helper;
    }

    HashMap<Integer, Set<Integer>> getObjectPropertyRelationsByIndex(OntologyHelper helper, String objectProperty) {
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

    HashMap<Integer, String> getDataProperties(OntologyHelper helper, String dataProperty) {
        HashMap<Integer, String> properties = new HashMap<>();

        for (OWLNamedIndividual ind : helper.getSortedIndividuals(helper.getIndividuals())) {

            OWLDataProperty dpIndex = helper.getDataProperty("index");

            int index = Integer.parseInt(helper.getDataValue(ind, dpIndex));
            properties.put(index, helper.getDataValue(ind, helper.getDataProperty(dataProperty)));
        }

        return properties;
    }

    @Test
    public void SimpleTest() {
        OntologyHelper helper = initHelper(Arrays.asList("var1", "+", "var2"));

        {
            HashMap<Integer, Set<Integer>> realOperands = getObjectPropertyRelationsByIndex(helper, "next_index");
            HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
            expOperands.put(1, Set.of(2));
            expOperands.put(2, Set.of(3));
            expOperands.put(3, Set.of());

            assertEquals(expOperands, realOperands);
        }
        {
            HashMap<Integer, Set<Integer>> realOperands = getObjectPropertyRelationsByIndex(helper, "next_step");
            HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
            expOperands.put(1, Set.of(1));
            expOperands.put(2, Set.of(2));
            expOperands.put(3, Set.of(3));

            assertEquals(expOperands, realOperands);
        }
        {
            HashMap<Integer, Set<Integer>> realOperands = getObjectPropertyRelationsByIndex(helper, "same_step");
            HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
            expOperands.put(1, Set.of(1, 2, 3));
            expOperands.put(2, Set.of(1, 2, 3));
            expOperands.put(3, Set.of(1, 2, 3));

            assertEquals(expOperands, realOperands);
        }
        {
            HashMap<Integer, Set<Integer>> realOperands = getObjectPropertyRelationsByIndex(helper, "not_index");
            HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
            expOperands.put(1, Set.of(2, 3));
            expOperands.put(2, Set.of(1, 3));
            expOperands.put(3, Set.of(1, 2));

            assertEquals(expOperands, realOperands);
        }
        {
            HashMap<Integer, Set<Integer>> realOperands = getObjectPropertyRelationsByIndex(helper, "prev_operand");
            HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
            expOperands.put(1, Set.of());
            expOperands.put(2, Set.of(1));
            expOperands.put(3, Set.of());

            assertEquals(expOperands, realOperands);
        }
        {
            HashMap<Integer, Set<Integer>> realOperands = getObjectPropertyRelationsByIndex(helper, "prev_operation");
            HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
            expOperands.put(1, Set.of(1));
            expOperands.put(2, Set.of());
            expOperands.put(3, Set.of(2));

            assertEquals(expOperands, realOperands);
        }
        {
            HashMap<Integer, String> realOperands = getDataProperties(helper, "is_operand");
            HashMap<Integer, String> expOperands = new HashMap<>();
            expOperands.put(1, "true");
            expOperands.put(2, "");
            expOperands.put(3, "true");

            assertEquals(expOperands, realOperands);
        }
    }
}
