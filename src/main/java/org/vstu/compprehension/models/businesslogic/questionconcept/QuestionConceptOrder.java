package org.vstu.compprehension.models.businesslogic.questionconcept;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.vstu.compprehension.models.entities.BackendEntity;
import org.vstu.compprehension.models.entities.QuestionEntity;

@Data
@NoArgsConstructor
public class QuestionConceptOrder {

    private String startConcept;

    private String notInOrderConcept;

    private String followVerb;

    private String notInOrderVerb;

    private QuestionEntity question;

    private BackendEntity backend;
}
