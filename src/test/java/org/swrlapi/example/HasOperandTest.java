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
    public void SimpleComplexTest() {
        List<String> texts = Arrays.asList("(", ")");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2));
        expOperands.put(2, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimpleSqComplexTest() {
        List<String> texts = Arrays.asList("[", "]");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2));
        expOperands.put(2, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void InnerComplexTest() {
        List<String> texts = Arrays.asList("(", "(", ")", ")");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts, true);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2, 4));
        expOperands.put(2, Set.of(3));
        expOperands.put(3, Set.of());
        expOperands.put(4, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void ManyInnerComplexTest() {
        List<String> texts = Arrays.asList("(", "(", "[", "[", "(", ")", "]", "]", ")", ")");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2, 10));
        expOperands.put(2, Set.of(3, 9));
        expOperands.put(3, Set.of(4, 8));
        expOperands.put(4, Set.of(5, 7));
        expOperands.put(5, Set.of(6));
        expOperands.put(6, Set.of());
        expOperands.put(7, Set.of());
        expOperands.put(8, Set.of());
        expOperands.put(9, Set.of());
        expOperands.put(10, Set.of());

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
    public void ManyPrefixTest() {
        List<String> texts = Arrays.asList("--", "--", "--", "var");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2));
        expOperands.put(2, Set.of(3));
        expOperands.put(3, Set.of(4));
        expOperands.put(4, Set.of());

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
    public void ManyPostfixTest() {
        List<String> texts = Arrays.asList("var", "--", "--", "--");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of());
        expOperands.put(2, Set.of(1));
        expOperands.put(3, Set.of(2));
        expOperands.put(4, Set.of(3));

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void PrefixPostfixTest() {
        List<String> texts = Arrays.asList("--", "var", "--");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(3));
        expOperands.put(2, Set.of());
        expOperands.put(3, Set.of(2));

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
    public void LeftAssocBinaryTest() {
        List<String> texts = Arrays.asList("var", "+", "var2", "+", "var3");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(2, Set.of(1, 3));
        expOperands.put(1, Set.of());
        expOperands.put(3, Set.of());
        expOperands.put(4, Set.of(2, 5));
        expOperands.put(5, Set.of());

        assertEquals(expOperands, realOperands);
    }


    @Test
    public void RightAssocBinaryTest() {
        List<String> texts = Arrays.asList("var", "=", "var2", "=", "var3");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of());
        expOperands.put(2, Set.of(1, 4));
        expOperands.put(3, Set.of());
        expOperands.put(4, Set.of(3, 5));
        expOperands.put(5, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimpleDiffPriorityTest() {
        List<String> texts = Arrays.asList("var", "+", "var2", "*", "var3");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of());
        expOperands.put(2, Set.of(1, 4));
        expOperands.put(3, Set.of());
        expOperands.put(4, Set.of(3, 5));
        expOperands.put(5, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimpleInComplexTest() {
        List<String> texts = Arrays.asList("(", "var", ")");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2, 3));
        expOperands.put(2, Set.of());
        expOperands.put(3, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void InComplexTest() {
        List<String> texts = Arrays.asList("(", "var", "+", "var2", ")");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(3, 5));
        expOperands.put(2, Set.of());
        expOperands.put(3, Set.of(2, 4));
        expOperands.put(4, Set.of());
        expOperands.put(5, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void ComplexTest() {
        List<String> texts = Arrays.asList("(", "var1", "+", "var2", ")", "*", "--", "var3");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(3, 5));
        expOperands.put(2, Set.of());
        expOperands.put(3, Set.of(2, 4));
        expOperands.put(4, Set.of());
        expOperands.put(5, Set.of());
        expOperands.put(6, Set.of(1, 7));
        expOperands.put(7, Set.of(8));
        expOperands.put(8, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void InnerComplexComplexTest() {
        List<String> texts = Arrays.asList("var1", "*", "(", "var2", "*", "(", "var3", "+", "var4", ")", ")", "+", "var5");
        HashMap<Integer, Set<Integer>> realOperands = getOperands(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of());
        expOperands.put(2, Set.of(1, 3));
        expOperands.put(3, Set.of(5, 11));
        expOperands.put(4, Set.of());
        expOperands.put(5, Set.of(4, 6));
        expOperands.put(6, Set.of(8, 10));
        expOperands.put(7, Set.of());
        expOperands.put(8, Set.of(7, 9));
        expOperands.put(9, Set.of());
        expOperands.put(10, Set.of());
        expOperands.put(12, Set.of(2, 13));
        expOperands.put(13, Set.of());

        assertEquals(expOperands, realOperands);
    }
}