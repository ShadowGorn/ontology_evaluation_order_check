package org.swrlapi.example;

import com.google.gson.Gson;
import org.vstu.compprehension.models.businesslogic.Question;
import org.vstu.compprehension.models.businesslogic.domains.Domain;
import org.vstu.compprehension.models.businesslogic.domains.ProgrammingLanguageExpressionDomain;
import org.vstu.compprehension.models.entities.AnswerObjectEntity;
import org.vstu.compprehension.models.entities.BackendFactEntity;
import org.vstu.compprehension.models.entities.ViolationEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.swrlapi.example.OntologyUtil.*;

class MessageToken {
    String text;
    Integer check_order;
    String status;
    String additional_info;
    Boolean enabled;
}

class Message {
    List<MessageToken> expression;
    List<MessageToken> answers;
    List<OntologyUtil.Error> errors;
    String lang;
    String task_lang;
    String action;
    String type;
    String text;
}

public class JsonRequester {
    // {"expression":[{"text":"a"},{"text":"["},{"text":"i"},{"text":"+"},{"text":"1"},{"text":"]"},{"text":"["},{"text":"j"},{"text":"]"}],"errors":[],"lang":"en", "task_lang":"cpp"}
    // {"expression":[{"text":"a"},{"text":"?"},{"text":"("},{"text":"b"},{"text":","},{"text":"c"},{"text":")"},{"text":":"},{"text":"f"},{"text":"("},{"text":"b"},{"text":","},{"text":"c"},{"text":")"}],"errors":[],"lang":"en", "task_lang":"cpp"}
    // {"expression":[{"text":"a"},{"text":"."},{"text":"b"},{"text":"("},{"text":"c"},{"text":"+"},{"text":"1"},{"text":","},{"text":"d"},{"text":")"}],"errors":[],"lang":"en", "task_lang":"cpp"}
    // {"expression":[{"text":"a","check_order":1000,"enabled":false},{"text":".","check_order":1000,"enabled":true},{"text":"b","check_order":1000,"enabled":false},{"text":"(","check_order":1,"status":"wrong","enabled":true},{"text":"c","check_order":1000,"enabled":false},{"text":"+","check_order":1000,"enabled":true},{"text":"1","check_order":1000,"enabled":false},{"text":",","check_order":1000,"enabled":false},{"text":"d","check_order":1000,"enabled":false},{"text":")","check_order":1000,"enabled":false}],"errors":[{"parts":[{"text":"operator + at pos 6","type":"operator","index":6},{"text":"should be evaluated before","type":"plain_text"},{"text":"parenthesis ( at pos 4,","type":"operator","index":4},{"text":"because","type":"plain_text"},{"text":"function arguments","type":"term"},{"text":"are evaluated before","type":"plain_text"},{"text":"function call","type":"term"}]}],"lang":"en","task_lang":"cpp","action":"get_supplement","type":"main"}
    // {"expression":[{"text":"a","check_order":1000,"enabled":false},{"text":".","check_order":1000,"enabled":true},{"text":"b","check_order":1000,"enabled":false},{"text":"(","check_order":1000,"enabled":true},{"text":"c","check_order":1000,"enabled":false},{"text":"+","check_order":1,"status":"wrong","enabled":true},{"text":"1","check_order":1000,"enabled":false},{"text":"*","check_order":1000,"enabled":true},{"text":"d","check_order":1000,"enabled":false},{"text":")","check_order":1000,"enabled":false}],"errors":[{"parts":[{"text":"operator * at pos 8","type":"operator","index":8},{"text":"should be evaluated before","type":"plain_text"},{"text":"operator + at pos 6,","type":"operator","index":6},{"text":"because","type":"plain_text"},{"text":"operator *","type":"operator","index":8},{"text":"has higher","type":"plain_text"},{"text":"precedence","type":"term"}],"type":"error_base_higher_precedence_right"}],"lang":"en","task_lang":"cpp","action":"get_supplement","type":"main"}
    // {"expression":[{"text":"a","check_order":1000,"enabled":false},{"text":".","check_order":1000,"enabled":true},{"text":"b","check_order":1000,"enabled":false},{"text":"(","check_order":1000,"enabled":true},{"text":"c","check_order":1000,"enabled":false},{"text":"+","check_order":1,"enabled":true},{"text":"1","check_order":1000,"enabled":false},{"text":"*","check_order":1000,"enabled":true},{"text":"d","check_order":1000,"enabled":false},{"text":")","check_order":1000,"enabled":false}],"answers":[{"text":"precedence of operator c at pos 5","status":"correct","additional_info":"select_highest_precedence_left_operator","enabled":true},{"text":"associativity of operator c at pos 5","status":"wrong","additional_info":"select_precedence_or_associativity_left_influence","enabled":true},{"text":"precedence of operator 1 at pos 7","status":"correct","additional_info":"select_highest_precedence_right_operator","enabled":true},{"text":"associativity of operator 1 at pos 7","status":"wrong","additional_info":"select_precedence_or_associativity_right_influence","enabled":true}],"errors":[{"parts":[], "type":"select_precedence_or_associativity_right_influence"}],"lang":"en","task_lang":"cpp","action":"get_supplement","type":"supplementary","text":"What prevents evaluation of operator + at pos 6 ?"}
    // {"expression":[{"text":"a","check_order":1000,"enabled":false},{"text":".","check_order":1000,"enabled":true},{"text":"b","check_order":1000,"enabled":false},{"text":"(","check_order":1000,"enabled":true},{"text":"c","check_order":1000,"enabled":false},{"text":"+","check_order":1,"enabled":true},{"text":"1","check_order":1000,"enabled":false},{"text":"*","check_order":1000,"enabled":true},{"text":"d","check_order":1000,"enabled":false},{"text":")","check_order":1000,"enabled":false}],"answers":[{"text":"precedence","status":"correct","additional_info":"select_highest_precedence_right_operator","enabled":true},{"text":"associativity","status":"wrong","additional_info":"error_select_precedence_or_associativity_left","enabled":true}],"errors":[{"parts":[], "type":"select_highest_precedence_right_operator"}],"lang":"en","task_lang":"cpp","action":"get_supplement","type":"supplementary","text":"What influences evaluation order at first?"}

