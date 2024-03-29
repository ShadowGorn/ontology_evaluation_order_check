package org.vstu.compprehension.models.businesslogic.domains;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import lombok.val;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.text.StringSubstitutor;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Assertions;
import org.swrlapi.example.Term;
import org.vstu.compprehension.models.businesslogic.*;
import org.vstu.compprehension.models.entities.*;
import org.vstu.compprehension.models.entities.EnumData.FeedbackType;
import org.vstu.compprehension.models.entities.EnumData.Language;
import org.vstu.compprehension.models.entities.EnumData.QuestionType;
import org.vstu.compprehension.models.entities.QuestionOptions.*;
import org.vstu.compprehension.utils.HyperText;
import org.apache.commons.collections4.MultiValuedMap;

import org.swrlapi.example.Expression;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.max;
import static org.junit.jupiter.api.Assertions.*;

public class ProgrammingLanguageExpressionDomain extends Domain {
    static final String EVALUATION_ORDER_QUESTION_TYPE = "OrderOperators";
    static final String EVALUATION_ORDER_SUPPLEMENTARY_QUESTION_TYPE = "OrderOperatorsSupplementary";
    static final String DEFINE_TYPE_QUESTION_TYPE = "DefineType";
    static final String LAWS_CONFIG_PATH = "org/vstu/compprehension/models/businesslogic/domains/programming-language-expression-domain-laws.json";
    static final String QUESTIONS_CONFIG_PATH = "org/vstu/compprehension/models/businesslogic/domains/programming-language-expression-domain-questions.json";
    static final String SUPPLEMENTARY_CONFIG_PATH = "org/vstu/compprehension/models/businesslogic/domains/programming-language-expression-domain-supplementary-strategy.json";

    public static final String END_EVALUATION = "student_end_evaluation";

    ResourceBundle localeRu;
    ResourceBundle localeEng;

    public ProgrammingLanguageExpressionDomain() {
        name = "ProgrammingLanguageExpressionDomain";

        fillConcepts();
        readLaws(this.getClass().getClassLoader().getResourceAsStream(LAWS_CONFIG_PATH));
        readSupplementaryConfig(this.getClass().getClassLoader().getResourceAsStream(SUPPLEMENTARY_CONFIG_PATH));

        localeRu = ResourceBundle.getBundle("org/vstu/compprehension/models/businesslogic/domains/programming-language-expression-domain-messages", new Locale("ru"));
        localeEng = ResourceBundle.getBundle("org/vstu/compprehension/models/businesslogic/domains/programming-language-expression-domain-messages", new Locale("en"));
    }

    private void fillConcepts() {
        concepts = new ArrayList<>();

        Concept operandConcept = addConcept("operand");
        Concept simpleOperandConcept = addConcept("simple_operand");
        Concept operatorConcept = addConcept("operator", new ArrayList<>(Arrays.asList(operandConcept)));
        Concept variableConcept = addConcept("variable", new ArrayList<>(Arrays.asList(simpleOperandConcept)));
        Concept literalConcept = addConcept("literal", new ArrayList<>(Arrays.asList(simpleOperandConcept)));
        Concept precedenceConcept = addConcept("precedence");
        Concept associativityConcept = addConcept("associativity");
        Concept leftAssociativityConcept = addConcept("left_associativity", new ArrayList<>(Arrays.asList(associativityConcept)));
        Concept rightAssociativityConcept = addConcept("right_associativity", new ArrayList<>(Arrays.asList(associativityConcept)));
        Concept absentAssociativityConcept = addConcept("absent_associativity", new ArrayList<>(Arrays.asList(associativityConcept)));
        Concept arityConcept = addConcept("arity");
        Concept unaryConcept = addConcept("unary", new ArrayList<>(Arrays.asList(arityConcept)));
        Concept binaryConcept = addConcept("binary", new ArrayList<>(Arrays.asList(arityConcept)));
        Concept ternaryConcept = addConcept("ternary", new ArrayList<>(Arrays.asList(arityConcept)));
        Concept singleTokenOperatorConcept = addConcept("single_token");
        Concept twoTokenOperatorConcept = addConcept("two_token");
        Concept singleTokenUnaryConcept = addConcept("single_token_unary", new ArrayList<>(Arrays.asList(singleTokenOperatorConcept, unaryConcept)));
        Concept singleTokenBinaryConcept = addConcept("single_token_binary", new ArrayList<>(Arrays.asList(singleTokenOperatorConcept, binaryConcept)));
        Concept twoTokenUnaryConcept = addConcept("two_token_unary", new ArrayList<>(Arrays.asList(twoTokenOperatorConcept, unaryConcept)));
        Concept functionCallConcept = addConcept("function_call", new ArrayList<>(Arrays.asList(twoTokenUnaryConcept)));
        Concept twoTokenBinaryConcept = addConcept("two_token_binary", new ArrayList<>(Arrays.asList(twoTokenOperatorConcept, binaryConcept)));
        Concept subscriptConcept = addConcept("operator_[", new ArrayList<>(Arrays.asList(twoTokenBinaryConcept)));
        Concept twoTokenTernaryConcept = addConcept("two_token_ternary", new ArrayList<>(Arrays.asList(twoTokenOperatorConcept, binaryConcept)));
        Concept operatorEvaluationStateConcept = addConcept("operator_evaluation_state");
        Concept operatorEvaluatingLeftOperandFirstConcept = addConcept("operator_evaluating_left_operand_first", new ArrayList<>(Arrays.asList(binaryConcept, operatorEvaluationStateConcept)));
        Concept operatorUnaryPlusConcept = addConcept("operator_unary_+", new ArrayList<>(Arrays.asList(singleTokenUnaryConcept)));
        Concept operatorUnaryMinusConcept = addConcept("operator_unary_-", new ArrayList<>(Arrays.asList(singleTokenUnaryConcept)));
        Concept operatorUnaryPtrConcept = addConcept("operator_unary_*", new ArrayList<>(Arrays.asList(singleTokenUnaryConcept)));
        Concept operatorBinaryPlusConcept = addConcept("operator_binary_+", new ArrayList<>(Arrays.asList(singleTokenBinaryConcept)));
        Concept operatorBinaryMinusConcept = addConcept("operator_binary_-", new ArrayList<>(Arrays.asList(singleTokenBinaryConcept)));
        Concept operatorBinaryMultipleConcept = addConcept("operator_binary_*", new ArrayList<>(Arrays.asList(singleTokenBinaryConcept)));
        Concept operatorBinaryCommaConcept = addConcept("operator_binary_,", new ArrayList<>(Arrays.asList(singleTokenBinaryConcept)));
        Concept operatorTernaryConcept = addConcept("operator_ternary", new ArrayList<>(Arrays.asList(twoTokenTernaryConcept, operatorEvaluatingLeftOperandFirstConcept)));
        Concept operatorEqualsConcept = addConcept("operator_==", new ArrayList<>(Arrays.asList(singleTokenBinaryConcept)));
        Concept prefixOperatorConcept = addConcept("prefix", new ArrayList<>(Arrays.asList(unaryConcept)));
        Concept postfixOperatorConcept = addConcept("postfix", new ArrayList<>(Arrays.asList(unaryConcept)));
        Concept operatorPrefixIncrementConcept = addConcept("operator_prefix_++", new ArrayList<>(Arrays.asList(singleTokenUnaryConcept, prefixOperatorConcept)));
        Concept operatorPrefixDecrementConcept = addConcept("operator_prefix_--", new ArrayList<>(Arrays.asList(singleTokenUnaryConcept, prefixOperatorConcept)));
        Concept operatorPostfixIncrementConcept = addConcept("operator_postfix_++", new ArrayList<>(Arrays.asList(singleTokenUnaryConcept, postfixOperatorConcept)));
        Concept operatorPostfixDecrementConcept = addConcept("operator_postfix_--", new ArrayList<>(Arrays.asList(singleTokenUnaryConcept, postfixOperatorConcept)));
        Concept typeConcept = addConcept("type");
        Concept systemIntegrationTestConcept = addConcept("SystemIntegrationTest");
    }

    private Concept addConcept(String name, List<Concept> baseConcepts) {
        Concept concept = new Concept(name, baseConcepts);
        concepts.add(concept);
        return concept;
    }

    private Concept addConcept(String name) {
        Concept concept = new Concept(name);
        concepts.add(concept);
        return concept;
    }

    public static class SupplementaryAnswerTransition {
        public String check;
        public String question;
        public String detailed_law;
        public boolean correct;
    }

    public static class SupplementaryAnswerConfig {
        public String name;
        public List<SupplementaryAnswerTransition> transitions;
    }

    class SupplementaryConfig {
        String name;
        List<SupplementaryAnswerConfig> answers;
    }

