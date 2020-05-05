package org.swrlapi.example;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.Arrays;

public class SWRLAPIExample {
    public static void main(String[] args) {
        OntologyHelper helper = new OntologyHelper(Arrays.asList("(", "a", "+", "b", ")", "*", "--", "c"));

        OWLReasoner reasoner = helper.getReasoner();
        reasoner.flush();

        OWLObjectProperty hasOperand = helper.getObjectProperty("has_operand");
        OWLDataProperty text = helper.getDataProperty("text");

        NodeSet<OWLNamedIndividual> individuals = helper.getIndividuals();
        for (Node<OWLNamedIndividual> sameInd : individuals) {
            OWLNamedIndividual ind = sameInd.getRepresentativeElement();

            System.out.println("Individual " + ind.toString());
            System.out.println("text: " + helper.getDataValue(ind, text));
            for (Node<OWLNamedIndividual> sameOpInd : reasoner.getObjectPropertyValues(ind, hasOperand)) {
                OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();
                System.out.println("hasOperand " + opInd.toString());
            }
            System.out.println("-----");

            System.out.println();
        }
    }
}
