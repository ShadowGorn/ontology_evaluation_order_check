package org.swrlapi.example;

import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class OntologyHelper {
    static final String DEFAULT_FILENAME = "ontology/ast_by_marks.owl";
    static final String DEFAULT_ONTOLOGY_IRI = "http://www.semanticweb.org/shadowgorn/ontologies/2020/2/ast_by_marks";

    public OntologyHelper(List<String> texts) {
        this(DEFAULT_FILENAME, DEFAULT_ONTOLOGY_IRI, texts);
    }

    public OntologyHelper(String ontologyFilename, String ontologyIRI, List<String> texts) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource(ontologyFilename).getFile());
        OntologyIRI = ontologyIRI;

        try {
            OntologyManager = OWLManager.createOWLOntologyManager();
            Ontology = OntologyManager.loadOntologyFromOntologyDocument(file);
            DataFactory = OntologyManager.getOWLDataFactory();

            fillInstances(texts);

            Reasoner = OpenlletReasonerFactory.getInstance().createReasoner(Ontology);
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

    public OWLNamedIndividual addInstance(IRI name) {
        OWLNamedIndividual ind = DataFactory.getOWLNamedIndividual(name);
        OntologyManager.addAxiom(Ontology, DataFactory.getOWLDeclarationAxiom(ind));
        return ind;
    }

    public NodeSet<OWLNamedIndividual> getIndividuals() {
        OWLClassExpression exp = DataFactory.getOWLDataHasValue(getDataProperty("step"), DataFactory.getOWLLiteral(0));
        return Reasoner.getInstances(exp);
    }

    public OWLNamedIndividual addInstance(int index, int step) {
        IRI name = getFullIRI("op-" + String.valueOf(step) + "-" + String.valueOf(index));
        OWLNamedIndividual ind = addInstance(name);

        setDataProperty(getDataProperty("index"), ind, DataFactory.getOWLLiteral(index));
        setDataProperty(getDataProperty("step"), ind, DataFactory.getOWLLiteral(step));
        return ind;
    }

    void setDataProperty(OWLDataProperty dataProperty, OWLNamedIndividual ind, OWLLiteral val) {
        OntologyManager.addAxiom(Ontology, DataFactory.getOWLDataPropertyAssertionAxiom(dataProperty, ind, val));
    }

    void fillInstances(List<String> texts) {
        int size = texts.size();

        ListIterator<String> it = texts.listIterator();
        while (it.hasNext()) {
            OWLNamedIndividual ind = addInstance(it.nextIndex() + 1, 0);
            setDataProperty(getDataProperty("text"), ind, DataFactory.getOWLLiteral(it.next()));

            if (!it.hasNext()) {
                setDataProperty(getDataProperty("last"), ind, DataFactory.getOWLLiteral(true));
            }
        }

        // Create blank individuals for step 1..size
        for (int i = 1; i <= size; ++i) {
            for (int j = 1; j <= size; ++j) {
                addInstance(i, j);
            }
        }
    }

    void dumpDataProperty(OWLNamedIndividual ind, String dataProperty) {
        System.out.println(dataProperty + ": " + getDataValue(ind, getDataProperty(dataProperty)));
    }

    public void dumpInstance(OWLNamedIndividual ind) {
        System.out.println("Individual " + ind.toStringID());
        System.out.println("-");
        List<String> properties = Arrays.asList(
                "text", "index", "step", "last",
                "init", "eval", "app",
                "arity", "associativity", "prefixPostfix", "priority",
                "complexBeginning", "complexEnding",
                "copy", "copyWithoutMarks",
                "hasHighestPriorityToLeft", "hasHighestPriorityToRight",
                "realPos", "studentPos", "isOperand");

        for (String property : properties) {
            dumpDataProperty(ind, property);
        }
    }

    public OWLReasoner getReasoner() {
        return Reasoner;
    }

    public String getDataValue(OWLNamedIndividual ind, OWLDataProperty dataProperty) {
        Set<OWLLiteral> data = Reasoner.getDataPropertyValues(ind, dataProperty);

        if (data.size() > 1) {
            throw new RuntimeException(ind.toStringID() + " has multiple " + dataProperty.toString() + " :" + data.toString());
        }
        if (data.isEmpty()) {
            return "";
        }

        return data.iterator().next().getLiteral();
    }

    IRI getFullIRI(String name) {
        return IRI.create(OntologyIRI + "#" + name);
    }

    String OntologyIRI;
    OWLOntologyManager OntologyManager;
    OWLOntology Ontology;
    OWLDataFactory DataFactory;
    OpenlletReasoner Reasoner;
}
