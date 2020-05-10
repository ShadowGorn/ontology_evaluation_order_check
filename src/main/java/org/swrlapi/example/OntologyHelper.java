package org.swrlapi.example;

import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.io.File;
import java.util.*;

public class OntologyHelper {
    static final String DEFAULT_FILENAME = "ontology/ast_by_marks.owl";
    static final String DEFAULT_ONTOLOGY_IRI = "http://www.semanticweb.org/shadowgorn/ontologies/2020/2/ast_by_marks";

    public OntologyHelper(List<String> texts) {
        this(DEFAULT_FILENAME, DEFAULT_ONTOLOGY_IRI, texts, new ArrayList<>());
    }

    public OntologyHelper(List<String> texts, List<Optional<Integer>> studentPos) {
        this(DEFAULT_FILENAME, DEFAULT_ONTOLOGY_IRI, texts, studentPos);
    }

    public OntologyHelper(String ontologyFilename, String ontologyIRI, List<String> texts, List<Optional<Integer>> studentPos) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource(ontologyFilename).getFile());
        OntologyIRI = ontologyIRI;

        while(studentPos.size() < texts.size()) {
            studentPos.add(Optional.empty());
        }

        try {
            OntologyManager = OWLManager.createOWLOntologyManager();
            Ontology = OntologyManager.loadOntologyFromOntologyDocument(file);
            DataFactory = OntologyManager.getOWLDataFactory();

            fillInstances(texts, studentPos);

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

    public NodeSet<OWLNamedIndividual> getAllIndividuals() {
        return Reasoner.getInstances(getThingClass());
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

    void fillInstances(List<String> texts, List<Optional<Integer>> studentPos) {
        int size = texts.size();

        ListIterator<String> it = texts.listIterator();
        while (it.hasNext()) {
            int index = it.nextIndex();
            OWLNamedIndividual ind = addInstance( index+ 1, 0);
            setDataProperty(getDataProperty("text"), ind, DataFactory.getOWLLiteral(it.next()));

            Optional<Integer> currentStudentPos = studentPos.get(index);
            currentStudentPos.ifPresent(integer -> setDataProperty(getDataProperty("student_pos"), ind, DataFactory.getOWLLiteral(integer)));

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

    public void dump(boolean all) {
        NodeSet<OWLNamedIndividual> inds = all ? getAllIndividuals() : getIndividuals();

        for (OWLNamedIndividual ind : getSortedIndividuals(inds)) {
            dumpDataProperties(ind);
            dumpObjectProperties(ind);
            System.out.println();
        }
    }

    public List<OWLNamedIndividual> getSortedIndividuals(NodeSet<OWLNamedIndividual> inds) {
        ArrayList<OWLNamedIndividual> sortedInds = new ArrayList<>();
        for (Node<OWLNamedIndividual> sameInd : inds) {
            OWLNamedIndividual ind = sameInd.getRepresentativeElement();
            sortedInds.add(ind);
        }
        sortedInds.sort(Comparator
                .comparing((OWLNamedIndividual cInd) -> {
                    return Integer.parseInt(getDataValue(cInd, getDataProperty("step")));
                })
                .thenComparing((OWLNamedIndividual cInd) -> {
                    return Integer.parseInt(getDataValue(cInd, getDataProperty("index")));
                })
        );
        return sortedInds;
    }

    void dumpDataProperty(OWLNamedIndividual ind, String dataProperty) {
        System.out.println(dataProperty + ": " + getDataValue(ind, getDataProperty(dataProperty)));
    }

    public void dumpDataProperties(OWLNamedIndividual ind) {
        dumpCoreDataProperties(ind);
        List<String> properties = Arrays.asList(
                "last",
                "init", "eval", "app",
                "arity", "associativity", "prefix_postfix", "priority",
                "complex_beginning", "complex_ending",
                "copy", "copy_without_marks",
                "has_highest_priority_to_left", "has_highest_priority_to_right",
                "real_pos", "student_pos", "is_operand", "is_function_call"
        );

        for (String property : properties) {
            dumpDataProperty(ind, property);
        }
    }

    public void dumpCoreDataProperties(OWLNamedIndividual ind) {
        System.out.println("Individual " + ind.toStringID());
        List<String> properties = Arrays.asList("text", "index", "step");

        for (String property : properties) {
            dumpDataProperty(ind, property);
        }
    }

    public void dumpObjectProperty(OWLNamedIndividual ind, String property) {
        System.out.println("Object property: " + property);
        NodeSet<OWLNamedIndividual> indNodeSet = Reasoner.getObjectPropertyValues(ind, getObjectProperty(property));
        for (OWLNamedIndividual pInd : getSortedIndividuals(indNodeSet)) {
            dumpCoreDataProperties(pInd);
        }
        System.out.println("-----");
    }

    public void dumpObjectProperties(OWLNamedIndividual ind) {
        List<String> properties = Arrays.asList(
                "all_app_to_left", "all_app_to_right", "all_eval_to_right",
                "before", "complex_boundaries", "find_left_operand", "find_right_operand",
                "has_operand", "high_priority", "in_complex", "more_priority_left_by_step",
                "more_priority_right_by_step", "next_index", "next_step", "not_index", "operation_time",
                "prev_index", "prev_operand", "prev_operation", "same_step", "0_step"
        );

        for (String property : properties) {
            dumpObjectProperty(ind, property);
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
