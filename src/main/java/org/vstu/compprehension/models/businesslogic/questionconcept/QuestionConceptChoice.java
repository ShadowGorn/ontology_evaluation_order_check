package org.vstu.compprehension.models.businesslogic.questionconcept;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.vstu.compprehension.models.entities.BackendEntity;
import org.vstu.compprehension.models.entities.QuestionEntity;

@Data
@NoArgsConstructor
public class QuestionConceptChoice {
    private String selectedVerb;

    private String notSelectedVerb;

    private String selectedConcept;

    private String notSelectedConcept;

    private QuestionEntity question;

    private BackendEntity backend;
}
