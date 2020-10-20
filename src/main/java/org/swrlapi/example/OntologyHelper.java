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

    public OntologyHelper(Expression expression) {
        this(DEFAULT_FILENAME, DEFAULT_ONTOLOGY_IRI, expression, new ArrayList<>());
    }

    public OntologyHelper(Expression expression, List<Relation> relations) {
        this(DEFAULT_FILENAME, DEFAULT_ONTOLOGY_IRI, expression, relations);
    }

    public OntologyHelper(String ontologyFilename, String ontologyIRI, Expression expression, List<Relation> relations) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource(ontologyFilename).getFile());
        OntologyIRI = ontologyIRI;

        try {
            OntologyManager = OWLManager.createOWLOntologyManager();
            Ontology = OntologyManager.loadOntologyFromOntologyDocument(file);
            DataFactory = OntologyManager.getOWLDataFactory();

            fillInstances(expression, relations.isEmpty());
            fillRelations(relations);

            Reasoner = OpenlletReasonerFactory.getInstance().createReasoner(Ontology);
            Reasoner.refresh();

            Optional<OWLNamedIndividual> minStepError = findMinErrorStep();
            if (minStepError.isPresent()) {
                setDataProperty(getDataProperty("min_error_step"), minStepError.get(), DataFactory.getOWLLiteral(true));
                Reasoner.refresh();
            }

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

    public OWLNamedIndividual getIndividual(int step, int index) {
        IRI name = getFullIRI("op-" + String.valueOf(step) + "-" + String.valueOf(index));
        return DataFactory.getOWLNamedIndividual(name);
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

    void setObjectProperty(OWLObjectProperty objectProperty, OWLNamedIndividual from, OWLNamedIndividual to) {
        OntologyManager.addAxiom(Ontology, DataFactory.getOWLObjectPropertyAssertionAxiom(objectProperty, from, to));
    }

    void fillInstances(Expression expression, boolean createASTSteps) {
        ListIterator<Term> it = expression.getTerms().listIterator();
        while (it.hasNext()) {
            int index = it.nextIndex();
            Term term = it.next();
            OWLNamedIndividual ind = addInstance( index+ 1, 0);
            setDataProperty(getDataProperty("text"), ind, DataFactory.getOWLLiteral(term.getText()));

            final int INF = 1000000;
            Integer currentStudentPos = term.getStudentPos().orElse(INF);
            setDataProperty(getDataProperty("student_pos"), ind, DataFactory.getOWLLiteral(currentStudentPos));

            if (!it.hasNext()) {
                setDataProperty(getDataProperty("last"), ind, DataFactory.getOWLLiteral(true));
            }

            OWLNamedIndividual errInd = addInstance( index+ 1, -2);
            setDataProperty(getDataProperty("text"), errInd, DataFactory.getOWLLiteral(term.getText()));
            setDataProperty(getDataProperty("student_pos"), errInd, DataFactory.getOWLLiteral(currentStudentPos));
            if (!it.hasNext()) {
                setDataProperty(getDataProperty("last"), errInd, DataFactory.getOWLLiteral(true));
            }
        }

        // Create blank individuals for step 1..size
        for (int i = 1; i <= expression.size(); ++i) {
            for (int j = 1; j <= (createASTSteps ? expression.size() : 1); ++j) {
                addInstance(i, j);
            }
        }
    }

    void fillRelations(List<Relation> relations) {
        for (Relation relation : relations) {
            setObjectProperty(
                    getObjectProperty(relation.getType()),
                    getIndividual(0, relation.getFrom()),
                    getIndividual(0, relation.getTo()));
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

    public Optional<OWLNamedIndividual> findMinErrorStep() {
        final int INF = 1000000;
        int minStep = INF;

        Optional<OWLNamedIndividual> error = Optional.empty();
        for (Node<OWLNamedIndividual> sameInd : getAllIndividuals()) {
            OWLNamedIndividual ind = sameInd.getRepresentativeElement();

            OWLDataProperty dpError = getDataProperty("describe_error");

            if (!getDataValue(ind, dpError).isEmpty()) {
                OWLDataProperty dpStep = getDataProperty("student_pos");
                int step = Integer.parseInt(getDataValue(ind, dpStep));
                if (step < minStep) {
                    minStep = step;
                    error = Optional.of(ind);
                }
            }
        }
        return error;
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
                "arity", "associativity", "prefix_postfix", "precedence",
                "complex_beginning", "complex_ending",
                "copy", "copy_without_marks", "eval_step", "describe_error",
                "has_highest_priority_to_left", "has_highest_priority_to_right",
                "real_pos", "student_pos", "is_operand", "is_function_call",
                "min_error_step", "min_error_step_describe"
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
                "prev_index", "prev_operand", "prev_operation", "same_step", "0_step",
                "student_error_more_priority_left", "student_error_more_priority_right"
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
