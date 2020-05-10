package org.swrlapi.example;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.swrlapi.example.OntologyUtil.checkObjectPropertyExist;

class StudentErrorTest {
    class StudentError {
        int BeforePos;
        int AfterPos;
        String BeforeText;
        String AfterText;
        List<Integer> BeforePropertiesSequence;

        @Override
        public String toString() {
            return "StudentError{" +
                    "BeforePos=" + BeforePos +
                    ", AfterPos=" + AfterPos +
                    ", BeforeText='" + BeforeText + '\'' +
                    ", AfterText='" + AfterText + '\'' +
                    ", BeforePropertiesSequence=" + BeforePropertiesSequence +
                    '}';
        }

        StudentError(int beforePos, int afterPos, String beforeText, String afterText, List<Integer> beforePropertiesSequence) {
            BeforePos = beforePos;
            AfterPos = afterPos;
            BeforeText = beforeText;
            AfterText = afterText;
            BeforePropertiesSequence = beforePropertiesSequence;
        }

        StudentError(OntologyHelper helper, OWLNamedIndividual before, OWLNamedIndividual after) {
            OWLDataProperty dpIndex = helper.getDataProperty("index");
            OWLDataProperty dpText = helper.getDataProperty("text");

            BeforePos = Integer.parseInt(helper.getDataValue(before, dpIndex));
            BeforeText = helper.getDataValue(before, dpText);
            AfterPos = Integer.parseInt(helper.getDataValue(after, dpIndex));
            AfterText = helper.getDataValue(after, dpText);

            BeforePropertiesSequence = new ArrayList<>();
            fillBeforePropertiesSequence(helper, before, after);
        }

        void fillBeforePropertiesSequence(OntologyHelper helper, OWLNamedIndividual before, OWLNamedIndividual after) {
            OWLDataProperty dpIndex = helper.getDataProperty("index");

            OWLNamedIndividual step = before;
            while (step != after) {
                BeforePropertiesSequence.add(Integer.parseInt(helper.getDataValue(step, dpIndex)));
                OWLNamedIndividual nextStep = findDirectBeforeStep(helper, step, after);
                if (step.equals(nextStep)) {
                    throw new RuntimeException("Cannot find before sequence by before_direct");
                }
                step = nextStep;
            }
            BeforePropertiesSequence.add(Integer.parseInt(helper.getDataValue(step, dpIndex)));
        }

        OWLNamedIndividual findDirectBeforeStep(OntologyHelper helper, OWLNamedIndividual before, OWLNamedIndividual after) {
            for (Node<OWLNamedIndividual> sameOpInd : helper.getReasoner().getObjectPropertyValues(before, helper.getObjectProperty("before_direct"))) {
                OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();
                if (opInd.equals(after) || checkObjectPropertyExist(helper, opInd, after, helper.getObjectProperty("before"))) {
                    return opInd;
                }
            }
            return before;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StudentError that = (StudentError) o;
            return BeforePos == that.BeforePos &&
                    AfterPos == that.AfterPos &&
                    BeforeText.equals(that.BeforeText) &&
                    AfterText.equals(that.AfterText) &&
                    BeforePropertiesSequence.equals(that.BeforePropertiesSequence);
        }

        @Override
        public int hashCode() {
            return Objects.hash(BeforePos, AfterPos, BeforeText, AfterText);
        }
    }

    Set<StudentError> getError(List<String> texts, List<Optional<Integer>> studentPos) {
        Set<StudentError> errors = new HashSet<>();
        OntologyHelper helper = new OntologyHelper(texts, studentPos);
        for (OWLNamedIndividual ind : helper.getSortedIndividuals(helper.getIndividuals())) {
            OWLObjectProperty opProperty = helper.getObjectProperty("student_error");

            for (Node<OWLNamedIndividual> sameOpInd : helper.getReasoner().getObjectPropertyValues(ind, opProperty)) {
                OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();
                errors.add(new StudentError(helper, ind, opInd));
            }
        }
        return errors;
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

    @Test
    public void EmptyTest() {
        Set<StudentError> real = getError(new ArrayList<>(), new ArrayList<>());
        Set<StudentError> exp = new HashSet<>();
        assertEquals(exp, real);
    }

    @Test
    public void SimpleDiffPriorityTest() {
        List<String> texts = Arrays.asList("var1", "+", "var2", "*", "var3");
        List<Optional<Integer>> studentPos = parseStudentPos(Arrays.asList("", "1", "", "2", ""));

        Set<StudentError> real = getError(texts, studentPos);
        Set<StudentError> exp = new HashSet<>();
        exp.add(new StudentError(4, 2, "*", "+", Arrays.asList(4, 2)));
        assertEquals(exp, real);
    }

    @Test
    public void SimpleNotNextStudentPosTest() {
        List<String> texts = Arrays.asList("var1", "+", "var2", "*", "var3");
        List<Optional<Integer>> studentPos = parseStudentPos(Arrays.asList("", "2", "", "1", "3"));

        Set<StudentError> real = getError(texts, studentPos);
        Set<StudentError> exp = new HashSet<>();
        exp.add(new StudentError(5, 4, "var3", "*", Arrays.asList(5, 4)));
        exp.add(new StudentError(5, 2, "var3", "+", Arrays.asList(5, 4, 2)));
        assertEquals(exp, real);
    }

    @Test
    public void SimpleComplexTest() {
        List<String> texts = Arrays.asList("(", "var1", "+", "var2", ")", "*", "var3");
        List<Optional<Integer>> studentPos = parseStudentPos(Arrays.asList("2", "", "3", "", "", "1", ""));

        Set<StudentError> real = getError(texts, studentPos);
        Set<StudentError> exp = new HashSet<>();
        exp.add(new StudentError(1, 6, "(", "*", Arrays.asList(1, 6)));
        exp.add(new StudentError(3, 1, "+", "(", Arrays.asList(3, 1)));
        exp.add(new StudentError(3, 6, "+", "*", Arrays.asList(3, 1, 6)));
        assertEquals(exp, real);
    }
}