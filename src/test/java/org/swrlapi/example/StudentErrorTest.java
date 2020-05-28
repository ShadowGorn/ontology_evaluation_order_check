package org.swrlapi.example;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.swrlapi.example.OntologyUtil.*;

class StudentErrorTest {

    HashMap<Integer, Set<Integer>> getMorePriorityLeftError(List<String> texts, List<Optional<Integer>> studentPos) {
        OntologyHelper helper = new OntologyHelper(texts, studentPos);
        return getObjectPropertyRelationsByIndex(helper, "student_error_more_priority_left");
    }

    HashMap<Integer, Set<Integer>> getMorePriorityRightError(List<String> texts, List<Optional<Integer>> studentPos) {
        OntologyHelper helper = new OntologyHelper(texts, studentPos);
        return getObjectPropertyRelationsByIndex(helper, "student_error_more_priority_right");
    }

    List<Optional<Integer>> parseStudentPos(List<String> studentPos) {
        List<Optional<Integer>> result = new ArrayList<>();
        for (String strPos : studentPos) {
            if (strPos.isEmpty()) {
                result.add(Optional.empty());
            } else {
                result.add(Optional.of(Integer.parseInt(strPos)));
            }
        }
        return result;
    }

    //@Test
    public void EmptyTest() {
        HashMap<Integer, Set<Integer>> realLeft = getMorePriorityLeftError(new ArrayList<>(), new ArrayList<>());
        HashMap<Integer, Set<Integer>> realRight = getMorePriorityRightError(new ArrayList<>(), new ArrayList<>());

        HashMap<Integer, Set<Integer>> exp = new HashMap<Integer, Set<Integer>>();
        assertEquals(exp, realLeft);
        assertEquals(exp, realRight);
    }

    @Test
    public void SimpleDiffPriorityTest() {
        List<String> texts = Arrays.asList("var1", "+", "var2", "*", "var3");
        List<Optional<Integer>> studentPos = parseStudentPos(Arrays.asList("", "1", "", "2", ""));

        HashMap<Integer, Set<Integer>> realLeft = getMorePriorityLeftError(texts, studentPos);
        HashMap<Integer, Set<Integer>> realRight = getMorePriorityRightError(texts, studentPos);
        HashMap<Integer, Set<Integer>> expLeft = new HashMap<>();
        expLeft.put(1, Set.of());
        expLeft.put(2, Set.of());
        expLeft.put(3, Set.of());
        expLeft.put(4, Set.of());
        expLeft.put(5, Set.of());

        HashMap<Integer, Set<Integer>> expRight = new HashMap<>();
        expRight.put(1, Set.of());
        expRight.put(2, Set.of(4));
        expRight.put(3, Set.of());
        expRight.put(4, Set.of());
        expRight.put(5, Set.of());

        assertEquals(expLeft, realLeft);
        assertEquals(expRight, realRight);
    }

    //@Test
    public void SimpleComplexTest() {
        List<String> texts = Arrays.asList("(", "var1", "+", "var2", ")", "*", "var3");
        List<Optional<Integer>> studentPos = parseStudentPos(Arrays.asList("2", "", "3", "", "", "1", ""));

        HashMap<Integer, Set<Integer>> realLeft = getMorePriorityLeftError(texts, studentPos);
        HashMap<Integer, Set<Integer>> realRight = getMorePriorityRightError(texts, studentPos);
        HashMap<Integer, Set<Integer>> expLeft = new HashMap<>();
        expLeft.put(1, Set.of());
        expLeft.put(2, Set.of());
        expLeft.put(3, Set.of());
        expLeft.put(4, Set.of());
        expLeft.put(5, Set.of());
        expLeft.put(6, Set.of(1));
        expLeft.put(7, Set.of());

        HashMap<Integer, Set<Integer>> expRight = new HashMap<>();
        expRight.put(1, Set.of());
        expRight.put(2, Set.of());
        expRight.put(3, Set.of());
        expRight.put(4, Set.of());
        expRight.put(5, Set.of());
        expRight.put(6, Set.of());
        expRight.put(7, Set.of());

        assertEquals(expLeft, realLeft);
        assertEquals(expRight, realRight);
    }
}