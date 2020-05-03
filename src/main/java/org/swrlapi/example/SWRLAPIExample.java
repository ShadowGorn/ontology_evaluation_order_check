package org.swrlapi.example;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import java.io.File;
import java.util.Set;

public class SWRLAPIExample
{
  public static void main(String[] args)
  {
    String filename = "ontology/ast_by_marks.owl";
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    File file = new File(classloader.getResource(filename).getFile());

    try {
      // Create an OWL ontology using the OWLAPI
      OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
      OWLOntology ontology = ontologyManager.loadOntologyFromOntologyDocument(file);

      PelletReasoner reasoner = PelletReasonerFactory.getInstance().createReasoner( ontology );

      // http://www.w3.org/2002/07/owl#Thing
      OWLClass Thing = ontologyManager.getOWLDataFactory().getOWLClass(IRI.create("http://www.w3.org/2002/07/owl#Thing"));
      String ontologyIRI = "http://www.semanticweb.org/shadowgorn/ontologies/2020/2/ast_by_marks";
      OWLObjectProperty hasOperand = ontologyManager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontologyIRI + "#has_operand"));
      OWLDataProperty text = ontologyManager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontologyIRI + "#text"));
      OWLDataProperty index = ontologyManager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontologyIRI + "#index"));
      OWLDataProperty step = ontologyManager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontologyIRI + "#step"));

      NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(Thing, false);
      for(Node<OWLNamedIndividual> sameInd : individuals) {
        OWLNamedIndividual ind = sameInd.getRepresentativeElement();

        NodeSet<OWLNamedIndividual> operands = reasoner.getObjectPropertyValues(ind, hasOperand);

        if (operands.isEmpty()) {
          continue;
        }

        Set<OWLLiteral> texts = reasoner.getDataPropertyValues(ind, text);
        Set<OWLLiteral> indexes = reasoner.getDataPropertyValues(ind, index);
        Set<OWLLiteral> steps = reasoner.getDataPropertyValues(ind, step);

        String operatorText = texts.iterator().next().getLiteral();
        String operatorIndex = indexes.iterator().next().getLiteral();
        String operatorStep = steps.iterator().next().getLiteral();

        for(Node<OWLNamedIndividual> sameOpInd : operands) {
          OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();

          Set<OWLLiteral> opTexts = reasoner.getDataPropertyValues(opInd, text);
          Set<OWLLiteral> opIndexes = reasoner.getDataPropertyValues(opInd, index);
          Set<OWLLiteral> opSteps = reasoner.getDataPropertyValues(opInd, step);

          String operandText = opTexts.iterator().next().getLiteral();
          String operandIndex = opIndexes.iterator().next().getLiteral();
          String operandStep = opSteps.iterator().next().getLiteral();

          System.out.println("operation: " + operatorText + " | index: " + operatorIndex + " | step: " + operatorStep);
          System.out.println("operand: " + operandText + " | index: " + operandIndex + " | step: " + operandStep);
          System.out.println("--------------------------");
        }
      }

    } catch (OWLOntologyCreationException e) {
      System.err.println("Error creating OWL ontology: " + e.getMessage());
      System.exit(-1);
    } catch (RuntimeException e) {
      System.err.println("Error starting application: " + e.getMessage());
      System.exit(-1);
    }
  }
}
