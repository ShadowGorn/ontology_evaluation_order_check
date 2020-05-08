package org.swrlapi.example;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.Arrays;

public class SWRLAPIExample {
    public static void main(String[] args) {
        OntologyHelper helper = new OntologyHelper(Arrays.asList("(", "a", "+", "b", ")", "*", "--", "c"));

        for (OWLNamedIndividual ind : helper.getSortedIndividuals(helper.getIndividuals())) {
            helper.dumpCoreDataProperties(ind);
            helper.dumpObjectProperty(ind,"has_operand");
            System.out.println();
        }
    }
}
