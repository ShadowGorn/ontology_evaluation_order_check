package org.vstu.compprehension.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
public class ViolationEntity {
    private Long id;
    private List<ExplanationTemplateInfoEntity> explanationTemplateInfo;
    private String lawName;
    private String detailedLawName;
    private List<BackendFactEntity> violationFacts;
}
