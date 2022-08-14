package org.swrlapi.example;

import org.vstu.compprehension.models.businesslogic.Ordering;
import org.vstu.compprehension.models.businesslogic.Question;
import org.vstu.compprehension.models.businesslogic.Tag;
import org.vstu.compprehension.models.businesslogic.backend.JenaBackend;
import org.vstu.compprehension.models.businesslogic.domains.Domain;
import org.vstu.compprehension.models.businesslogic.domains.ProgrammingLanguageExpressionDomain;
import org.vstu.compprehension.models.entities.AnswerObjectEntity;
import org.vstu.compprehension.models.entities.BackendFactEntity;
import org.vstu.compprehension.models.entities.ViolationEntity;
import org.vstu.compprehension.utils.DomainAdapter;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.swrlapi.example.OntologyUtil.GetErrors;

public class OntologyHelper {
    ProgrammingLanguageExpressionDomain domain = (ProgrammingLanguageExpressionDomain)DomainAdapter.getDomain("ProgrammingLanguageExpressionDomain");
    Question question;
    Domain.InterpretSentenceResult res;
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

    public OntologyHelper(Expression expression, String programmingLanguage, String lang, String lawName) {
        makeQuestion(expression, programmingLanguage);
        ViolationEntity violation = new ViolationEntity();
        if (lawName == null) {
            for (ViolationEntity testViolation : res.violations) {
                if (needSupplementaryQuestion(testViolation)) {
                    violation = testViolation;
                    break;
                }
            }
            if (violation.getLawName() == null) {
                violation = res.violations.get(0);
            }
        } else {
            violation.setLawName(lawName);
        }
        question = domain.makeSupplementaryQuestion(question, violation, lang);
    }

    private void makeQuestion(Expression expression, String programmingLanguage) {
        question = domain.makeQuestion(expression);

        List<BackendFactEntity> solution = backend.solve(
                new ArrayList<>(domain.getQuestionPositiveLaws(question.getQuestionDomainType(), getTags(programmingLanguage))),
                question.getStatementFacts(),
                domain.getSolutionVerbs(question.getQuestionDomainType(), question.getStatementFacts()));
        assertFalse(expression.size() > 1 && solution.isEmpty());
        List<BackendFactEntity> violations = backend.judge(
                new ArrayList<>(domain.getQuestionNegativeLaws(question.getQuestionDomainType(), getTags(programmingLanguage))),
                question.getStatementFacts(),
                solution,
                domain.responseToFacts(
                        question.getQuestionDomainType(),
                        ((Ordering)question).getResponses(),
                        question.getAnswerObjects()),
                domain.getViolationVerbs(question.getQuestionDomainType(), question.getStatementFacts())
        );
        solution.addAll(violations);
        res = domain.interpretSentence(violations);
        question.getQuestionData().setSolutionFacts(solution);

        HashSet<String> notAnswers = new HashSet<>();
        for (BackendFactEntity fact : question.getSolutionFacts()) {
            if (fact.getVerb().equals("not_selectable") && fact.getSubject().startsWith("op__0__")) {
                notAnswers.add(fact.getSubject());
            }
        }
        ArrayList<AnswerObjectEntity> newAnswers = new ArrayList<>();
        for (AnswerObjectEntity answer : question.getAnswerObjects()) {
            if (!notAnswers.contains(answer.getDomainInfo())) {
                newAnswers.add(answer);
            }
        }
        question.setAnswerObjects(newAnswers);
    }

    public OntologyHelper(Expression expression, String programmingLanguage) {
        makeQuestion(expression, programmingLanguage);
    }

    public Domain.InterpretSentenceResult judgeSupplementaryQuestion(AnswerObjectEntity answer) {
        return domain.judgeSupplementaryQuestion(question, answer);
    }

    public boolean needSupplementaryQuestion(ViolationEntity violation) {
        return domain.needSupplementaryQuestion(violation);
    }

    public Domain.InterpretSentenceResult judgeQuestion() {
        return res;
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

    public Question getQuestion() {
        return question;
    }
}
