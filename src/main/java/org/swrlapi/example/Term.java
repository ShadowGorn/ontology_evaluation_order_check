package org.swrlapi.example;

import java.util.Optional;

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

    public Optional<Integer> getStudentPos() {
        return StudentPos;
    }

    public String getText() {
        return Text;
    }

    Optional<Integer> StudentPos;
    String Text;
}
