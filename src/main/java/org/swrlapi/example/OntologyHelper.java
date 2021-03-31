package org.swrlapi.example;

import org.vstu.compprehension.models.businesslogic.Ordering;
import org.vstu.compprehension.models.businesslogic.Tag;
import org.vstu.compprehension.models.businesslogic.backend.JenaBackend;
import org.vstu.compprehension.models.businesslogic.domains.ProgrammingLanguageExpressionDomain;
import org.vstu.compprehension.models.entities.BackendFactEntity;
import org.vstu.compprehension.models.entities.ExerciseAttemptEntity;
import org.vstu.compprehension.utils.DomainAdapter;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class OntologyHelper {
    ProgrammingLanguageExpressionDomain domain = (ProgrammingLanguageExpressionDomain)DomainAdapter.getDomain("ProgrammingLanguageExpressionDomain");
    Ordering question;
    JenaBackend backend = new JenaBackend();

    List<Tag> getTags(String programmingLanguage) {
        List<Tag> result = new ArrayList<>();
        for (String tagString : List.of("basics", "operators", "order", "evaluation", programmingLanguage)) {
            Tag tag = new Tag();
            tag.setName(tagString);
            result.add(tag);
        }
        return result;
    }

    public OntologyHelper(Expression expression, String programmingLanguage) {
        question = (Ordering)domain.makeQuestion(expression);

        List<BackendFactEntity> solution = backend.solve(
                new ArrayList<>(domain.getQuestionPositiveLaws(question.getQuestionDomainType(), getTags(programmingLanguage))),
                question.getStatementFacts(),
                domain.getSolutionVerbs(question.getQuestionDomainType(), question.getStatementFacts()));
        assertFalse(solution.isEmpty());
        List<BackendFactEntity> violations = backend.judge(
                new ArrayList<>(domain.getQuestionNegativeLaws(question.getQuestionDomainType(), getTags(programmingLanguage))),
                question.getStatementFacts(),
                solution,
                domain.responseToFacts(
                        question.getQuestionDomainType(),
                        question.getResponses(),
                        question.getAnswerObjects()),
                domain.getViolationVerbs(question.getQuestionDomainType(), question.getStatementFacts())
        );
        solution.addAll(violations);
        question.getQuestionData().setSolutionFacts(solution);
    }

    public HashMap<Integer, String> getDataProperties(String prop) {
        HashMap<Integer, String> result = new HashMap<>();
        for (BackendFactEntity fact : question.getSolutionFacts()) {
            if (fact.getVerb().equals(prop) && fact.getSubject().startsWith("op__0__")) {
                Integer ind = Integer.parseInt(fact.getSubject().substring("op__0__".length()));
                result.put(ind, fact.getObject());
            }
        }
        return result;
    }

    public HashMap<Integer, Set<Integer>> getObjectPropertyRelationsByIndex(String prop) {
        HashMap<Integer, Set<Integer>> result = new HashMap<>();
        for (BackendFactEntity fact : question.getSolutionFacts()) {
            if (fact.getVerb().equals(prop) && fact.getSubject().startsWith("op__0__")) {
                Integer ind = Integer.parseInt(fact.getSubject().substring("op__0__".length()));
                Integer toInd = Integer.parseInt(fact.getObject().substring("op__0__".length()));

                if (!result.containsKey(ind)) {
                    result.put(ind, new HashSet<>());
                }
                result.get(ind).add(toInd);
            }
        }
        return result;
    }
}
