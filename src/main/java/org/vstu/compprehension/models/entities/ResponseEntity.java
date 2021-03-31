package org.vstu.compprehension.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseEntity {
    private Long id;
    private AnswerObjectEntity leftAnswerObject;
    private AnswerObjectEntity rightAnswerObject;
}
