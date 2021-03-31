package org.vstu.compprehension.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class MistakeEntity {
    private Long id;

    private List<ExplanationTemplateInfoEntity> explanationTemplateInfo;

    private String lawName;
    private List<BackendFactEntity> violationFacts;
}
