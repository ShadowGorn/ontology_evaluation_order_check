package org.vstu.compprehension.models.businesslogic;

import org.vstu.compprehension.models.entities.AnswerObjectEntity;
import org.vstu.compprehension.models.entities.BackendFactEntity;
import org.vstu.compprehension.models.entities.QuestionEntity;
import org.vstu.compprehension.models.entities.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class Choice extends Question {

    public Choice(QuestionEntity questionData) {
        super(questionData);
    }

    @Override
    public List<BackendFactEntity> responseToFacts() {

        List<AnswerObjectEntity> answers = new ArrayList<>(super.
                getAnswerObjects());
        List<ResponseEntity> responses = super.studentResponses;
        List<BackendFactEntity> facts = new ArrayList<>();

        return facts;
    }

    @Override
    public List<BackendFactEntity> responseToFacts(long backendId) {
        return null;
    }

    @Override
    public Long getExerciseAttemptId() {
        return null;
    }
}
