package org.vstu.compprehension.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.vstu.compprehension.models.entities.EnumData.QuestionStatus;
import org.vstu.compprehension.models.entities.EnumData.QuestionType;
import org.vstu.compprehension.models.entities.QuestionOptions.QuestionOptionsEntity;
import java.util.List;

@Data
@NoArgsConstructor
public class QuestionEntity {
    private Long id;
    private QuestionType questionType;
    private QuestionStatus questionStatus;
    private String questionText;

    private String questionDomainType;
    private Boolean areAnswersRequireContext;
    private QuestionOptionsEntity options;
    private List<AnswerObjectEntity> answerObjects;

    private ExerciseAttemptEntity exerciseAttempt;
    private DomainEntity domainEntity;

    private List<BackendFactEntity> statementFacts;
    private List<BackendFactEntity> solutionFacts;
}
