package org.swrlapi.example;

import java.util.ArrayList;
import java.util.List;

class Expression {
    List<Term> Terms;

    public Expression(List<String> expression) {
        Terms = new ArrayList<>();
        for (String part : expression) {
            Terms.add(new Term(part));
        }
    }

    public List<Term> getTerms() {
        return Terms;
    }

    int size() {
        return Terms.size();
    }

}
