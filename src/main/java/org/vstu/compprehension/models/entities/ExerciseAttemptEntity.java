package org.vstu.compprehension.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ExerciseAttemptEntity {
    private Long id;
    private List<QuestionEntity> questions;
}
