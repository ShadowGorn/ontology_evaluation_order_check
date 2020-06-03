package org.swrlapi.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class Expression {
    List<String> Texts;
    List<Optional<Integer>> StudentPos;

    public Expression(List<String> expression) {
        Texts = new ArrayList<>();
        StudentPos = new ArrayList<>();
        FillFromExpression(expression);
    }

    void FillFromExpression(List<String> expression) {
        for (String part : expression) {
            Term term = new Term(part);
            Texts.add(term.Text);
            StudentPos.add(term.StudentPos);
        }
    }

    class Term {
        Term(String text) {
            String[] parts = text.split("\\$", 2);

            Text = parts[0];
            if (parts.length > 1) {
                StudentPos = Optional.of(Integer.parseInt(parts[1]));
            } else {
                StudentPos = Optional.empty();
            }
        }

        Optional<Integer> StudentPos;
        String Text;
    }
}
