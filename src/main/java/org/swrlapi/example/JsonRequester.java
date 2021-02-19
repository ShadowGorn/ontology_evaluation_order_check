package org.swrlapi.example;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
}

public class JsonRequester {
    static HashMap<String, List<org.swrlapi.example.Relation>> relationsCache = new HashMap<>();

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
        String cacheKey = "";
        int pos = 0;
        if(message == null) {
            message = new Message();
        }
        if(message.expression == null) {
            message.expression = new ArrayList<>();
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
            cacheKey += part + " ";
            pos = pos + 1;
        }

        Expression expr = new Expression(expression);

        if (!relationsCache.containsKey(cacheKey)) {
            OntologyHelper helper = new OntologyHelper(expr);
            relationsCache.put(cacheKey, GetRelations(helper));
            Set<Integer> operandsPos = getOperandPositions(helper);

            pos = 0;
            for (MessageToken token : message.expression) {
                token.enabled = !operandsPos.contains(pos);
                pos = pos + 1;
            }
        }

        pos = 0;
        for (MessageToken token : message.expression) {
            expr.getTerms().get(pos).setStudentPos(token.check_order);
            pos = pos + 1;
        }

        OntologyHelper helperErrors = new OntologyHelper(expr, relationsCache.get(cacheKey));
        Set<StudentError> errors = GetErrors(helperErrors, false);
        message.errors = new ArrayList<>();
        for (StudentError error : errors) {
            OntologyUtil.Error text = getErrorDescription(error, helperErrors, message.lang);
            message.errors.add(text);
            message.expression.get(error.getErrorPos() - 1).status = "wrong";
        }

        if (errors.isEmpty()) {
            for (MessageToken token : message.expression) {
                if (token.check_order != 1000) {
                    token.enabled = false;
                    token.status = "correct";
                }
            }
        }

        return new Gson().toJson(message);
    }
}
