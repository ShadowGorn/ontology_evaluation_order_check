package org.vstu.compprehension.models.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BackendEntity {
    private Long id;

    private String name;

    // Делаю так, что Backend не может знать об Exercise, LawFormulation и questionConceptChoice/Order/Match.
    // Чтобы изменить, надо сделать OneToMany связь и добавить списки сюда
    // См.Пример в Question
}
