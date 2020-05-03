package org.swrlapi.example;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;

class OntologyHelper {
    public OntologyHelper(String ontologyFilename, String ontologyIRI) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource(ontologyFilename).getFile());
        OntologyIRI = ontologyIRI;

        try {
            OntologyManager = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = OntologyManager.loadOntologyFromOntologyDocument(file);

            Reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
        } catch (OWLOntologyCreationException e) {
            System.err.println("Error creating OWL ontology: " + e.getMessage());
            System.exit(-1);
        }
    }

    public OWLClass getThingClass() {
        final String OWL_THING_IRI = "http://www.w3.org/2002/07/owl#Thing";
        return OntologyManager.getOWLDataFactory().getOWLClass(IRI.create(OWL_THING_IRI));
    }

    public OWLObjectProperty getObjectProperty(String propertyName) {
        return OntologyManager.getOWLDataFactory().getOWLObjectProperty(getFullIRI(propertyName));
    }

    public OWLDataProperty getDataProperty(String propertyName) {
        return OntologyManager.getOWLDataFactory().getOWLDataProperty(getFullIRI(propertyName));
    }

    public OWLReasoner getReasoner() {
        return Reasoner;
    }

    public String getDataValue(OWLNamedIndividual ind, OWLDataProperty dataProperty) {
        Set<OWLLiteral> data = Reasoner.getDataPropertyValues(ind, dataProperty);
        assert data.size() <= 1;

        if (data.isEmpty()) {
            return "";
        }

        return data.toString();
    }

    IRI getFullIRI(String name) {
        return IRI.create(OntologyIRI + "#" + name);
    }

    String OntologyIRI;
    OWLOntologyManager OntologyManager;
    PelletReasoner Reasoner;
}

public class SWRLAPIExample {
    public static void main(String[] args) {
        String filename = "ontology/ast_by_marks.owl";
        String ontologyIRI = "http://www.semanticweb.org/shadowgorn/ontologies/2020/2/ast_by_marks";
        OntologyHelper helper = new OntologyHelper(filename, ontologyIRI);

        OWLClass Thing = helper.getThingClass();
        OWLObjectProperty hasOperand = helper.getObjectProperty("has_operand");
        OWLObjectProperty inComplex = helper.getObjectProperty("in_complex");
        OWLObjectProperty complexBoundaries = helper.getObjectProperty("complex_boundaries");
        OWLObjectProperty nextIndex = helper.getObjectProperty("next_index");

        OWLDataProperty text = helper.getDataProperty("text");
        OWLDataProperty index = helper.getDataProperty("index");
        OWLDataProperty step = helper.getDataProperty("step");
        OWLDataProperty init = helper.getDataProperty("init");
        OWLDataProperty eval = helper.getDataProperty("eval");
        OWLDataProperty app = helper.getDataProperty("app");
        OWLDataProperty arity = helper.getDataProperty("arity");
        OWLDataProperty associativity = helper.getDataProperty("associativity");
        OWLDataProperty complexBeginning = helper.getDataProperty("complex_beginning");
        OWLDataProperty complexEnding = helper.getDataProperty("complex_ending");
        OWLDataProperty copy = helper.getDataProperty("copy");
        OWLDataProperty copyWithoutMarks = helper.getDataProperty("copy_without_marks");
        OWLDataProperty hasHighestPriorityToLeft = helper.getDataProperty("has_highest_priority_to_left");
        OWLDataProperty hasHighestPriorityToRight = helper.getDataProperty("has_highest_priority_to_right");
        OWLDataProperty isOperand = helper.getDataProperty("is_operand");
        OWLDataProperty last = helper.getDataProperty("last");
        OWLDataProperty prefixPostfix = helper.getDataProperty("prefix_postfix");
        OWLDataProperty priority = helper.getDataProperty("priority");
        OWLDataProperty realPos = helper.getDataProperty("real_pos");
        OWLDataProperty studentPos = helper.getDataProperty("student_pos");

        OWLReasoner reasoner = helper.getReasoner();

        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(Thing, true);
        for (Node<OWLNamedIndividual> sameInd : individuals) {
            OWLNamedIndividual ind = sameInd.getRepresentativeElement();

            System.out.println("Individual " + ind.toStringID());
            System.out.println("-");
            System.out.println("text: " + helper.getDataValue(ind, text));
            System.out.println("index: " + helper.getDataValue(ind, index));
            System.out.println("step: " + helper.getDataValue(ind, step));
            System.out.println("init: " + helper.getDataValue(ind, init));
            System.out.println("eval: " + helper.getDataValue(ind, eval));
            System.out.println("app: " + helper.getDataValue(ind, app));
            System.out.println("arity: " + helper.getDataValue(ind, arity));
            System.out.println("associativity: " + helper.getDataValue(ind, associativity));
            System.out.println("complexBeginning: " + helper.getDataValue(ind, complexBeginning));
            System.out.println("complexEnding: " + helper.getDataValue(ind, complexEnding));
            System.out.println("copyWithoutMarks: " + helper.getDataValue(ind, copyWithoutMarks));
            System.out.println("copy: " + helper.getDataValue(ind, copy));
            System.out.println("hasHighestPriorityToLeft: " + helper.getDataValue(ind, hasHighestPriorityToLeft));
            System.out.println("hasHighestPriorityToRight: " + helper.getDataValue(ind, hasHighestPriorityToRight));
            System.out.println("isOperand: " + helper.getDataValue(ind, isOperand));
            System.out.println("last: " + helper.getDataValue(ind, last));
            System.out.println("prefixPostfix: " + helper.getDataValue(ind, prefixPostfix));
            System.out.println("priority: " + helper.getDataValue(ind, priority));
            System.out.println("realPos: " + helper.getDataValue(ind, realPos));
            System.out.println("studentPos: " + helper.getDataValue(ind, studentPos));
            System.out.println("-----");

            for (Node<OWLNamedIndividual> sameOpInd : reasoner.getObjectPropertyValues(ind, inComplex)) {
                OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();
                System.out.println("inComplex " + opInd.toStringID());
            }
            System.out.println("-----");
            for (Node<OWLNamedIndividual> sameOpInd : reasoner.getObjectPropertyValues(ind, complexBoundaries)) {
                OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();
                System.out.println("complexBoundaries " + opInd.toStringID());
            }
            System.out.println("-----");
            for (Node<OWLNamedIndividual> sameOpInd : reasoner.getObjectPropertyValues(ind, nextIndex)) {
                OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();
                System.out.println("nextIndex " + opInd.toStringID());
            }
            System.out.println("-----");

            System.out.println();
        }
    }
}
