package org.swrlapi.example;

import openllet.core.utils.Pair;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.swrlapi.example.OntologyUtil.getObjectPropertyRelationsByIndex;
import static org.swrlapi.example.OntologyUtil.initHelper;

class StudentErrorTest {
    class StudentError {
        int BeforePos;
        int AfterPos;
        String BeforeText;
        String AfterText;
        List<Integer> BeforePropertiesSequence;

        StudentError(int beforePos, int afterPos, String beforeText, String afterText) {
            BeforePos = beforePos;
            AfterPos = afterPos;
            BeforeText = beforeText;
            AfterText = afterText;
        }

        StudentError(OntologyHelper helper, OWLNamedIndividual before, OWLNamedIndividual after) {
            OWLDataProperty dpIndex = helper.getDataProperty("index");
            OWLDataProperty dpText = helper.getDataProperty("text");

            BeforePos = Integer.parseInt(helper.getDataValue(before, dpIndex));
            BeforeText = helper.getDataValue(before, dpText);
            AfterPos = Integer.parseInt(helper.getDataValue(after, dpIndex));
            AfterText = helper.getDataValue(after, dpText);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StudentError that = (StudentError) o;
            return BeforePos == that.BeforePos &&
                    AfterPos == that.AfterPos &&
                    Objects.equals(BeforeText, that.BeforeText) &&
                    Objects.equals(AfterText, that.AfterText);
        }

        @Override
        public int hashCode() {
            return Objects.hash(BeforePos, AfterPos, BeforeText, AfterText);
        }
    }

    List<StudentError> getError(List<String> texts, List<Optional<Integer>> studentPos) {
        List<StudentError> errors = new ArrayList<>();
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

    @Test
    public void EmptyTest() {
        List<StudentError> real = getError(new ArrayList<>(), new ArrayList<>());
        List<StudentError> exp = new ArrayList<>();
        assertEquals(exp, real);
    }

    @Test
    public void SimpleDiffPriorityTest() {
        List<String> texts = Arrays.asList("var1", "+", "var2", "*", "var3");
        List<Optional<Integer>> studentPos = new ArrayList<>();
        studentPos.add(Optional.empty());
        studentPos.add(Optional.of(1));
        studentPos.add(Optional.empty());
        studentPos.add(Optional.of(2));
        studentPos.add(Optional.empty());

        List<StudentError> real = getError(texts, studentPos);
        List<StudentError> exp = new ArrayList<>();
        exp.add(new StudentError(4, 2, "*", "+"));
        assertEquals(exp, real);
    }
}