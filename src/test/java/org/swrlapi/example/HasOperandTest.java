package org.swrlapi.example;

import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.util.*;

public class HasOperandTest {

    HashMap<Integer, Set<Integer>> getOperands(List<String> texts) {
        return getOperands(texts, false);
    }

    HashMap<Integer, Set<Integer>> getOperands(List<String> texts, boolean dump) {
        HashMap<Integer, Set<Integer>> operandsIndexes = new HashMap<>();
        OntologyHelper helper = new OntologyHelper(texts);

        if (dump) {
            helper.dump(true);
        }

        for (Node<OWLNamedIndividual> sameInd : helper.getIndividuals()) {
            OWLNamedIndividual ind = sameInd.getRepresentativeElement();

            OWLDataProperty dpIndex = helper.getDataProperty("index");
            OWLObjectProperty opHasOperand = helper.getObjectProperty("has_operand");

            int index = Integer.parseInt(helper.getDataValue(ind, dpIndex));

            Set<Integer> opIndexes = new HashSet<>();

            for (Node<OWLNamedIndividual> sameOpInd : helper.getReasoner().getObjectPropertyValues(ind, opHasOperand)) {
                OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();
                int opIndex = Integer.parseInt(helper.getDataValue(opInd, dpIndex));
                opIndexes.add(opIndex);
            }

            operandsIndexes.put(index, opIndexes);
        }

        return operandsIndexes;
    }

    @Test
    public void EmptyTest() {
        HashMap<Integer, Set<Integer>> realOperands = getOperands(new ArrayList<>());
        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimplePrefixTest() {
        List<String> texts = Arrays.asList("--", "var");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2));
        expOperands.put(2, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimplePostfixTest() {
        List<String> texts = Arrays.asList("var", "--");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of());
        expOperands.put(2, Set.of(1));

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimpleBinaryTest() {
        List<String> texts = Arrays.asList("var", "+", "var2");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(2, Set.of(1, 3));
        expOperands.put(1, Set.of());
        expOperands.put(3, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void ComplexTest() {
        List<String> texts = Arrays.asList("(", "var1", "+", "var2", ")", "*", "--", "var3");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(5));
        expOperands.put(2, Set.of());
        expOperands.put(3, Set.of(2, 4));
        expOperands.put(4, Set.of());
        expOperands.put(5, Set.of());
        expOperands.put(6, Set.of(1, 7));
        expOperands.put(7, Set.of(8));
        expOperands.put(8, Set.of());

        assertEquals(expOperands, realOperands);
    }
}