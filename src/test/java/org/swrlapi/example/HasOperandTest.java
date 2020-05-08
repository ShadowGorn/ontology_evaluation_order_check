package org.swrlapi.example;

import org.junit.Assert;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;

import java.util.*;

public class HasOperandTest {

    HashMap<Integer, Set<Integer>> getOperands(List<String> texts) {
        HashMap<Integer, Set<Integer>> operandsIndexes = new HashMap<>();
        OntologyHelper helper = new OntologyHelper(texts);

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
        Assert.assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimplePrefixTest() {
        List<String> texts = Arrays.asList("--", "var");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, new HashSet<>(Arrays.asList((2))));
        expOperands.put(2, new HashSet<>(Arrays.asList()));

        Assert.assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimplePostfixTest() {
        List<String> texts = Arrays.asList("var", "--");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, new HashSet<>(Arrays.asList()));
        expOperands.put(2, new HashSet<>(Arrays.asList(1)));

        Assert.assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimpleBinaryTest() {
        List<String> texts = Arrays.asList("var", "+", "var2");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(2, new HashSet<>(Arrays.asList(1, 3)));
        expOperands.put(1, new HashSet<>(Arrays.asList()));
        expOperands.put(3, new HashSet<>(Arrays.asList()));

        Assert.assertEquals(expOperands, realOperands);
    }

    @Test
    public void ComplexTest() {
        List<String> texts = Arrays.asList("(", "var1", "+", "var2", ")", "*", "--", "var3");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, new HashSet<>(Arrays.asList(5)));
        expOperands.put(2, new HashSet<>(Arrays.asList()));
        expOperands.put(3, new HashSet<>(Arrays.asList(2, 4)));
        expOperands.put(4, new HashSet<>(Arrays.asList()));
        expOperands.put(5, new HashSet<>(Arrays.asList()));
        expOperands.put(6, new HashSet<>(Arrays.asList(1, 7)));
        expOperands.put(7, new HashSet<>(Arrays.asList(8)));
        expOperands.put(8, new HashSet<>(Arrays.asList()));

        Assert.assertEquals(expOperands, realOperands);
    }
}