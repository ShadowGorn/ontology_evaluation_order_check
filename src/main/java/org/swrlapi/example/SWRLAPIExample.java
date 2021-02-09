package org.swrlapi.example;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.swrlapi.example.OntologyUtil.*;

public class SWRLAPIExample extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Evaluation order faults description");
        String lang = "en";

        VBox root = new VBox();
        root.setStyle("-fx-font: 24 arial;");
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root,1100,500);
        stage.setScene(scene);

        HBox inputPane = new HBox();
        inputPane.setAlignment(Pos.CENTER);

        Label inputLabel = new Label("Input expression, split tokens with a space");
        TextField input = new TextField();
        Button prepareButton = new Button("Prepare");
        inputPane.getChildren().addAll(input, prepareButton);

        HBox buttonsPaneStub = new HBox();
        VBox errorsPaneStub = new VBox();
        root.getChildren().addAll(inputLabel, inputPane, buttonsPaneStub, errorsPaneStub);
        final int BUTTONS_INDEX = 2;
        final int ERRORS_INDEX = 3;

        AtomicReference<Expression> expr = new AtomicReference<>();
        AtomicReference<List<Relation>> relations = new AtomicReference<>();
        AtomicReference<Integer> lastSetPos = new AtomicReference<>();
        AtomicReference<Set<Integer>> functionCallPos = new AtomicReference<>();
        AtomicReference<Boolean> lastPosIsError = new AtomicReference<>();

        prepareButton.setOnAction(e -> {
            expr.set(new Expression(Arrays.asList(input.getText().split(" "))));
            lastSetPos.set(1);
            lastPosIsError.set(false);
            root.getChildren().set(BUTTONS_INDEX, new HBox());
            OntologyHelper helper = new OntologyHelper(expr.get());
            relations.set(GetRelations(helper));
            functionCallPos.set(getFunctionCallPositions(helper));
            Set<Integer> operandsPos = getOperandPositions(helper);

            GridPane evaluationButtons = new GridPane();
            evaluationButtons.setAlignment(Pos.CENTER);
            int pos = 0;
            for (String token : input.getText().split(" ")) {
                Button tokenButton = new Button(token);
                AtomicReference<Integer> tokenPos = new AtomicReference<>();
                tokenPos.set(pos);
                boolean isParenthesis = token.equals("(") && !functionCallPos.get().contains(pos);
                expr.get().getTerms().get(tokenPos.get()).setStudentPos(isParenthesis ? 0 : 1000);

                if (operandsPos.contains(pos) || isParenthesis) {
                    tokenButton.setDisable(true);
                } else {
                    tokenButton.setOnAction(fe -> {
                        root.getChildren().set(ERRORS_INDEX, new FlowPane());
                        expr.get().getTerms().get(tokenPos.get()).setStudentPos(lastSetPos.get());
                        OntologyHelper helperErrors = new OntologyHelper(expr.get(), relations.get());
                        Set<StudentError> errors = GetErrors(helperErrors, false);
                        if (errors.isEmpty()) {
                            tokenButton.setDisable(true);
                            tokenButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
                            tokenButton.setText(tokenButton.getText() + " / " + lastSetPos.get());
                            lastSetPos.set(lastSetPos.get() + 1);

                        } else {
                            VBox errorsPane = new VBox();
                            errorsPane.setAlignment(Pos.CENTER);
                            int errorPos = 1;
                            for (StudentError error : errors) {
                                String text = getErrorDescription(error, helperErrors, lang);
                                text = "\n" + errorPos + ") " + Character.toUpperCase(text.charAt(0)) + text.substring(1);
                                Label errorLabel = new Label(text);
                                errorsPane.getChildren().add(errorLabel);
                                errorPos++;
                            }
                            root.getChildren().set(ERRORS_INDEX, errorsPane);
                            expr.get().getTerms().get(tokenPos.get()).setStudentPos(1000);
                        }
                    });
                }
                Label posLabel = new Label(String.valueOf(pos + 1));
                posLabel.setTextAlignment(TextAlignment.CENTER);
                GridPane.setHalignment(posLabel, HPos.CENTER);
                evaluationButtons.add(posLabel, pos, 0);
                evaluationButtons.add(tokenButton, pos, 1);
                pos++;
            }
            root.getChildren().set(BUTTONS_INDEX, evaluationButtons);
        });

        stage.show();
    }
}

