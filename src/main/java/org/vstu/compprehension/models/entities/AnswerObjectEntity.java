package org.vstu.compprehension.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AnswerObjectEntity {
    private Long id;
    private String hyperText;
    private String domainInfo;
    private boolean isRightCol;
    private String concept;

    private List<ResponseEntity> responsesLeft;
    private List<ResponseEntity> responsesRight;
    private QuestionEntity question;
}
