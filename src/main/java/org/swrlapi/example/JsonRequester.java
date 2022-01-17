package org.swrlapi.example;

import com.google.gson.Gson;
import org.vstu.compprehension.models.businesslogic.domains.Domain;
import org.vstu.compprehension.models.businesslogic.domains.ProgrammingLanguageExpressionDomain;
import org.vstu.compprehension.models.entities.AnswerObjectEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.swrlapi.example.OntologyUtil.*;

class MessageToken {
    String text;
    Integer check_order;
    String status;
    Boolean enabled;
}

class Message {
    List<MessageToken> expression;
    List<OntologyUtil.Error> errors;
    String lang;
    String task_lang;
    String action;
}

public class JsonRequester {
    // {"expression":[{"text":"a"},{"text":"["},{"text":"i"},{"text":"+"},{"text":"1"},{"text":"]"},{"text":"["},{"text":"j"},{"text":"]"}],"errors":[],"lang":"en", "task_lang":"cpp"}
    // {"expression":[{"text":"a"},{"text":"?"},{"text":"("},{"text":"b"},{"text":","},{"text":"c"},{"text":")"},{"text":":"},{"text":"f"},{"text":"("},{"text":"b"},{"text":","},{"text":"c"},{"text":")"}],"errors":[],"lang":"en", "task_lang":"cpp"}
    // {"expression":[{"text":"a"},{"text":"."},{"text":"b"},{"text":"("},{"text":"c"},{"text":"+"},{"text":"1"},{"text":","},{"text":"d"},{"text":")"}],"errors":[],"lang":"en", "task_lang":"cpp"}
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
        if(message == null) {
            message = new Message();
        }
        if(message.expression == null) {
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
        message.errors = new ArrayList<>();

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

        String programmingLanguage;
        if (message.task_lang.equals("cpp")) {
            programmingLanguage = "C++";
        } else if (message.task_lang.equals("cs")) {
            programmingLanguage = "C#";
        } else {
            programmingLanguage = "Python";
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
            Domain.CorrectAnswer correctAnswer = new ProgrammingLanguageExpressionDomain().getAnyNextCorrectAnswer(helper.getQuestion());
            pos = 0;
            for (AnswerObjectEntity answer : helper.getQuestion().getAnswerObjects()) {
                if (answer.getDomainInfo().equals(correctAnswer.answer.getDomainInfo())) {
                    message.expression.get(pos).enabled = false;
                    message.expression.get(pos).status = "suggested";
                    message.expression.get(pos).check_order = last_check_order + 1;
                    return new Gson().toJson(message);
                }
                pos++;
            }
        }

        return new Gson().toJson(message);
    }
}
