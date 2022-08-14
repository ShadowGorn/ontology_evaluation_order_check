package org.vstu.compprehension.models.businesslogic.domains;

import org.vstu.compprehension.models.businesslogic.*;
import org.vstu.compprehension.models.entities.*;
import org.vstu.compprehension.models.entities.BackendFactEntity;
import org.vstu.compprehension.models.entities.EnumData.FeedbackType;
import org.vstu.compprehension.models.entities.EnumData.Language;
import org.vstu.compprehension.utils.HyperText;

import java.util.*;

public abstract class Domain {
    public static final String NAME_PREFIX_IS_HUMAN = "[human]";
    protected List<PositiveLaw> positiveLaws;
    protected List<NegativeLaw> negativeLaws;
    protected List<Concept> concepts;

    protected DomainEntity domainEntity;

    protected String name = "";
    protected String version = "";

    public abstract void update();

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<PositiveLaw> getPositiveLaws() {
        return positiveLaws;
    }

    public List<NegativeLaw> getNegativeLaws() {
        return negativeLaws;
    }

    public List<Concept> getConcepts() {
        return concepts;
    }

    public PositiveLaw getPositiveLaw(String name) {
        for (PositiveLaw law : positiveLaws) {
            if (name.equals(law.getName())) {
                return law;
            }
        }
        return null;
    }

    public NegativeLaw getNegativeLaw(String name) {
        for (NegativeLaw law : negativeLaws) {
            if (name.equals(law.getName())) {
                return law;
            }
        }
        return null;
    }

    public Concept getConcept(String name) {
        for (Concept concept : concepts) {
            if (name.equals(concept.getName())) {
                return concept;
            }
        }
        return null;
    }

    public Domain() {
    }

    public abstract List<HyperText> getFullSolutionTrace(Question question);

    public abstract Question makeQuestion(QuestionRequest questionRequest, List<Tag> tags, Language userLanguage);

    public abstract ArrayList<HyperText> makeExplanation(List<ViolationEntity> mistakes, FeedbackType feedbackType);

    public List<Law> getQuestionLaws(String questionDomainType, List<Tag> tags) {
        List<PositiveLaw> positiveLaws = getQuestionPositiveLaws(questionDomainType, tags);
        List<NegativeLaw> negativeLaws = getQuestionNegativeLaws(questionDomainType, tags);
        List<Law> laws = new ArrayList<>();
        laws.addAll(positiveLaws);
        laws.addAll(negativeLaws);
        return laws;
    }

    public abstract List<PositiveLaw> getQuestionPositiveLaws(String questionDomainType, List<Tag> tags);

    public abstract List<NegativeLaw> getQuestionNegativeLaws(String questionDomainType, List<Tag> tags);

    public abstract List<String> getSolutionVerbs(String questionDomainType, List<BackendFactEntity> statementFacts);

    public abstract List<String> getViolationVerbs(String questionDomainType, List<BackendFactEntity> statementFacts);

    /**
     * Сформировать из ответов (которые были ранее добавлены к вопросу)
     * студента факты в универсальной форме
     *
     * @return - факты в универсальной форме
     */
    public abstract List<BackendFactEntity> responseToFacts(String questionDomainType, List<ResponseEntity> responses, List<AnswerObjectEntity> answerObjects);

    /**
     * Statistics for current step of question evaluation
     */
    public static class ProcessSolutionResult {
        /**
         * Number of correct variants at current step
         */
        public int CountCorrectOptions;
        /**
         * Shortest number of steps (iterations) left
         */
        public int IterationsLeft;
    }

    /**
     * Info about one iteration
     */
    public static class InterpretSentenceResult extends ProcessSolutionResult {
        /**
         * All violations
         */
        public List<ViolationEntity> violations;
        /**
         * List of all negative laws that not occurred
         * (all answers where this answer would be the cause of the violation)
         */
        public List<String> correctlyAppliedLaws;
        /**
         * Is answer on question is correct.
         * Supplementary can generate new violations even on correct variant.
         */
        public boolean isAnswerCorrect;
    }

    /**
     * Сформировать из найденных Backend'ом фактов объекты нарушений
     */
    public abstract InterpretSentenceResult interpretSentence(List<BackendFactEntity> violations);

