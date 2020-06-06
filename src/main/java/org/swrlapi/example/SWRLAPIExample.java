package org.swrlapi.example;

import org.semanticweb.owlapi.model.*;

import java.util.Arrays;

public class SWRLAPIExample {
    public static void main(String[] args) {
        OntologyHelper helper = new OntologyHelper(new Expression(Arrays.asList("(", "a", "+", "b", ")", "*", "--", "c")));

        for (OWLNamedIndividual ind : helper.getSortedIndividuals(helper.getIndividuals())) {
            helper.dumpCoreDataProperties(ind);
            helper.dumpObjectProperty(ind,"has_operand");
            System.out.println();
        }
    }
}
