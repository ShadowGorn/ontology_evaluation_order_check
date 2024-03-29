package org.vstu.compprehension.models.businesslogic;

import org.junit.jupiter.api.Test;
import org.vstu.compprehension.models.businesslogic.backend.Backend;
import org.vstu.compprehension.models.businesslogic.backend.JenaBackend;
import org.vstu.compprehension.models.businesslogic.domains.ProgrammingLanguageExpressionDomain;
import org.vstu.compprehension.models.entities.BackendFactEntity;
import org.vstu.compprehension.models.entities.EnumData.Language;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProgrammingLanguageExpressionDomainTest {

    ProgrammingLanguageExpressionDomain domain;
    ProgrammingLanguageExpressionDomainTest() {
        domain = new ProgrammingLanguageExpressionDomain();
    }

    @Test
    public void testName() {
        assertEquals(domain.getName(), "ProgrammingLanguageExpressionDomain");
    }

    @Test
    public void testLaws() {
        assertNotNull(domain.getPositiveLaw("single_token_binary_execution"));
        assertTrue(domain.getPositiveLaw("single_token_binary_execution").isPositiveLaw());
    }

    List<BackendFactEntity> createStatement(List<String> expression, List<String> isOperator) {
        List<BackendFactEntity> facts = new ArrayList<>();
        for (int i = 0; i < expression.size(); ++i) {
            facts.add(new BackendFactEntity(isOperator.get(i),"",expression.get(i)));
        }
        return facts;
    }

    @Test
    public void testQuestionGeneration() throws Exception {
        List<Tag> tags = new ArrayList<>();
        for (String tagString : List.of("basics", "operators", "order", "evaluation", "C++")) {
            Tag tag = new Tag();
            tag.setName(tagString);
            tags.add(tag);
        }

        QuestionRequest qr = new QuestionRequest();
        qr.setTargetConcepts(List.of(
                domain.getConcept("precedence"),
                domain.getConcept("SystemIntegrationTest")
        ));
        qr.setAllowedConcepts(List.of(
                domain.getConcept("operator_binary_+"),
                domain.getConcept("operator_binary_*")
        ));
        qr.setDeniedConcepts(List.of(
                domain.getConcept("associativity")
        ));
        assertEquals("<p>Press the operators in the expression in the order they are evaluated</p>"+ProgrammingLanguageExpressionDomain.ExpressionToHtml(createStatement(List.of("a", "==", "b", "<", "c"), List.of("", "operator", "", "operator", ""))), domain.makeQuestion(qr, tags, Language.ENGLISH).getQuestionText().getText());

        QuestionRequest qr2 = new QuestionRequest();
        qr2.setTargetConcepts(List.of(
                domain.getConcept("associativity"),
                domain.getConcept("SystemIntegrationTest")
        ));
        qr2.setAllowedConcepts(List.of(
                domain.getConcept("operator_binary_+")
        ));
        qr2.setDeniedConcepts(List.of(
                domain.getConcept("precedence")
        ));
        assertEquals("<p>Press the operators in the expression in the order they are evaluated</p>"+ProgrammingLanguageExpressionDomain.ExpressionToHtml(createStatement(List.of("a", "+", "b", "+", "c"), List.of("", "operator", "", "operator", ""))), domain.makeQuestion(qr2, tags, Language.ENGLISH).getQuestionText().getText());

        QuestionRequest qr3 = new QuestionRequest();
        qr3.setTargetConcepts(List.of(
                domain.getConcept("associativity"),
                domain.getConcept("precedence"),
                domain.getConcept("SystemIntegrationTest")
        ));
        qr3.setAllowedConcepts(List.of(
                domain.getConcept("operator_binary_*"),
                domain.getConcept("operator_binary_+")
        ));
        qr3.setDeniedConcepts(List.of(

        ));
        assertEquals("<p>Press the operators in the expression in the order they are evaluated</p>"+ProgrammingLanguageExpressionDomain.ExpressionToHtml(createStatement(List.of("a", "+", "b", "+", "c", "*", "d"), List.of("", "operator", "", "operator", "", "operator", ""))), domain.makeQuestion(qr3, tags, Language.ENGLISH).getQuestionText().getText());

        QuestionRequest qr4 = new QuestionRequest();
        qr4.setTargetConcepts(List.of());
        qr4.setAllowedConcepts(List.of());
        qr4.setDeniedConcepts(List.of(
                domain.getConcept("associativity"),
                domain.getConcept("precedence"),
                domain.getConcept("type"),
                domain.getConcept("SystemIntegrationTest")
        ));
        assertEquals("Choose associativity of operator binary +",
                domain.makeQuestion(qr4, tags, Language.ENGLISH).getQuestionText().getText());
    }

    @Test
    public void testQuestionSolve() throws Exception {
        List<Tag> tags = new ArrayList<>();
        for (String tagString : List.of("basics", "operators", "order", "evaluation", "C++")) {
            Tag tag = new Tag();
            tag.setName(tagString);
            tags.add(tag);
        }

        QuestionRequest qr = new QuestionRequest();
        qr.setTargetConcepts(List.of(
                domain.getConcept("precedence"),
                domain.getConcept("SystemIntegrationTest")
        ));
        qr.setAllowedConcepts(List.of(
                domain.getConcept("operator_binary_+"),
                domain.getConcept("operator_binary_*")
        ));
        qr.setDeniedConcepts(List.of(
                domain.getConcept("associativity")
        ));
        Question question = domain.makeQuestion(qr, tags, Language.ENGLISH);
        assertEquals("<p>Press the operators in the expression in the order they are evaluated</p>"+ProgrammingLanguageExpressionDomain.ExpressionToHtml(createStatement(List.of("a", "==", "b", "<", "c"), List.of("", "operator", "", "operator", ""))), question.getQuestionText().getText());

        Backend backend = new JenaBackend();
        List<BackendFactEntity> solution = backend.solve(
                domain.getQuestionLaws(question.getQuestionDomainType(), tags),
                question.getStatementFacts(),
                domain.getSolutionVerbs(question.getQuestionDomainType(), new ArrayList<>()));
        assertFalse(solution.isEmpty());
    }
}