    public String response(String request) {
        Message message;

        try {
            message = new Gson().fromJson(
                    request,
                    Message.class);
        } catch (java.lang.Exception xcp) {
            message = new Message();
        }

        List<String> expression = new ArrayList<>();
        int pos = 0;
        if (message == null) {
            message = new Message();
        }
        if (message.expression == null) {
            message.expression = new ArrayList<>();
        }
        if (message.lang == null) {
            message.lang = "en";
        }
        if (message.task_lang == null) {
            message.task_lang = "cpp";
        }
        if (message.action == null) {
            message.action = "find_errors";
        }
        if (message.type == null) {
            message.type = "main";
        }

        if (message.action.equals("supported_languages")){
            MessageToken token = new MessageToken();
            token.text = "en";
            message.expression.add(token);
            token = new MessageToken();
            token.text = "ru";
            message.expression.add(token);
            return new Gson().toJson(message);
        } else if (message.action.equals("supported_task_languages")) {
            MessageToken token = new MessageToken();
            token.text = "cpp";
            message.expression.add(token);
            token = new MessageToken();
            token.text = "cs";
            message.expression.add(token);
            token = new MessageToken();
            token.text = "python";
            message.expression.add(token);
            return new Gson().toJson(message);
        }

        String programmingLanguage;
        if (message.task_lang.equals("cpp")) {
            programmingLanguage = "C++";
        } else if (message.task_lang.equals("cs")) {
            programmingLanguage = "C#";
        } else {
            programmingLanguage = "Python";
        }

        for (MessageToken token : message.expression) {
            if (token.status != null && token.status.equals("wrong")) {
                token.status = null;
            }
            if (token.check_order == null) {
                token.check_order = 1000;
            }
            if (token.text == null) {
                token.text = "";
            }

            String part = token.text;
            expression.add(part);
            pos = pos + 1;
        }

        int last_check_order = 0;
        for (MessageToken token : message.expression) {
            if (token.check_order != 1000) {
                last_check_order = Math.max(last_check_order, token.check_order);
            }
        }

        Expression expr = new Expression(expression);
        pos = 0;
        for (MessageToken token : message.expression) {
            expr.getTerms().get(pos).setStudentPos(token.check_order);
            pos = pos + 1;
        }

        if (message.action.equals("get_supplement")) {
            message.type = "supplementary";
            if (message.errors != null && message.errors.size() > 0) {
                message.answers = new ArrayList<>();
                String lawName = message.errors.get(0).type;
                OntologyHelper helper = new OntologyHelper(expr, programmingLanguage, message.lang, lawName);
                Question q = helper.getQuestion();

                if (q == null) {
                    message.type = "no_supplementary";
                    return new Gson().toJson(message);
                }

                message.text = q.getQuestionText().getText();
                for (AnswerObjectEntity answer : q.getAnswerObjects()) {
                    MessageToken token = new MessageToken();
                    token.text = answer.getHyperText();
                    token.enabled = true;
                    Domain.InterpretSentenceResult res = helper.judgeSupplementaryQuestion(answer);

                    token.status = res.isAnswerCorrect ? "correct" : "wrong";
                    token.additional_info = res.violations.get(0).getLawName();
                    message.answers.add(token);
                }
                message.errors = new ArrayList<>();
            }
            return new Gson().toJson(message);
        }

        message.errors = new ArrayList<>();
        message.type = "main";

        OntologyHelper helper = new OntologyHelper(expr, programmingLanguage);
        Set<Integer> tokenGood = getGoodPositions(helper);

        pos = 0;
        for (MessageToken token : message.expression) {
            pos = pos + 1;
            if (!tokenGood.contains(pos - 1)) {
                if (message.lang.equals("ru")) {
                    OntologyUtil.Error result = new OntologyUtil.Error();
                    result.add(new ErrorPart(
                            "Токен на позиции " + (pos),
                            "operator",
                            pos
                    )).add(new ErrorPart(
                            "не распознан, возможно не все части разделены пробелами или оператор не поддержан"
                    ));
                    message.expression.get(pos - 1).status = "wrong";
                    message.errors.add(result);
                } else {
                    OntologyUtil.Error result = new OntologyUtil.Error();
                    result.add(new ErrorPart(
                            "Token at pos " + (pos),
                            "operator",
                            pos
                    )).add(new ErrorPart(
                            "is not correct, try to separate all parts with spaces or operator is not supported"
                    ));
                    message.expression.get(pos - 1).status = "wrong";
                    message.errors.add(result);
                }

            }
        }

        pos = 0;
        Set<Integer> operandsPos = getOperandPositions(helper);
        for (MessageToken token : message.expression) {
            token.enabled = !operandsPos.contains(pos);
            expr.getTerms().get(pos).setStudentPos(token.check_order);
            pos = pos + 1;
        }

        if (message.action.equals("find_errors")) {
            Set<StudentError> errors = GetErrors(helper, false);
            for (StudentError error : errors) {
                OntologyUtil.Error text = getErrorDescription(error, helper, message.lang);
                for (ViolationEntity violation : helper.judgeQuestion().violations) {
                    if (helper.needSupplementaryQuestion(violation)) {
                        text.type = violation.getLawName();
                        break;
                    }
                }

                message.errors.add(text);
                message.expression.get(error.getErrorPos() - 1).status = "wrong";
            }

            if (errors.isEmpty()) {
                for (MessageToken token : message.expression) {
                    if (token.check_order != 1000 && token.check_order != 0) {
                        token.enabled = false;
                        token.status = "correct";
                    }
                }
            }

            return new Gson().toJson(message);
        } else if (message.action.equals("next_step")) {
            Domain.CorrectAnswer correctAnswer = new ProgrammingLanguageExpressionDomain().getAnyNextCorrectAnswer(helper.getQuestion(), message.lang);
            if (correctAnswer == null) {
                return new Gson().toJson(message);
            }
            pos = 0;
            for (AnswerObjectEntity answer : helper.getQuestion().getAnswerObjects()) {
                if (answer.getDomainInfo().equals(correctAnswer.answer.getDomainInfo())) {
                    for (MessageToken token : message.expression) {
                        if (token.check_order != 1000 && token.check_order != 0) {
                            token.enabled = false;
                            token.status = "correct";
                        }
                    }

                    message.expression.get(pos).enabled = false;
                    message.expression.get(pos).status = "suggested";
                    message.expression.get(pos).check_order = last_check_order + 1;

                    OntologyUtil.Error error = new OntologyUtil.Error();
                    OntologyUtil.ErrorPart errorPart = new ErrorPart(correctAnswer.explanation.getText());
                    message.errors.add(error.add(errorPart));

                    return new Gson().toJson(message);
                }
                pos++;
            }
        }

        return new Gson().toJson(message);
    }
}
