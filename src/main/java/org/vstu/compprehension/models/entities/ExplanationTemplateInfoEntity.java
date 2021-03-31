package org.vstu.compprehension.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExplanationTemplateInfoEntity {
    private Long id;

    private String fieldName;
    private String value;
    private MistakeEntity mistake;
}