    private HashMap<String, HashMap<String, List<SupplementaryAnswerTransition>>> supplementaryConfig;
    public  HashMap<String, HashMap<String, List<SupplementaryAnswerTransition>>> getSupplementaryConfig() {
        return supplementaryConfig;
    }
    private void readSupplementaryConfig(InputStream inputStream) {
        supplementaryConfig = new HashMap<>();

        SupplementaryConfig[] configs = new Gson().fromJson(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8),
                SupplementaryConfig[].class);

        for (SupplementaryConfig config : configs) {
            supplementaryConfig.put(config.name, new HashMap<>());
            for (SupplementaryAnswerConfig answer : config.answers) {
                supplementaryConfig.get(config.name).put(answer.name, answer.transitions);
            }
        }
    }

    private void readLaws(InputStream inputStream) {
        positiveLaws = new ArrayList<>();
        negativeLaws = new ArrayList<>();

        RuntimeTypeAdapterFactory<Law> runtimeTypeAdapterFactory =
                RuntimeTypeAdapterFactory
                        .of(Law.class, "positive")
                        .registerSubtype(PositiveLaw.class, "true")
                        .registerSubtype(NegativeLaw.class, "false");
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();

        Law[] lawForms = gson.fromJson(
                new InputStreamReader(inputStream),
                Law[].class);

        for (Law lawForm : lawForms) {
            if (lawForm.isPositiveLaw()) {
                positiveLaws.add((PositiveLaw) lawForm);
            } else {
                negativeLaws.add((NegativeLaw) lawForm);
            }
        }
    }

    @Override
    public List<HyperText> getFullSolutionTrace(Question question) {
    	return null;
    }

    @Override
    public void update() {
    }

    private List<Question> readQuestions(InputStream inputStream) {
        List<Question> res = new ArrayList<>();

        RuntimeTypeAdapterFactory<Question> runtimeTypeAdapterFactory =
                RuntimeTypeAdapterFactory
                        .of(Question.class, "questionType")
                        .registerSubtype(Ordering.class, "ORDERING")
                        .registerSubtype(SingleChoice.class, "SINGLE_CHOICE")
                        .registerSubtype(MultiChoice.class, "MULTI_CHOICE")
                        .registerSubtype(Matching.class, "MATCHING");
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();

        Question[] questions = gson.fromJson(
                new InputStreamReader(inputStream),
                Question[].class);

        Collections.addAll(res, questions);
        return res;
    }

    static List<Question> QUESTIONS;
    @Override
    protected List<Question> getQuestionTemplates() {
        if (QUESTIONS == null) {
            QUESTIONS = readQuestions(this.getClass().getClassLoader().getResourceAsStream(QUESTIONS_CONFIG_PATH));
        }
        return QUESTIONS;
    }

    Question makeQuestionCopy(Question q, ExerciseAttemptEntity exerciseAttemptEntity, Language userLang) {
        QuestionOptionsEntity orderQuestionOptions = OrderQuestionOptionsEntity.builder()
                .requireContext(true)
                .showTrace(false)
                .multipleSelectionEnabled(false)
                .orderNumberOptions(new OrderQuestionOptionsEntity.OrderNumberOptions("/", OrderQuestionOptionsEntity.OrderNumberPosition.SUFFIX, null))
                .build();

        QuestionOptionsEntity matchingQuestionOptions = MatchingQuestionOptionsEntity.builder()
                .requireContext(false)
                .displayMode(MatchingQuestionOptionsEntity.DisplayMode.COMBOBOX)
                .build();

        QuestionOptionsEntity multiChoiceQuestionOptions = MultiChoiceOptionsEntity.builder()
                .requireContext(false)
                .build();

        QuestionOptionsEntity singleChoiceQuestionOptions = SingleChoiceOptionsEntity.builder()
                .requireContext(false)
                .build();

        QuestionEntity entity = new QuestionEntity();
        List<AnswerObjectEntity> answerObjectEntities = new ArrayList<>();
        for (AnswerObjectEntity answerObjectEntity : q.getAnswerObjects()) {
            AnswerObjectEntity newAnswerObjectEntity = new AnswerObjectEntity();
            newAnswerObjectEntity.setAnswerId(answerObjectEntity.getAnswerId());
            newAnswerObjectEntity.setConcept(answerObjectEntity.getConcept());
            newAnswerObjectEntity.setDomainInfo(answerObjectEntity.getDomainInfo());

            String text = getMessage(answerObjectEntity.getHyperText(), langStr(userLang));
            if (text.equals("")) {
                text = answerObjectEntity.getHyperText();
            }

            newAnswerObjectEntity.setHyperText(text);
            newAnswerObjectEntity.setQuestion(entity);
            newAnswerObjectEntity.setRightCol(answerObjectEntity.isRightCol());
            newAnswerObjectEntity.setResponsesLeft(new ArrayList<>());
            newAnswerObjectEntity.setResponsesRight(new ArrayList<>());
            answerObjectEntities.add(newAnswerObjectEntity);
        }
        entity.setAnswerObjects(answerObjectEntities);
        entity.setExerciseAttempt(exerciseAttemptEntity);
        entity.setQuestionDomainType(q.getQuestionDomainType());

        //TODO: remove this hack supporting old format
        if (!q.getStatementFacts().isEmpty() && (q.getStatementFacts().get(0).getVerb() == null)) {
            entity.setStatementFacts(getBackendFactss(q.getStatementFacts()));
        } else {
            entity.setStatementFacts(q.getStatementFacts());
            entity.setSolutionFacts(q.getStatementFacts());
        }
        entity.setQuestionType(q.getQuestionType());
        entity.setQuestionName(q.getQuestionName());

        String text = getMessage(q.getQuestionText().getText(), langStr(userLang));
        if (text.equals("")) {
            text = q.getQuestionText().getText();
        }

        switch (q.getQuestionType()) {
            case ORDER:
                val baseQuestionText = getMessage("expr_domain.BASE_QUESTION_TEXT", langStr(userLang));
                //TODO: remove this hack supporting old format
                if (!q.getStatementFacts().isEmpty() && (q.getStatementFacts().get(0).getVerb() == null)) {
                    entity.setQuestionText(baseQuestionText + ExpressionToHtml(q.getStatementFacts()));
                } else {
                    entity.setQuestionText(baseQuestionText + text
                            .replace("end evaluation", getMessage("expr_domain.END_EVALUATION", langStr(userLang)))
                            .replace("student_end_evaluation", getMessage("expr_domain.STUDENT_END_EVALUATION", langStr(userLang))));
                }
                entity.setOptions(orderQuestionOptions);
                Question question = new Ordering(entity, this);
                // patch the newly created question with the concepts from the "template"
                question.getConcepts().addAll(q.getConcepts());
                // ^ shouldn't this be done in a more straightforward way..?
                return question;
            case MATCHING:
                entity.setQuestionText(QuestionTextToHtml(text));
                entity.setOptions(matchingQuestionOptions);
                return new Matching(entity, this);
            case MULTI_CHOICE:
                entity.setQuestionText(QuestionTextToHtml(text));
                entity.setOptions(multiChoiceQuestionOptions);
                return new MultiChoice(entity, this);
            case SINGLE_CHOICE:
                entity.setQuestionText(text);
                entity.setOptions(singleChoiceQuestionOptions);
                return new SingleChoice(entity, this);
            default:
                throw new UnsupportedOperationException("Unknown type in ProgrammingLanguageExpressionDomain::makeQuestion: " + q.getQuestionType());
        }
    }

    public Question makeQuestion(Expression expr) {
        QuestionEntity question = new QuestionEntity();
        question.setQuestionText("");
        question.setQuestionType(QuestionType.ORDER);
        question.setQuestionDomainType("OrderOperators");
        question.setAreAnswersRequireContext(true);
        ArrayList<AnswerObjectEntity> answers = new ArrayList<>();
        TreeMap<Integer, Integer> answerOrderToAnswerPos = new TreeMap<>();
        Integer pos = 0;
        for (Term t : expr.getTerms()) {
            pos++;
            answers.add(createAnswerObject(question, t.getText(), "", "op__0__" + pos.toString(), true));
            if (t.getStudentPos().isPresent() && t.getStudentPos().get() < 1000) {
                answerOrderToAnswerPos.put(t.getStudentPos().get(), pos);
            }
        }
        question.setAnswerObjects(answers);
        question.setStatementFacts(getBackendFacts(expr.getTokens()));
        Ordering ordering = new Ordering(question, this);
        for (Map.Entry<Integer, Integer> a : answerOrderToAnswerPos.entrySet()) {
            ResponseEntity response = new ResponseEntity();
            response.setLeftAnswerObject(question.getAnswerObjects().get(a.getValue() - 1));
            ordering.addResponse(response);
        }
        return ordering;
    }

    @Override
    public Question makeQuestion(QuestionRequest questionRequest, List<Tag> tags, Language userLanguage) {
        // Prepare concept name sets ...
        HashSet<String> conceptNames = new HashSet<>();
        for (Concept concept : questionRequest.getTargetConcepts()) {
            conceptNames.add(concept.getName());
        }
        HashSet<String> deniedConceptNames = new HashSet<>();
        for (Concept concept : questionRequest.getDeniedConcepts()) {
            deniedConceptNames.add(concept.getName());
        }
        deniedConceptNames.add("supplementary");

        HashSet<String> lawNames = new HashSet<>();
        if (questionRequest.getTargetLaws() != null) {
            for (Law law : questionRequest.getTargetLaws()) {
                lawNames.add(law.getName());
            }
        }

        HashSet<String> deniedLawNames = new HashSet<>();
        if (questionRequest.getDeniedLaws() != null) {
            for (Law law : questionRequest.getDeniedLaws()) {
                deniedLawNames.add(law.getName());
            }
        }

        HashSet<String> deniedQuestions = new HashSet<>();
        if (questionRequest.getExerciseAttempt() != null &&
                questionRequest.getExerciseAttempt().getQuestions() != null &&
                questionRequest.getExerciseAttempt().getQuestions().size() > 0) {
            deniedQuestions.add(questionRequest.getExerciseAttempt().getQuestions().get(questionRequest.getExerciseAttempt().getQuestions().size() - 1).getQuestionName());
        }

        Question res = findQuestion(tags, conceptNames, deniedConceptNames, lawNames, deniedLawNames, deniedQuestions);
        if (res != null) {
            return makeQuestionCopy(res, questionRequest.getExerciseAttempt(), userLanguage);
        }

        // make a SingleChoice question ...
        QuestionEntity question = new QuestionEntity();
        question.setExerciseAttempt(questionRequest.getExerciseAttempt());
        question.setQuestionText("Choose associativity of operator binary +");
        question.setQuestionType(QuestionType.SINGLE_CHOICE);
        question.setQuestionDomainType("ChooseAssociativity");
        question.setAreAnswersRequireContext(true);
        question.setAnswerObjects(new ArrayList<>(Arrays.asList(
                createAnswerObject(question, "left", "left_associativity", "left", true),
                createAnswerObject(question, "right", "right_associativity", "right", true),
                createAnswerObject(question, "no associativity", "absent_associativity", "no associativity", true)
        )));
        return new SingleChoice(question, this);
    }

    private AnswerObjectEntity createAnswerObject(QuestionEntity question, String text, String concept, String domainInfo, boolean isLeft) {
        AnswerObjectEntity answerObject = new AnswerObjectEntity();
        answerObject.setHyperText(text);
        answerObject.setRightCol(!isLeft);
        answerObject.setDomainInfo(domainInfo);
        answerObject.setConcept(concept);
        answerObject.setQuestion(question);
        return answerObject;
    }

    public static String ExpressionToHtml(List<BackendFactEntity> expression) {
        StringBuilder sb = new StringBuilder("");
        sb.append("<p class='comp-ph-expr'>");
        int idx = 0;
        int anwerIdx = -1;
        for (BackendFactEntity fact : expression) {
            String tokenValue = "";
            if (fact.getSubjectType() != null) { // Token has value
                tokenValue = fact.getSubjectType();
            }

            if (fact.getSubject() != null && fact.getSubject().equals("operator")) {
                sb.append("<span data-comp-ph-pos='").append(++idx).append("' id='answer_").append(++anwerIdx).append("' class='comp-ph-expr-op-btn' data-comp-ph-value='").append(tokenValue).append("'>").append(fact.getObject()).append("</span>");
            } else if (fact.getSubject() != null && fact.getSubject().equals(END_EVALUATION)) {
                sb.append("<span data-comp-ph-pos='").append(++idx).append("' id='answer_").append(++anwerIdx).append("' class='comp-ph-expr-op-btn comp-ph-expr-op-end' data-comp-ph-value=''>").append(fact.getObject()).append("</span>");
            } else {
                sb.append("<span data-comp-ph-pos='").append(++idx).append("' class='comp-ph-expr-const' data-comp-ph-value='").append(tokenValue).append("'>").append(fact.getObject()).append("</span>");
            }
        }

        sb.append("<!-- Original expression: ");
        for (BackendFactEntity fact : expression) {
            sb.append(fact.getObject()).append(" ");
        }
        sb.append("-->").append("</p>");
        return QuestionTextToHtml(sb.toString());
    }

    public static String QuestionTextToHtml(String text) {
        StringBuilder sb = new StringBuilder(text
                .replaceAll("\\*", "&#8727")
                .replaceAll("\\n", "<br>")
                .replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;"));
        sb.insert(0, "<div class='comp-ph-question'>"); sb.append("</div>");
        return sb.toString();
    }

    private String getName(int step, int index) {
        return "op__" + step + "__" + index;
    }

    public List<BackendFactEntity> getBackendFacts(List<String> expression) {
        List<BackendFactEntity> facts = new ArrayList<>();
        int index = 0;
        for (String token : expression) {
            index++;
            String name = getName(0, index);
            facts.add(new BackendFactEntity(name, "rdf:type", "owl:NamedIndividual"));
            facts.add(new BackendFactEntity("owl:NamedIndividual", name, "index", "xsd:int", String.valueOf(index)));
            facts.add(new BackendFactEntity("owl:NamedIndividual", getName(0, index), "text", "xsd:string", token));
        }
        facts.add(new BackendFactEntity("owl:NamedIndividual", getName(0, index), "last_index", "xsd:boolean", "true"));
        return facts;
    }

    public List<BackendFactEntity> getBackendFactss(List<BackendFactEntity> expression) {
        List<BackendFactEntity> facts = new ArrayList<>();
        int index = 0;
        for (BackendFactEntity token : expression) {
            index++;
            String name = getName(0, index);
            facts.add(new BackendFactEntity(name, "rdf:type", "owl:NamedIndividual"));
            facts.add(new BackendFactEntity("owl:NamedIndividual", name, "index", "xsd:int", String.valueOf(index)));
            facts.add(new BackendFactEntity("owl:NamedIndividual", getName(0, index), "text", "xsd:string", token.getObject()));
            if (token.getVerb() != null) {
                facts.add(new BackendFactEntity("owl:NamedIndividual", getName(0, index), token.getVerb(), token.getSubjectType(), token.getSubject()));
            }
            if (token.getObjectType() != null) { // Hack to insert boolean result
                facts.add(new BackendFactEntity("owl:NamedIndividual", getName(0, index), "has_value", "xsd:boolean", token.getObjectType()));
            }
        }
        facts.add(new BackendFactEntity("owl:NamedIndividual", getName(0, 1), "first", "xsd:boolean", "true"));
        facts.add(new BackendFactEntity("owl:NamedIndividual", getName(0, expression.size()), "last_index", "xsd:boolean", "true"));
        return facts;
    }

    // filter positive laws by question type and tags
    @Override
    public List<PositiveLaw> getQuestionPositiveLaws(String questionDomainType, List<Tag> tags) {
        if (questionDomainType.equals(EVALUATION_ORDER_QUESTION_TYPE) || questionDomainType.equals(DEFINE_TYPE_QUESTION_TYPE)) {
            List<PositiveLaw> positiveLaws = new ArrayList<>();
            for (PositiveLaw law : getPositiveLaws()) {
                boolean needLaw = true;
                for (Tag tag : law.getTags()) {
                    boolean inQuestionTags = false;
                    for (Tag questionTag : tags) {
                        if (questionTag.getName().equals(tag.getName())) {
                            inQuestionTags = true;
                        }
                    }
                    if (!inQuestionTags) {
                        needLaw = false;
                    }
                }
                if (needLaw) {
                    positiveLaws.add(law);
                }
            }
            return positiveLaws;
        }
        return new ArrayList<>(Arrays.asList());
    }

    public List<NegativeLaw> getQuestionNegativeLaws(String questionDomainType, List<Tag> tags) {
        if (questionDomainType.equals(EVALUATION_ORDER_QUESTION_TYPE)) {
            List<NegativeLaw> negativeLaws = new ArrayList<>();
            for (NegativeLaw law : getNegativeLaws()) {
                boolean needLaw = true;
                //filter by tags after separation
                if (needLaw) {
                    negativeLaws.add(law);
                }
            }
            return negativeLaws;
        }
        return new ArrayList<>(Arrays.asList());
    }

    public List<String> getSolutionVerbs(String questionDomainType, List<BackendFactEntity> statementFacts) {
        if (questionDomainType.equals(EVALUATION_ORDER_QUESTION_TYPE)) {
            return new ArrayList<>(Arrays.asList(
                    "has_operand",
                    "before",
                    "before_direct",
                    "before_by_third_operator",
                    "before_third_operator",
                    "before_as_operand",
                    "is_operator_with_strict_operands_order",
                    "high_precedence",
                    "high_precedence_diff_precedence",
                    "high_precedence_left_assoc",
                    "high_precedence_right_assoc",
                    "is_operand",
                    "law_name",
                    "text",
                    "index",
                    "precedence",
                    "associativity",
                    "in_complex",
                    "complex_beginning",
                    "is_function_call",
                    "student_error_in_complex_base",
                    "student_error_more_precedence_base",
                    "student_error_right_assoc_base",
                    "student_error_strict_operands_order_base",
                    "student_error_left_assoc_base",
                    "good_token",
                    "not_selectable"
            ));
        } else if (questionDomainType.equals(DEFINE_TYPE_QUESTION_TYPE)) {
            return new ArrayList<>(Arrays.asList(
                    "get_type"
            ));
        }
        return new ArrayList<>();
    }

    public List<String> getViolationVerbs(String questionDomainType, List<BackendFactEntity> statementFacts) {
        if (questionDomainType.equals(EVALUATION_ORDER_QUESTION_TYPE)) {
            return new ArrayList<>(Arrays.asList(
                    "student_error_more_precedence",
                    "student_error_more_precedence_left",
                    "student_error_more_precedence_right",
                    "student_error_left_assoc",
                    "student_error_right_assoc",
                    "student_error_in_complex",
                    "student_error_strict_operands_order",
                    "student_error_more_precedence_base",
                    "student_error_left_assoc_base",
                    "student_error_right_assoc_base",
                    "student_error_in_complex_base",
                    "student_error_strict_operands_order_base",
                    "before_third_operator",
                    "text",
                    "index",
                    "before_direct",
                    "student_pos_number",
                    "is_operand",
                    "is_function_call"
            ));
        } else if (questionDomainType.equals(DEFINE_TYPE_QUESTION_TYPE)) {
            return new ArrayList<>(Arrays.asList(
                    "wrong_type"
            ));
        }
        return new ArrayList<>();
    }

    @Override
    public List<BackendFactEntity> responseToFacts(String questionDomainType, List<ResponseEntity> responses, List<AnswerObjectEntity> answerObjects) {
        if (questionDomainType.equals(EVALUATION_ORDER_QUESTION_TYPE)) {
            List<BackendFactEntity> result = new ArrayList<>();
            int pos = 1;
            HashSet<String> used = new HashSet<>();
            for (ResponseEntity response : responses) {
                result.add(new BackendFactEntity(
                        "owl:NamedIndividual",
                        response.getLeftAnswerObject().getDomainInfo(),
                        "student_pos_number",
                        "xsd:int",
                        String.valueOf(pos)
                ));
                for (String earlier : used) {
                    result.add(new BackendFactEntity(
                            "owl:NamedIndividual",
                            earlier,
                            "student_pos_less",
                            "owl:NamedIndividual",
                            response.getLeftAnswerObject().getDomainInfo()
                    ));
                }
                used.add(response.getLeftAnswerObject().getDomainInfo());
                pos = pos + 1;
            }

            for (AnswerObjectEntity answerObject : answerObjects) {
                if (!used.contains(answerObject.getDomainInfo())) {
                    for (String earlier : used) {
                        result.add(new BackendFactEntity(
                                "owl:NamedIndividual",
                                earlier,
                                "student_pos_less",
                                "owl:NamedIndividual",
                                answerObject.getDomainInfo()
                        ));
                    }
                }
            }
            return result;
        } else if (questionDomainType.equals(DEFINE_TYPE_QUESTION_TYPE)) {
            List<BackendFactEntity> result = new ArrayList<>();
            for (ResponseEntity response : responses) {
                result.add(new BackendFactEntity(
                        "owl:NamedIndividual",
                        response.getLeftAnswerObject().getDomainInfo(),
                        "student_type",
                        "xsd:string",
                        response.getRightAnswerObject().getDomainInfo()
                ));
            }
            return result;
        }
        return new ArrayList<>();
    }

    private static Optional<Integer> getIndexFromName(String name, boolean allowNotZeroStep) {
        Assertions.assertTrue(name.startsWith("op__"), name);
        String[] parts = name.split("__");
        assertEquals(3, parts.length, name);
        if (allowNotZeroStep || parts[1].equals("0")) {
            return Optional.of(Integer.parseInt(parts[2]));
        }
        return Optional.empty();
    }

    class CorrectAnswerImpl {
        String domainID;
        String lawName;
    }

    private List<CorrectAnswerImpl> getCorrectAnswers(List<BackendFactEntity> solution) {
        Map<String, List<String>> before = new HashMap<>();
        Map<String, String> studentPos = new HashMap<>();
        Map<String, String> operatorLawName = new HashMap<>();
        HashSet<String> isOperand = new HashSet<>();
        HashSet<String> allTokens = new HashSet<>();

        List<CorrectAnswerImpl> result = new ArrayList<>();
        for (BackendFactEntity fact : solution) {
            if (fact.getVerb().equals("before_direct")) {
                if (!before.containsKey(fact.getObject())) {
                    before.put(fact.getObject(), new ArrayList<>());
                }
                before.get(fact.getObject()).add(fact.getSubject());
                allTokens.add(fact.getObject());
                allTokens.add(fact.getSubject());
            } else if (fact.getVerb().equals("student_pos_number")) {
                studentPos.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("is_operand")) {
                isOperand.add(fact.getSubject());
            } else if (fact.getVerb().equals("law_name")) {
                operatorLawName.put(fact.getSubject(), fact.getObject());
            }
        }

        for (String operator : allTokens) {
            if (!operator.startsWith("op__0") || isOperand.contains(operator)) {
                continue;
            }
            boolean can = !studentPos.containsKey(operator);
            if (before.containsKey(operator)) {
                List<String> deps = before.get(operator);
                for (String dep : deps) {
                    if (!studentPos.containsKey(dep) && !isOperand.contains(dep)) {
                        can = false;
                        break;
                    }
                }
            }
            if (can) {
                CorrectAnswerImpl answer = new CorrectAnswerImpl();
                answer.domainID = operator;
                answer.lawName = operatorLawName.get(operator);
                result.add(answer);
            }
        }
        return result;
    }

    @Override
    public ProcessSolutionResult processSolution(List<BackendFactEntity> solution) {
        Map<String, String> studentPos = new HashMap<>();
        HashSet<String> isOperand = new HashSet<>();
        HashSet<String> allTokens = new HashSet<>();
        for (BackendFactEntity fact : solution) {
            if (fact.getVerb().equals("before_direct")) {
                allTokens.add(fact.getObject());
                allTokens.add(fact.getSubject());
            } else if (fact.getVerb().equals("student_pos_number")) {
                studentPos.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("is_operand")) {
                isOperand.add(fact.getSubject());
            }
        }

        int IterationsLeft = 0;
        for (String operator : allTokens) {
            if (operator.startsWith("op__0") && !isOperand.contains(operator) && !studentPos.containsKey(operator)) {
                IterationsLeft++;
            }
        }

        InterpretSentenceResult result = new InterpretSentenceResult();
        result.CountCorrectOptions = getCorrectAnswers(solution).size();
        result.IterationsLeft = IterationsLeft;
        return result;
    }

    @Override
    public CorrectAnswer getAnyNextCorrectAnswer(Question q) {
        return null;
    }

    String langStr(Language language) {
        if (language.equals(Language.RUSSIAN)) {
            return "ru";
        }
        return "eng";
    }

    String getMessage(String x, String lang) {
        try {
            if (lang.equals("ru")) {
                return localeRu.getString(x);
            } else {
                return localeEng.getString(x);
            }
        } catch (MissingResourceException xcp) {
            return x;
        }
    }

    HyperText getCorrectExplanation(Question q, AnswerObjectEntity answer, String lang) {
        HashMap<String, String> indexes = new HashMap<>();
        HashMap<String, String> texts = new HashMap<>();
        HashMap<String, String> isStrict = new HashMap<>();
        MultiValuedMap<String, String> before = new HashSetValuedHashMap<>();
        MultiValuedMap<String, String> beforeIndirectReversed = new HashSetValuedHashMap<>();
        MultiValuedMap<String, String> beforeHighPriority = new HashSetValuedHashMap<>();
        MultiValuedMap<String, String> beforeLeftAssoc = new HashSetValuedHashMap<>();
        MultiValuedMap<String, String> beforeRightAssoc = new HashSetValuedHashMap<>();
        MultiValuedMap<String, String> beforeByThirdOperator = new HashSetValuedHashMap<>();
        MultiValuedMap<String, String> beforeThirdOperator = new HashSetValuedHashMap<>();
        MultiValuedMap<String, String> beforeAsOperand = new HashSetValuedHashMap<>();
        HashMap<String, String> notSelectable = new HashMap<>();

        for (BackendFactEntity fact : q.getSolutionFacts()) {
            if (fact.getVerb().equals("before_direct")) {
                before.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("before")) {
                beforeIndirectReversed.put(fact.getObject(), fact.getSubject());
            } else if (fact.getVerb().equals("before_by_third_operator")) {
                beforeByThirdOperator.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("before_third_operator")) {
                beforeThirdOperator.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("high_precedence_diff_precedence")) {
                beforeHighPriority.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("high_precedence_left_assoc")) {
                beforeLeftAssoc.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("high_precedence_right_assoc")) {
                beforeRightAssoc.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("before_as_operand")) {
                beforeAsOperand.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("index")) {
                indexes.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("text")) {
                texts.put(fact.getSubject(), fact.getObject());
            }  else if (fact.getVerb().equals("is_operator_with_strict_operands_order")) {
                isStrict.put(fact.getSubject(), fact.getObject());
            }  else if (fact.getVerb().equals("not_selectable")) {
                notSelectable.put(fact.getSubject(), fact.getObject());

            }
        }

        AnswerObjectEntity last = null;
        ArrayList<AnswerObjectEntity> explain = new ArrayList<>();
        TreeMap<Integer, String> posToExplanation = new TreeMap<>();

        int answerPos = Integer.parseInt(indexes.get(answer.getDomainInfo()));
        String answerText = texts.get(answer.getDomainInfo());
        String answerTemplate = new StringJoiner(" ").add(answerText).add(getMessage("expr_domain.AT_POS", lang)).add(String.valueOf(answerPos)).toString();
        posToExplanation.put(-1, new StringJoiner(" ").add(getMessage("expr_domain.OPERATOR", lang)).add(answerTemplate).add(getMessage("expr_domain.EVALUATES", lang)).toString());

        for (AnswerObjectEntity answerObjectEntity : q.getAnswerObjects()) {
            if (beforeByThirdOperator.containsKey(answerObjectEntity.getDomainInfo())) {
                for (String leftPart : beforeIndirectReversed.get(answerObjectEntity.getDomainInfo())) {
                    for (String rightPart : beforeByThirdOperator.get(answerObjectEntity.getDomainInfo())) {
                        beforeByThirdOperator.put(leftPart, rightPart);
                    }
                    for (String rightPart : beforeThirdOperator.get(answerObjectEntity.getDomainInfo())) {
                        beforeThirdOperator.put(leftPart, rightPart);
                    }
                }
            }
        }

        for (AnswerObjectEntity answerObjectEntity : q.getAnswerObjects()) {
            if (notSelectable.containsKey(answerObjectEntity.getDomainInfo())) {
                continue;
            }
            if (answer == answerObjectEntity && last != null) {
                explain.add(last);
            } else if (answer == last) {
                explain.add(answerObjectEntity);
            } else if (beforeByThirdOperator.containsMapping(answer.getDomainInfo(), answerObjectEntity.getDomainInfo())) {
                for (String thirdOperator : beforeThirdOperator.get(answer.getDomainInfo())) {
                    // explain highest in right half of strict
                    if (before.containsMapping(answerObjectEntity.getDomainInfo(), thirdOperator)) {
                        int pos = Integer.parseInt(indexes.get(answerObjectEntity.getDomainInfo()));
                        String text = texts.get(answerObjectEntity.getDomainInfo());
                        String template = new StringJoiner(" ").add(text).add(getMessage("expr_domain.AT_POS", lang)).add(String.valueOf(pos)).toString();

                        int thirdPos = Integer.parseInt(indexes.get(thirdOperator));
                        String thirdText = texts.get(thirdOperator);
                        String thirdTemplate = new StringJoiner(" ").add(thirdText).add(getMessage("expr_domain.AT_POS", lang)).add(String.valueOf(thirdPos)).toString();

                        if (isStrict.containsKey(thirdOperator)) {
                            posToExplanation.put(pos, new StringJoiner(" ")
                                    .add(getMessage("expr_domain.BEFORE_OPERATOR", lang))
                                    .add(template)
                                    .add(":")
                                    .add(getMessage("expr_domain.OPERATOR", lang))
                                    .add(answerTemplate)
                                    .add(getMessage("expr_domain.LEFT_SUBOPERATOR", lang))
                                    .add(thirdTemplate)
                                    .add(getMessage("expr_domain.WHILE_OPERATOR", lang))
                                    .add(template)
                                    .add(getMessage("expr_domain.TO_LEFT_OPERAND", lang) + ",")
                                    .add(getMessage("expr_domain.AND_LEFT_OPERAND", lang))
                                    .add(thirdText)
                                    .add(getMessage("expr_domain.EVALUATES_BEFORE_RIGHT", lang))
                                    .toString());
                        }
                    }
                }
            } else if (beforeByThirdOperator.containsMapping(answerObjectEntity.getDomainInfo(), answer.getDomainInfo())) {
                for (String thirdOperator : beforeThirdOperator.get(answerObjectEntity.getDomainInfo())) {
                    // explain highest in left half of strict
                    if (before.containsMapping(answerObjectEntity.getDomainInfo(), thirdOperator)) {
                        int pos = Integer.parseInt(indexes.get(answerObjectEntity.getDomainInfo()));
                        String text = texts.get(answerObjectEntity.getDomainInfo());
                        String template = new StringJoiner(" ").add(text).add(getMessage("expr_domain.AT_POS", lang)).add(String.valueOf(pos)).toString();

                        int thirdPos = Integer.parseInt(indexes.get(thirdOperator));
                        String thirdText = texts.get(thirdOperator);
                        String thirdTemplate = new StringJoiner(" ").add(thirdText).add(getMessage("expr_domain.AT_POS", lang)).add(String.valueOf(thirdPos)).toString();

                        if (isStrict.containsKey(thirdOperator)) {
                            posToExplanation.put(pos, new StringJoiner(" ")
                                    .add(getMessage("expr_domain.AFTER_OPERATOR", lang))
                                    .add(template)
                                    .add(":")
                                    .add(getMessage("expr_domain.OPERATOR", lang))
                                    .add(answerTemplate)
                                    .add(getMessage("expr_domain.RIGHT_SUBOPERATOR", lang))
                                    .add(thirdTemplate)
                                    .add(getMessage("expr_domain.WHILE_OPERATOR", lang))
                                    .add(template)
                                    .add(getMessage("expr_domain.TO_LEFT_OPERAND", lang) + ",")
                                    .add(getMessage("expr_domain.AND_LEFT_OPERAND", lang))
                                    .add(thirdText)
                                    .add(getMessage("expr_domain.EVALUATES_BEFORE_RIGHT", lang))
                                    .toString());
                        } else if (thirdText.equals("(")) {
                            posToExplanation.put(pos, new StringJoiner(" ")
                                    .add(getMessage("expr_domain.AFTER_OPERATOR", lang))
                                    .add(template)
                                    .add(":")
                                    .add(getMessage("expr_domain.OPERATOR", lang))
                                    .add(template)
                                    .add(getMessage("expr_domain.ENCLOSED_PARENTHESIS", lang))
                                    .add(String.valueOf(thirdPos))
                                    .add(getMessage("expr_domain.INSIDE_PARENTHESIS_FIRST", lang))
                                    .toString());
                        }
                    }
                }
            }
            last = answerObjectEntity;
        }

        for (AnswerObjectEntity reason : explain) {
            int pos = Integer.parseInt(indexes.get(reason.getDomainInfo()));
            String text = texts.get(reason.getDomainInfo());
            String template = new StringJoiner(" ").add(text).add(getMessage("expr_domain.AT_POS", lang)).add(String.valueOf(pos)).toString();

            if (beforeHighPriority.containsMapping(answer.getDomainInfo(), reason.getDomainInfo())) {
                posToExplanation.put(pos, new StringJoiner(" ")
                        .add(getMessage("expr_domain.BEFORE_OPERATOR", lang))
                        .add(template)
                        .add(":")
                        .add(getMessage("expr_domain.OPERATOR", lang))
                        .add(answerText)
                        .add(getMessage("expr_domain.HAS_HIGHER_PRECEDENCE", lang))
                        .add(getMessage("expr_domain.THAN_OPERATOR", lang))
                        .add(text)
                        .toString());
            } else if (beforeHighPriority.containsMapping(reason.getDomainInfo(), answer.getDomainInfo())) {
                posToExplanation.put(pos, new StringJoiner(" ")
                        .add(getMessage("expr_domain.AFTER_OPERATOR", lang))
                        .add(template)
                        .add(":")
                        .add(getMessage("expr_domain.OPERATOR", lang))
                        .add(answerText)
                        .add(getMessage("expr_domain.HAS_LOWER_PRECEDENCE", lang))
                        .add(getMessage("expr_domain.THAN_OPERATOR", lang))
                        .add(text)
                        .toString());
            } else if (beforeLeftAssoc.containsMapping(answer.getDomainInfo(), reason.getDomainInfo())) {
                posToExplanation.put(pos, new StringJoiner(" ")
                        .add(getMessage("expr_domain.BEFORE_OPERATOR", lang))
                        .add(template)
                        .add(":")
                        .add(getMessage("expr_domain.OPERATOR", lang))
                        .add(answerText)
                        .add(getMessage("expr_domain.LEFT_ASSOC_DESC", lang))
                        .toString());
            } else if (beforeLeftAssoc.containsMapping(reason.getDomainInfo(), answer.getDomainInfo())) {
                posToExplanation.put(pos, new StringJoiner(" ")
                        .add(getMessage("expr_domain.AFTER_OPERATOR", lang))
                        .add(template)
                        .add(":")
                        .add(getMessage("expr_domain.OPERATOR", lang))
                        .add(answerText)
                        .add(getMessage("expr_domain.LEFT_ASSOC_DESC", lang))
                        .toString());
            } else if (beforeRightAssoc.containsMapping(answer.getDomainInfo(), reason.getDomainInfo())) {
                posToExplanation.put(pos, new StringJoiner(" ")
                        .add(getMessage("expr_domain.BEFORE_OPERATOR", lang))
                        .add(template)
                        .add(":")
                        .add(getMessage("expr_domain.OPERATOR", lang))
                        .add(answerText)
                        .add(getMessage("expr_domain.RIGHT_ASSOC_DESC", lang))
                        .toString());
            } else if (beforeRightAssoc.containsMapping(reason.getDomainInfo(), answer.getDomainInfo())) {
                posToExplanation.put(pos, new StringJoiner(" ")
                        .add(getMessage("expr_domain.AFTER_OPERATOR", lang))
                        .add(template)
                        .add(":")
                        .add(getMessage("expr_domain.OPERATOR", lang))
                        .add(answerText)
                        .add(getMessage("expr_domain.RIGHT_ASSOC_DESC", lang))
                        .toString());
            }
        }

        StringBuilder result = new StringBuilder();
        List<String> concats = new ArrayList<>(List.of(" because ", " while "));
        for (int i = 2; i < posToExplanation.size() - 1; ++i) {
            concats.add(", ");
        }
        concats.add(" and ");
        int concats_pos = 0;
        for (Map.Entry<Integer, String> kv : posToExplanation.entrySet()) {
            String part = kv.getValue();
            if (part.contains(" : ")) {
                if (lang.equals("en")) {
                    part = part.replace(" : ", concats.get(concats_pos));
                    concats_pos++;
                }
            }
            result.append(part).append("\n");
        }

        return new HyperText(result.toString());
    }

    public CorrectAnswer getAnyNextCorrectAnswer(Question q, String lang) {
        List<CorrectAnswerImpl> correctAnswerImpls = getCorrectAnswers(q.getSolutionFacts());

        for (AnswerObjectEntity answer : q.getAnswerObjects()) {
            for (CorrectAnswerImpl answerImpl : correctAnswerImpls) {
                if (answerImpl.domainID.equals(answer.getDomainInfo())) {
                    CorrectAnswer correctAnswer = new CorrectAnswer();
                    correctAnswer.answer = answer;
                    correctAnswer.lawName = answerImpl.lawName;
                    correctAnswer.explanation = getCorrectExplanation(q, answer, lang);
                    return correctAnswer;
                }
            }
        }
        return null;
    }

    List<Tag> getTags(ExerciseAttemptEntity exerciseAttempt) {
        List<Tag> result = new ArrayList<>();
        return result;
    }

    @Override
    public boolean needSupplementaryQuestion(ViolationEntity violation) {
        if (violation.getLawName().equals("error_base_student_error_in_complex") ||
                violation.getLawName().equals("error_base_student_error_strict_operands_order") ||
                violation.getLawName().equals("error_base_student_error_unevaluated_operand") ||
                violation.getLawName().equals("error_base_student_error_early_finish")) {
            return false;
        }
        return true;
    }

    @Override
    public Question makeSupplementaryQuestion(Question question, ViolationEntity violation, String userLang) {
        if (!needSupplementaryQuestion(violation)) {
            return null;
        }

        HashSet<String> targetConcepts = new HashSet<>();
        String failedLaw = violation.getLawName().startsWith("error_base") ? "first_OrderOperatorsSupplementary" : violation.getLawName();
        targetConcepts.add(failedLaw);
        targetConcepts.add("supplementary");

        if (!supplementaryConfig.containsKey(failedLaw)) {
            return null;
        }

        Language language;
        if (Objects.equals(userLang, "ru")) {
            language = Language.RUSSIAN;
        } else {
            language = Language.ENGLISH;
        }

        Question res = findQuestion(new ArrayList<>(), targetConcepts, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        if (res != null) {
            Question copy = makeQuestionCopy(res, null, language);
            return fillSupplementaryAnswerObjects(question, failedLaw, copy, userLang);
        }

        return null;
    }

    Question fillSupplementaryAnswerObjects(Question originalQuestion, String failedLaw, Question supplementaryQuestion, String lang) {
        Map<String, List<String>> before = new HashMap<>();
        MultiValuedMap<String, String> beforeIndirect = new HashSetValuedHashMap<>();
        Map<String, String> texts = new HashMap<>();
        Map<String, String> indexes = new HashMap<>();
        MultiValuedMap<String, String> highPrecedence = new HashSetValuedHashMap<>();
        MultiValuedMap<String, String> samePrecedenceLeftAssoc = new HashSetValuedHashMap<>();
        MultiValuedMap<String, String> samePrecedenceRightAssoc = new HashSetValuedHashMap<>();
        HashSet<String> used = new HashSet<>();

        for (BackendFactEntity fact : originalQuestion.getSolutionFacts()) {
            if (fact.getVerb().equals("before_direct")) {
                if (!before.containsKey(fact.getObject())) {
                    before.put(fact.getObject(), new ArrayList<>());
                }
                before.get(fact.getObject()).add(fact.getSubject());
            } else if (fact.getVerb().equals("before")) {
                beforeIndirect.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("text")) {
                texts.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("index")) {
                indexes.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("high_precedence_diff_precedence")) {
                highPrecedence.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("high_precedence_left_assoc")) {
                samePrecedenceLeftAssoc.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("high_precedence_right_assoc")) {
                samePrecedenceRightAssoc.put(fact.getSubject(), fact.getObject());
            }
        }

        AnswerObjectEntity failedAnswer = null;
        if (originalQuestion.getResponses() == null || originalQuestion.getResponses().size() == 0) {
            return null;
        }
        for (ResponseEntity response : originalQuestion.getResponses()) {
            used.add(response.getLeftAnswerObject().getDomainInfo());
            failedAnswer = response.getLeftAnswerObject();
        }

        if (failedAnswer == null) {
            return null;
        }

        List<BackendFactEntity> possibleViolationFacts = new ArrayList<>();
        {
            BackendFactEntity factOriginalMistake = new BackendFactEntity("","","original_mistake", "", failedLaw);
            possibleViolationFacts.add(factOriginalMistake);
        }

        Integer failedIndex = Integer.parseInt(indexes.get(failedAnswer.getDomainInfo()));

        HashMap<String, String> templates = new HashMap<>();
        for (AnswerObjectEntity origAnswer : originalQuestion.getAnswerObjects()) {
            if (origAnswer.getDomainInfo().equals("end_token")) {
                continue;
            }
            String text = texts.get(origAnswer.getDomainInfo());
            Integer index = Integer.parseInt(indexes.get(origAnswer.getDomainInfo()));
            String domainInfo = origAnswer.getDomainInfo();

            possibleViolationFacts.add(new BackendFactEntity(String.valueOf(origAnswer.getAnswerId()), "text", text));
            possibleViolationFacts.add(new BackendFactEntity(String.valueOf(origAnswer.getAnswerId()), "index", String.valueOf(index)));

            if (origAnswer.getDomainInfo().equals(failedAnswer.getDomainInfo())) {
                templates.put("operator", text);
                templates.put("pos", index.toString());
                templates.put("operator_domain_info", domainInfo);
            }

            if (index < failedIndex && !used.contains(origAnswer.getDomainInfo())) {
                templates.put("left_operator", text);
                templates.put("left_operator_pos", index.toString());
                templates.put("left_operator_domain_info", domainInfo);

                if (highPrecedence.containsMapping(failedAnswer.getDomainInfo(), origAnswer.getDomainInfo())) {
                    templates.put("left_operator_priority", "low");
                    templates.put("left_operator_correct", "wrong");
                } else if (highPrecedence.containsMapping(origAnswer.getDomainInfo(), failedAnswer.getDomainInfo())) {
                    templates.put("left_operator_priority", "high");
                    templates.put("left_operator_correct", "correct");
                } else if (samePrecedenceLeftAssoc.containsMapping(origAnswer.getDomainInfo(), failedAnswer.getDomainInfo())) {
                    templates.put("left_operator_priority", "same");
                    templates.put("left_operator_associativity", "L");
                    templates.put("left_operator_correct", "correct");
                } else {
                    templates.put("left_operator_priority", "same");
                    templates.put("left_operator_associativity", "R");
                    templates.put("left_operator_correct", "wrong");
                }
            }
            if (index > failedIndex && !used.contains(origAnswer.getDomainInfo()) && !templates.containsKey("right_operator")) {
                templates.put("right_operator", text);
                templates.put("right_operator_pos", index.toString());
                templates.put("right_operator_domain_info", domainInfo);

                if (highPrecedence.containsMapping(origAnswer.getDomainInfo(), failedAnswer.getDomainInfo())) {
                    templates.put("right_operator_priority", "high");
                    templates.put("right_operator_correct", "correct");
                } else if (highPrecedence.containsMapping(failedAnswer.getDomainInfo(), origAnswer.getDomainInfo())) {
                    templates.put("right_operator_priority", "low");
                    templates.put("right_operator_correct", "wrong");
                } else if (samePrecedenceRightAssoc.containsMapping(origAnswer.getDomainInfo(), failedAnswer.getDomainInfo())) {
                    templates.put("right_operator_priority", "same");
                    templates.put("right_operator_associativity", "R");
                    templates.put("right_operator_correct", "correct");
                } else {
                    templates.put("right_operator_priority", "same");
                    templates.put("right_operator_associativity", "L");
                    templates.put("right_operator_correct", "wrong");
                }
            }
            //TODO: check in parenthesis left/right
            //TODO: check is failed complex beginning and have inner unused
            //TODO: check operator with strict order
        }

        StringSubstitutor stringSubstitutor = new StringSubstitutor(templates);
        stringSubstitutor.setEnableUndefinedVariableException(true);

        try {
            String text = stringSubstitutor.replace(supplementaryQuestion.getQuestionText().getText());
            text = text.replaceAll(getMessage("expr_domain.SAME_PRECEDENCE_TEMPLATE", lang), getMessage("expr_domain.OPERATOR_TEMPLATE", lang));

            supplementaryQuestion.getQuestionData().setQuestionText(text);
        } catch (IllegalArgumentException ex) {
            return null;
        }

        boolean sameAnswers = false;
        HashSet<String> answerTexts = new HashSet<>();
        List<AnswerObjectEntity> answers = new ArrayList<>();
        for (AnswerObjectEntity answer : supplementaryQuestion.getAnswerObjects()) {
            try {
                String result = stringSubstitutor.replace(answer.getHyperText());
                if (answerTexts.contains(result)) {
                    sameAnswers = true;
                }

                AnswerObjectEntity newAnswer = new AnswerObjectEntity();
                answerTexts.add(result);
                newAnswer.setHyperText(result);
                newAnswer.setAnswerId(answer.getAnswerId());

                List<SupplementaryAnswerTransition> transitions = supplementaryConfig.get(failedLaw).get(answer.getDomainInfo());

                boolean isAnswerCorrect = false;
                for (SupplementaryAnswerTransition transition : transitions) {
                    String[] transitionCheckParts = transition.check.split(";");
                    boolean transitionSuit = false;
                    if (transition.check.equals("correct")) {
                        transitionSuit = true;
                    } else if (transitionCheckParts.length == 3) {
                        String subject = templates.get(transitionCheckParts[0] + "_domain_info");
                        String object = templates.get(transitionCheckParts[2] + "_domain_info");
                        if (transitionCheckParts[1].equals("before")) {
                            transitionSuit = beforeIndirect.containsMapping(subject, object);
                        } else if (transitionCheckParts[1].equals("high_precedence")) {
                            transitionSuit = highPrecedence.containsMapping(subject, object);
                        } else if (transitionCheckParts[1].equals("same_precedence")) {
                            transitionSuit = samePrecedenceLeftAssoc.containsMapping(subject, object) || samePrecedenceRightAssoc.containsMapping(subject, object)
                                    || samePrecedenceLeftAssoc.containsMapping(object, subject) || samePrecedenceRightAssoc.containsMapping(object, subject);
                        } else if (transitionCheckParts[1].equals("same_precedence_left_assoc")) {
                            transitionSuit = samePrecedenceLeftAssoc.containsMapping(subject, object);
                        } else if (transitionCheckParts[1].equals("same_precedence_right_assoc")) {
                            transitionSuit = samePrecedenceRightAssoc.containsMapping(subject, object);
                        } else {
                            throw new IllegalStateException("Supplementary answer correctness check verb is not supported");
                        }
                    }

                    if (transitionSuit) {
                        newAnswer.setDomainInfo(transition.question);
                        isAnswerCorrect = transition.correct;
                        if (!isAnswerCorrect) {
                            BackendFactEntity mistake = new BackendFactEntity(String.valueOf(answer.getAnswerId()), "detailed_law", transition.detailed_law);
                            possibleViolationFacts.add(mistake);
                        } else {
                            BackendFactEntity mistake = new BackendFactEntity(String.valueOf(answer.getAnswerId()), "is_correct", "");
                            possibleViolationFacts.add(mistake);
                        }
                        break;
                    }
                }

                if (newAnswer.getDomainInfo() == null) {
                    throw new IllegalStateException("Supplementary answer correctness check failed");
                }

                // skip question with same answers
                if (sameAnswers && isAnswerCorrect) {
                    ViolationEntity violationEntity = new ViolationEntity();
                    violationEntity.setLawName(newAnswer.getDomainInfo());
                    return makeSupplementaryQuestion(originalQuestion, violationEntity, lang);
                }

                answers.add(newAnswer);
            } catch (IllegalArgumentException ex) {
                // pass, this variant should not be used
            }
        }
        supplementaryQuestion.setAnswerObjects(answers);
        supplementaryQuestion.getQuestionData().setSolutionFacts(possibleViolationFacts);
        return supplementaryQuestion;
    }

    @Override
    public InterpretSentenceResult judgeSupplementaryQuestion(Question question, AnswerObjectEntity answer) {
        InterpretSentenceResult interpretSentenceResult = new InterpretSentenceResult();

        interpretSentenceResult.violations = new ArrayList<>();
        interpretSentenceResult.isAnswerCorrect = true;

        ViolationEntity violationEntity = new ViolationEntity();
        if (answer.getDomainInfo() != null) {
            violationEntity.setLawName(answer.getDomainInfo());
        }

        List<BackendFactEntity> violationFacts = new ArrayList<>();
        violationFacts.addAll(question.getSolutionFacts());

        for (BackendFactEntity fact : question.getSolutionFacts()) {
            if (fact.getSubject().equals(String.valueOf(answer.getAnswerId()))) {
                if (fact.getVerb().equals("detailed_law")) {
                    violationEntity.setDetailedLawName(fact.getObject());
                    interpretSentenceResult.isAnswerCorrect = false;
                    if (violationEntity.getLawName() == null) {
                        violationEntity.setLawName(fact.getObject());
                    }
                } else if (fact.getVerb().equals("text") || fact.getVerb().equals("index")) {
                    violationFacts.add(fact);
                }
            }
        }

        violationFacts.addAll(question.getSolutionFacts());
        violationEntity.setViolationFacts(violationFacts);
        if (violationEntity.getLawName() != null || violationEntity.getDetailedLawName() != null) {
            interpretSentenceResult.violations.add(violationEntity);
        }

        return interpretSentenceResult;
    }

    @Override
    public InterpretSentenceResult interpretSentence(List<BackendFactEntity> violations) {
        List<ViolationEntity> mistakes = new ArrayList<>();

        String questionType = "";
        for (BackendFactEntity violation : violations) {
            if (violation.getVerb().equals("student_error_more_precedence")
                    || violation.getVerb().equals("student_error_left_assoc")
                    || violation.getVerb().equals("student_error_right_assoc")
                    || violation.getVerb().equals("student_error_strict_operands_order")
                    || violation.getVerb().equals("student_error_unevaluated_operand")
                    || violation.getVerb().equals("student_error_early_finish")
                    || violation.getVerb().equals("student_error_in_complex")) {
                questionType = EVALUATION_ORDER_QUESTION_TYPE;
            }
        }

        if (questionType.equals(EVALUATION_ORDER_QUESTION_TYPE)) {
            // retrieve subjects' info from facts ...
            Map<String, BackendFactEntity> nameToText = new HashMap<>();
            Map<String, BackendFactEntity> nameToPos = new HashMap<>();
            Map<String, BackendFactEntity> nameToBeforeThirdOperator = new HashMap<>();

            for (BackendFactEntity violation : violations) {
                if (violation.getVerb().equals("text")) {
                    nameToText.put(violation.getSubject(), violation);
                } else if (violation.getVerb().equals("index")) {
                    nameToPos.put(violation.getSubject(), violation);
                } else if (violation.getVerb().equals("before_third_operator")) {
                    nameToBeforeThirdOperator.put(violation.getSubject(), violation);
                }
            }

            // filter facts and fill mistakes list ...
            for (BackendFactEntity violation : violations) {
                ViolationEntity violationEntity = new ViolationEntity();
                if (violation.getVerb().equals("student_error_more_precedence")) {
                    if (getIndexFromName(violation.getSubject(), false).orElse(0) > getIndexFromName(violation.getObject(), false).orElse(0)) {
                        violationEntity.setLawName("error_base_higher_precedence_left");
                    } else {
                        violationEntity.setLawName("error_base_higher_precedence_right");
                    }
                } else if (violation.getVerb().equals("student_error_left_assoc")) {
                    violationEntity.setLawName("error_base_same_precedence_left_associativity_left");
                } else if (violation.getVerb().equals("student_error_right_assoc")) {
                    violationEntity.setLawName("error_base_same_precedence_right_associativity_right");
                } else if (violation.getVerb().equals("student_error_strict_operands_order")) {
                    violationEntity.setLawName("error_base_student_error_strict_operands_order");
                } else if (violation.getVerb().equals("student_error_unevaluated_operand")) {
                    violationEntity.setLawName("error_base_student_error_unevaluated_operand");
                } else if (violation.getVerb().equals("student_error_early_finish")) {
                    violationEntity.setLawName("error_base_student_error_early_finish");
                } else if (violation.getVerb().equals("student_error_in_complex")) {
                    violationEntity.setLawName("error_base_student_error_in_complex");
                } else if (violation.getVerb().equals("wrong_type")) {
                    violationEntity.setLawName("error_wrong_type");
                }
                if (violationEntity.getLawName() != null) {
                    ArrayList<BackendFactEntity> facts = new ArrayList<>(Arrays.asList(
                            violation,
                            nameToText.get(violation.getObject()),
                            nameToText.get(violation.getSubject()),
                            nameToPos.get(violation.getObject()),
                            nameToPos.get(violation.getSubject())));
                    if (nameToBeforeThirdOperator.containsKey(violation.getObject())) {
                        facts.add(nameToBeforeThirdOperator.get(violation.getObject()));
                        facts.add(nameToPos.get(nameToBeforeThirdOperator.get(violation.getObject()).getObject()));
                        facts.add(nameToText.get(nameToBeforeThirdOperator.get(violation.getObject()).getObject()));
                    }
                    violationEntity.setViolationFacts(facts);
                    mistakes.add(violationEntity);
                }
            }
        }

        InterpretSentenceResult result = new InterpretSentenceResult();
        result.violations = mistakes;
        result.correctlyAppliedLaws = calculateCorrectlyAppliedLaws(violations);
        result.isAnswerCorrect = mistakes.isEmpty();

        ProcessSolutionResult processResult = processSolution(violations);
        result.CountCorrectOptions = processResult.CountCorrectOptions;
        result.IterationsLeft = processResult.IterationsLeft + (result.isAnswerCorrect ? 0 : 1);
        return result;
    }

    List<String> calculateCorrectlyAppliedLaws(List<BackendFactEntity> violations) {
        List<String> result = new ArrayList<>();

        Map<String, Integer> nameToStudentPos = new HashMap<>();
        Integer maxStudentPos = -1;
        for (BackendFactEntity violation : violations) {
            if (violation.getVerb().equals("student_pos_number")) {
                Integer studentPosNumber = Integer.parseInt(violation.getObject());
                maxStudentPos = max(maxStudentPos, studentPosNumber);
                nameToStudentPos.put(violation.getSubject(), studentPosNumber);
            }
        }

        for (BackendFactEntity violation : violations) {
            // Consider only errors that could happen at current step and where current token will be error reason
            if (!nameToStudentPos.getOrDefault(violation.getObject(), -2).equals(maxStudentPos) ||
                    nameToStudentPos.containsKey(violation.getSubject())) {
                continue;
            }
            String correctlyAppliedLaw = null;
            if (violation.getVerb().equals("student_error_more_precedence_base")) {
                if (getIndexFromName(violation.getSubject(), false).orElse(0) > getIndexFromName(violation.getObject(), false).orElse(0)) {
                    correctlyAppliedLaw = "error_single_token_binary_operator_has_unevaluated_higher_precedence_left";
                } else {
                    correctlyAppliedLaw = "error_single_token_binary_operator_has_unevaluated_higher_precedence_right";
                }
            } else if (violation.getVerb().equals("student_error_left_assoc_base")) {
                correctlyAppliedLaw = "error_single_token_binary_operator_has_unevaluated_same_precedence_left_associativity_left";
            } else if (violation.getVerb().equals("student_error_right_assoc_base")) {
                correctlyAppliedLaw = "error_single_token_binary_operator_has_unevaluated_same_precedence_right_associativity_right";
            }
            if (correctlyAppliedLaw != null) {
                result.add(correctlyAppliedLaw);
            }
        }

        return result;
    }

    @Override
    public ArrayList<HyperText> makeExplanation(List<ViolationEntity> mistakes, FeedbackType feedbackType) {
        ArrayList<HyperText> result = new ArrayList<>();
        for (ViolationEntity mistake : mistakes) {
            result.add(makeExplanation(mistake, feedbackType));
        }
        return result;
    }

    private static String getOperatorTextDescription(String errorText) {
        if (errorText.equals("(")) {
            return "parenthesis ";
        } else if (errorText.equals("[")) {
            return "brackets ";
        } else if (errorText.contains("(")) {
            return "function call ";
        }
        return "operator ";
    }

    private HyperText makeExplanation(ViolationEntity mistake, FeedbackType feedbackType) {

        // retrieve subjects' info from facts, and find base and third ...
        BackendFactEntity base = null;
        BackendFactEntity third = null;
        Map<String, String> nameToText = new HashMap<>();
        Map<String, String> nameToPos = new HashMap<>();
        for (BackendFactEntity fact : mistake.getViolationFacts()) {
            if (fact.getVerb().equals("before_third_operator")) {
                third = fact;
            } else if (fact.getVerb().equals("index")) {
                nameToPos.put(fact.getSubject(), fact.getObject());
            } else if (fact.getVerb().equals("text")) {
                nameToText.put(fact.getSubject(), fact.getObject());
            } else {
                base = fact;
            }
        }

        String errorText = nameToText.get(base.getSubject());
        String reasonText = nameToText.get(base.getObject());

        String thirdOperatorPos = third == null ? "" : nameToPos.get(third.getObject());
        String thirdOperatorText = third == null ? "" : nameToText.get(third.getObject());

        String reasonPos = nameToPos.get(base.getObject());
        String errorPos = nameToPos.get(base.getSubject());

        String what = getOperatorTextDescription(reasonText) + reasonText + " at pos " + reasonPos
                + " should be evaluated before " + getOperatorTextDescription(errorText) + errorText + " at pos " + errorPos;
        String reason = "";

        String errorType = mistake.getLawName();

        if (errorType.equals("error_single_token_binary_operator_has_unevaluated_higher_precedence_left") ||
                errorType.equals("error_single_token_binary_operator_has_unevaluated_higher_precedence_right")) {
            reason = " because " + getOperatorTextDescription(reasonText) + reasonText + " has higher precedence";
        } else if (errorType.equals("error_single_token_binary_operator_has_unevaluated_same_precedence_left_associativity_left") && errorText.equals(reasonText)) {
            reason = " because " + getOperatorTextDescription(reasonText) + reasonText + " has left associativity and is evaluated from left to right";
        } else if (errorType.equals("error_single_token_binary_operator_has_unevaluated_same_precedence_left_associativity_left")) {
            reason = " because " + getOperatorTextDescription(reasonText) + reasonText + " has the same precedence and left associativity";
        } else if (errorType.equals("error_single_token_binary_operator_has_unevaluated_same_precedence_right_associativity_right") && errorText.equals(reasonText)) {
            reason = " because " + getOperatorTextDescription(reasonText) + reasonText + " has right associativity and is evaluated from right to left";
        } else if (errorType.equals("error_single_token_binary_operator_has_unevaluated_same_precedence_right_associativity_right")) {
            reason = " because " + getOperatorTextDescription(reasonText) + reasonText + " has the same precedence and right associativity";
//        } else if (error.Type == StudentErrorType.IN_COMPLEX && errorText.equals("(")) {
//            reason = " because function arguments are evaluated before function call​";
//        } else if (error.Type == StudentErrorType.IN_COMPLEX && errorText.equals("[")) {
//            reason = " because expression in brackets is evaluated before brackets";
//        } else if (error.Type == StudentErrorType.IN_COMPLEX && thirdOperatorText.equals("(")) {
//            reason = " because expression in parenthesis is evaluated before operators​ outside of them";
//        } else if (error.Type == StudentErrorType.IN_COMPLEX && thirdOperatorText.equals("[")) {
//            reason = " because expression in brackets is evaluated before operator outside of them​​";
//        } else if (error.Type == StudentErrorType.STRICT_OPERANDS_ORDER) {
//            reason = " because the left operand of the " + getOperatorTextDescription(thirdOperatorText) + thirdOperatorText + " at pos " + thirdOperatorPos + " must be evaluated before its right operand​";
        } else {
            reason = " because unknown error";
        }

        return new HyperText(what + "\n" + reason);
    }
}