    /**
     * Check that violation has supplementary questions
     *
     * @param violation info about mistake
     * @return violation has supplementary questions
     */
    public abstract boolean needSupplementaryQuestion(ViolationEntity violation);

    /**
     * Make supplementary question based on violation in last iteration
     *
     * @param violation      info about mistake
     * @param sourceQuestion source question
     * @return supplementary question
     */
    public abstract Question makeSupplementaryQuestion(Question sourceQuestion, ViolationEntity violation, String lang);

    public abstract InterpretSentenceResult judgeSupplementaryQuestion(Question question, AnswerObjectEntity answer);

    public abstract ProcessSolutionResult processSolution(List<BackendFactEntity> solution);

    public class CorrectAnswer {
        public AnswerObjectEntity answer;
        public HyperText explanation;
        public String lawName;
    }

    public abstract CorrectAnswer getAnyNextCorrectAnswer(Question q);

    protected abstract List<Question> getQuestionTemplates();

    /**
     * Find a question template in in-memory suite of Domain's `questions`
     *
     * @param tags               question tags
     * @param targetConcepts     concepts that should be in question
     * @param deniedConcepts     concepts that should not be in question
     * @param targetNegativeLaws negative laws that should be in question
     * @param deniedNegativeLaws negative laws that should not be in question
     * @param forbiddenQuestions texts of question that not suit TODO: use ExerciseAttemptEntity
     * @return new question template
     */
    public Question findQuestion(List<Tag> tags, HashSet<String> targetConcepts, HashSet<String> deniedConcepts, HashSet<String> targetNegativeLaws, HashSet<String> deniedNegativeLaws, HashSet<String> forbiddenQuestions) {
        List<Question> questions = new ArrayList<>();

        int maxSuitCount = 0;
        int minAdditionalCount = 10000;
        for (Question q : getQuestionTemplates()) {
            int targetConceptCount = 0;
            int anotherConcepts = 0;
            boolean suit = true;
            if (forbiddenQuestions.contains(q.getQuestionName()) || forbiddenQuestions.contains(NAME_PREFIX_IS_HUMAN + q.getQuestionName())) {
                continue;
            }
            for (Tag tag : tags) {
                if (!q.getTags().contains(tag.getName())) {
                    suit = false;
                    break;
                }
            }
            if (!suit) continue;
            for (String concept : q.getConcepts()) {
                if (deniedConcepts.contains(concept)) {
                    suit = false;
                    break;
                } else if (targetConcepts.contains(concept)) {
                    targetConceptCount++;
                } else {
                    anotherConcepts++;
                }
            }
            if (!suit) continue;
            for (String negativeLaw : q.getNegativeLaws()) {
                if (deniedNegativeLaws.contains(negativeLaw)) {
                    suit = false;
                    break;
                } else if (targetNegativeLaws.contains(negativeLaw)) {
                    targetConceptCount++;
                } else {
                    anotherConcepts++;
                }
            }
            if (suit) {
                if (targetConceptCount > maxSuitCount || targetConceptCount == maxSuitCount && anotherConcepts <= minAdditionalCount) {
                    if (targetConceptCount > maxSuitCount || anotherConcepts < minAdditionalCount) {
                        questions.clear();
                        maxSuitCount = targetConceptCount;
                        minAdditionalCount = anotherConcepts;
                    }
                    questions.add(q);
                }
            }
        }
        if (questions.isEmpty()) {
            return null;
        } else {
            for (Question question : questions) {
                //log.info("Отобранный вопрос (из " + questions.size() + "): " + question.getQuestionName());
            }

            Question question = questions.get(new Random().nextInt(questions.size()));
            //log.info("В итоге, взят вопрос: " + question.getQuestionName());

            ///
            /// add a mark to the question's name: this question is made by human.
            //if (question.getQuestionName() != null && ! question.getQuestionName().startsWith(NAME_PREFIX_IS_HUMAN) ) {
            //    question.getQuestionData().setQuestionName(NAME_PREFIX_IS_HUMAN + question.getQuestionName());
            //}
            ///

            return question;
        }
    }
}
