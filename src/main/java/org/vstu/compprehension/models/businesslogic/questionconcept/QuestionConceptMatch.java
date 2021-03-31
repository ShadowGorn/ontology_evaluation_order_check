package org.vstu.compprehension.models.businesslogic.questionconcept;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.vstu.compprehension.models.entities.BackendEntity;
import org.vstu.compprehension.models.entities.QuestionEntity;

@Data
@NoArgsConstructor
public class QuestionConceptMatch {
    private String matchVerb;

    private String noMatchLeftConcept;

    private String noMatchLeftVerb;

    private String noMatchRightConcept;

    private String noMatchRightVerb;

    private QuestionEntity question;

    private BackendEntity backend;
}
