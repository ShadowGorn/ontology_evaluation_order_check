package org.swrlapi.example;

import java.util.ArrayList;
import java.util.List;

public class Expression {
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

    public List<String> getTokens() {
        List<String> tokens = new ArrayList<>();
        for (Term t : Terms) {
            tokens.add(t.Text);
        }
        return tokens;
    }

    public int size() {
        return Terms.size();
    }

}
