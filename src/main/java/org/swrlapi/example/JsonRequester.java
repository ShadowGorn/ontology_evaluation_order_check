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
    List<String> errors;
}

public class JsonRequester {
    static HashMap<String, List<org.swrlapi.example.Relation>> relationsCache = new HashMap<>();

    public String response(String request) {
        Message message = new Gson().fromJson(
                request,
                Message.class);

        List<String> expression = new ArrayList<>();
        String cacheKey = "";
        Integer maxSetPos = null;
        int pos = 0;
        for (MessageToken token : message.expression) {
            token.status = null;
            if (token.check_order == null) {
                token.check_order = 1000;
            } else if (token.check_order != 0 && token.check_order != 1000) {
                if (maxSetPos == null || token.check_order > maxSetPos) {
                    maxSetPos = pos;
                }
            }
            if (token.text == null) {
                token.text = "";
            }

            String part = token.text + "$" + token.check_order;
            expression.add(part);
            cacheKey += part + " ";
            pos = pos + 1;
        }

        Expression expr = new Expression(expression);

        if (!relationsCache.containsKey(cacheKey)) {
            OntologyHelper helper = new OntologyHelper(expr);
            relationsCache.put(cacheKey, GetRelations(helper));
            Set<Integer> functionCallPos = getFunctionCallPositions(helper);
            Set<Integer> operandsPos = getOperandPositions(helper);

            pos = 0;
            for (MessageToken token : message.expression) {
                boolean isParenthesis = token.text.equals("(") && !functionCallPos.contains(pos);
                if (token.check_order == 1000 && isParenthesis) {
                    expr.getTerms().get(pos).setStudentPos(0);
                    token.check_order = 0;
                }
                token.enabled = !(operandsPos.contains(pos) || isParenthesis);
                pos = pos + 1;
            }
        }

        OntologyHelper helperErrors = new OntologyHelper(expr, relationsCache.get(cacheKey));
        Set<StudentError> errors = GetErrors(helperErrors, false);
        message.errors = new ArrayList<>();
        for (StudentError error : errors) {
            String text = getErrorDescription(error, helperErrors).replace('\n', ',');
            message.errors.add(text);
            message.expression.get(error.getErrorPos() - 1).status = "wrong";
        }

        if (maxSetPos != null) {
            if (message.expression.get(maxSetPos).status == null) {
                message.expression.get(maxSetPos).status = "correct";
            }
        }

        if (errors.isEmpty()) {
            for (MessageToken token : message.expression) {
                if (token.check_order != 1000) {
                    token.enabled = false;
                }
            }
        }

        return new Gson().toJson(message);
    }
}
