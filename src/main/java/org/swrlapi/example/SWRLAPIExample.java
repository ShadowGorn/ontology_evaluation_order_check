package org.swrlapi.example;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class SWRLAPIExample extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Evaluation order faults description");

        VBox root = new VBox();
        Scene scene = new Scene(root,300,300);
        stage.setScene(scene);

        HBox inputPane = new HBox();

        Label inputLabel = new Label("Input expression, split tokens with a space");
        TextField input = new TextField();
        Button prepareButton = new Button("Prepare");
        inputPane.getChildren().addAll(input, prepareButton);

        HBox buttonsPaneStub = new HBox();
        VBox errorsPaneStub = new VBox();
        root.getChildren().addAll(inputLabel, inputPane, buttonsPaneStub, errorsPaneStub);
        final Integer BUTTONS_INDEX = 2;
        final Integer ERRORS_INDEX = 3;

        AtomicReference<Expression> expr = new AtomicReference<>();
        AtomicReference<List<Relation>> relations = new AtomicReference<>();
        AtomicReference<Integer> lastSetPos = new AtomicReference<>();
        AtomicReference<Boolean> lastPosIsError = new AtomicReference<>();

        prepareButton.setOnAction(e -> {
            expr.set(new Expression(Arrays.asList(input.getText().split(" "))));
            lastSetPos.set(0);
            lastPosIsError.set(false);
            root.getChildren().set(BUTTONS_INDEX, new HBox());
            Set<Integer> operandsPos = getOperandPositions(expr.get());

            GridPane evaluationButtons = new GridPane();
            int pos = 0;
            for (String token : input.getText().split(" ")) {
                Button tokenButton = new Button(token);
                AtomicReference<Integer> tokenPos = new AtomicReference<>();
                tokenPos.set(pos);
                expr.get().getTerms().get(tokenPos.get()).setStudentPos(1000);

                if (operandsPos.contains(pos)) {
                    tokenButton.setDisable(true);
                } else {
                    tokenButton.setOnAction(fe -> {
                        root.getChildren().set(ERRORS_INDEX, new FlowPane());
                        expr.get().getTerms().get(tokenPos.get()).setStudentPos(lastSetPos.get());
                        Set<StudentError> errors = GetErrors(expr.get(), false);
                        if (errors.isEmpty()) {
                            lastSetPos.set(lastSetPos.get() + 1);
                            tokenButton.setDisable(true);
                        } else {
                            VBox errorsPane = new VBox();
                            for (StudentError error : errors) {
                                errorsPane.getChildren().add(new Label(
                                        "Error: " + error.Type.toString() +
                                                " on pos " + error.ErrorPos +
                                                " cause pos " + error.ReasonPos));
                            }
                            root.getChildren().set(ERRORS_INDEX, errorsPane);
                            expr.get().getTerms().get(tokenPos.get()).setStudentPos(1000);
                        }
                    });
                }
                evaluationButtons.add(new Label(String.valueOf(pos + 1)), pos, 0);
                evaluationButtons.add(tokenButton, pos, 1);
                pos++;
            }
            root.getChildren().set(BUTTONS_INDEX, evaluationButtons);
        });

        stage.show();
    }

    List<Relation> GetRelations(OntologyHelper helper) {
        List<Relation> relations = new ArrayList<>();
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "before_direct"), "before_direct");
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "before_by_third_operator"), "before_by_third_operator");
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "before_third_operator"), "before_third_operator");
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "before_as_operand"), "before_as_operand");
        return relations;
    }

    void AddToRelations(List<Relation> relations, HashMap<Integer, Set<Integer>> props, String type) {
        for (Map.Entry<Integer, Set<Integer>> kv : props.entrySet()) {
            for (Integer to : kv.getValue()) {
                relations.add(new Relation(kv.getKey(), to, type));
            }
        }
    }

    Set<StudentError> GetErrors(Expression expression, boolean debug) {
        Set<StudentError> resultErrors = new HashSet<>();
        OntologyHelper helper = new OntologyHelper(expression);
        if (debug) {
            helper.dump(false);
        }
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_more_priority_left"),
                StudentErrorType.HIGH_PRIORITY_TO_LEFT);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_more_priority_right"),
                StudentErrorType.HIGH_PRIORITY_TO_RIGHT);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_left_assoc"),
                StudentErrorType.LEFT_ASSOC_TO_LEFT);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_right_assoc"),
                StudentErrorType.RIGHT_ASSOC_TO_RIGHT);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_in_complex"),
                StudentErrorType.IN_COMPLEX);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_strict_operands_order"),
                StudentErrorType.STRICT_OPERANDS_ORDER);
        return resultErrors;
    }

    Set<Integer> getOperandPositions(Expression expression) {
        Set<Integer> result = new HashSet<>();
        OntologyHelper helper = new OntologyHelper(expression);

        HashMap<Integer, String> props = getDataProperties(helper, "is_operand");
        for (Map.Entry<Integer, String> kv : props.entrySet()) {
            if (!kv.getValue().isEmpty()) {
                result.add(kv.getKey() - 1);
            }
        }
        return result;
    }

    static public HashMap<Integer, String> getDataProperties(OntologyHelper helper, String dataProperty) {
        HashMap<Integer, String> properties = new HashMap<>();

        for (OWLNamedIndividual ind : helper.getSortedIndividuals(helper.getIndividuals())) {

            OWLDataProperty dpIndex = helper.getDataProperty("index");

            int index = Integer.parseInt(helper.getDataValue(ind, dpIndex));
            properties.put(index, helper.getDataValue(ind, helper.getDataProperty(dataProperty)));
        }

        return properties;
    }

    static public HashMap<Integer, Set<Integer>> getObjectPropertyRelationsByIndex(OntologyHelper helper, String objectProperty) {
        HashMap<Integer, Set<Integer>> relations = new HashMap<>();

        for (OWLNamedIndividual ind : helper.getSortedIndividuals(helper.getIndividuals())) {

            OWLDataProperty dpIndex = helper.getDataProperty("index");
            OWLObjectProperty opProperty = helper.getObjectProperty(objectProperty);

            int index = Integer.parseInt(helper.getDataValue(ind, dpIndex));

            Set<Integer> indIndexes = new HashSet<>();

            for (Node<OWLNamedIndividual> sameOpInd : helper.getReasoner().getObjectPropertyValues(ind, opProperty)) {
                OWLNamedIndividual opInd = sameOpInd.getRepresentativeElement();
                int opIndex = Integer.parseInt(helper.getDataValue(opInd, dpIndex));
                indIndexes.add(opIndex);
            }

            relations.put(index, indIndexes);
        }

        return relations;
    }

    void FillErrors(Set<StudentError> resultErrors, HashMap<Integer, Set<Integer>> errors, StudentErrorType type) {
        errors.forEach((error,reasons) -> {
            for (Integer reason : reasons) {
                resultErrors.add(new StudentError(error, reason, type));
            }
        });
    }
}

