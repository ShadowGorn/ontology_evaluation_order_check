package org.swrlapi.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.reasoner.Node;

import java.util.*;

public class OntologyUtil {
    static public Expression getExpressionFromJson(String jsonExpression) {
        return new Expression(
                new Gson().fromJson(
                        jsonExpression,
                        new TypeToken<List<String>>() {}.getType()));
    }

    class Relation {
        int indexFrom;
        List<Integer> indexesTo;
    }

    static public HashMap<Integer, Set<Integer>> getObjectPropertyRelationsByIndexFromJson(String jsonRelations, int maxIndex) {
        HashMap<Integer, Set<Integer>> result = new HashMap<>();
        for (int i = 1; i <= maxIndex; i++) {
            result.put(i, new HashSet<>());
        }

        Relation[] relations = new Gson().fromJson(
                jsonRelations,
                Relation[].class);

        for (Relation relation : relations) {
            for (Integer indexTo : relation.indexesTo) {
                result.get(relation.indexFrom).add(indexTo);
            }
        }

        return result;
    }

    static public HashMap<Integer, Set<Integer>> getObjectPropertyRelationsByIndex(OntologyHelper helper, String objectProperty) {
        return helper.getObjectPropertyRelationsByIndex(objectProperty);
    }

    static public HashMap<Integer, String> getDataProperties(OntologyHelper helper, String dataProperty) {
        return helper.getDataProperties(dataProperty);
    }

    static public Set<StudentError> GetErrors(OntologyHelper helper, boolean debug) {
        Set<StudentError> resultErrors = new HashSet<>();
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_more_precedence_left"),
                StudentErrorType.HIGH_PRIORITY_TO_LEFT);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_more_precedence_right"),
                StudentErrorType.HIGH_PRIORITY_TO_RIGHT);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_left_assoc"),
                StudentErrorType.LEFT_ASSOC_TO_LEFT);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_right_assoc"),
                StudentErrorType.RIGHT_ASSOC_TO_RIGHT);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_in_complex"),
                StudentErrorType.IN_COMPLEX);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_strict_operands_order"),
                StudentErrorType.STRICT_OPERANDS_ORDER);
        return resultErrors;
    }

    static void FillErrors(Set<StudentError> resultErrors, HashMap<Integer, Set<Integer>> errors, StudentErrorType type) {
        errors.forEach((error,reasons) -> {
            for (Integer reason : reasons) {
                resultErrors.add(new StudentError(error, reason, type));
            }
        });
    }

    static public List<org.swrlapi.example.Relation> GetRelations(OntologyHelper helper) {
        List<org.swrlapi.example.Relation> relations = new ArrayList<>();
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "before_direct"), "before_direct");
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "before_by_third_operator"), "before_by_third_operator");
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "before_third_operator"), "before_third_operator");
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "before_as_operand"), "before_as_operand");
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "has_operand"), "has_operand");
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "not_selectable"), "not_selectable");
        AddToRelations(relations, getObjectPropertyRelationsByIndex(helper, "good_token"), "good_token");
        return relations;
    }

    static void AddToRelations(List<org.swrlapi.example.Relation> relations, HashMap<Integer, Set<Integer>> props, String type) {
        for (Map.Entry<Integer, Set<Integer>> kv : props.entrySet()) {
            for (Integer to : kv.getValue()) {
                relations.add(new org.swrlapi.example.Relation(kv.getKey(), to, type));
            }
        }
    }

    static public Set<Integer> getOperandPositions(OntologyHelper helper) {
        Set<Integer> result = new HashSet<>();

        HashMap<Integer, String> props = getDataProperties(helper, "not_selectable");
        for (Map.Entry<Integer, String> kv : props.entrySet()) {
            if (!kv.getValue().isEmpty()) {
                result.add(kv.getKey() - 1);
            }
        }

        return result;
    }

    static public Set<Integer> getGoodPositions(OntologyHelper helper) {
        Set<Integer> result = new HashSet<>();

        HashMap<Integer, String> props = getDataProperties(helper, "good_token");
        for (Map.Entry<Integer, String> kv : props.entrySet()) {
            if (!kv.getValue().isEmpty()) {
                result.add(kv.getKey() - 1);
            }
        }

        return result;
    }

    static public Set<Integer> getFunctionCallPositions(OntologyHelper helper) {
        Set<Integer> result = new HashSet<>();
        HashMap<Integer, String> props = getDataProperties(helper, "is_function_call");
        for (Map.Entry<Integer, String> kv : props.entrySet()) {
            if (kv.getValue().equals("true")) {
                result.add(kv.getKey() - 1);
            }
        }
        return result;
    }

    public static String getDataProperty(OntologyHelper helper, int index, String property) {
        HashMap<Integer, String> props = helper.getDataProperties(property);
        for (Map.Entry<Integer,String> prop : props.entrySet()) {
            if (prop.getKey() == index) {
                return prop.getValue();
            }
        }
        return null;
    }

    public static class ErrorPart {
        ErrorPart(String text, String type, Integer index) {
            this.text = text;
            this.type = type;
            this.index = index;
        }
        ErrorPart(String text, String type) {
            this(text, type, null);
        }
        ErrorPart(String text) {
            this(text, "plain_text", null);
        }
        String text;
        String type;
        Integer index;
    }

    public static class Error {
        Error() {
            parts = new ArrayList<>();
        }
        Error add(ErrorPart part) {
            parts.add(part);
            return this;
        }
        List<ErrorPart> parts;
    }

    public static String getOperatorTextDescriptionRu(String errorText) {
        if (errorText.equals("(")) {
            return "скобки ";
        } else if (errorText.equals("[")) {
            return "квадратные скобки ";
        } else if (errorText.contains("(")) {
            return "вызов функции ";
        }
        return "операция ";
    }

    public static Error getErrorDescriptionRu(StudentError error, OntologyHelper helper) {
        String errorText = getDataProperty(helper, error.getErrorPos(), "text");
        String reasonText = getDataProperty(helper, error.getReasonPos(), "text");

        HashMap<Integer, Set<Integer>> thirdOperators = getObjectPropertyRelationsByIndex(helper, "before_third_operator");
        Set<Integer> thirdOperatorPoss = thirdOperators.getOrDefault(error.getReasonPos(), new HashSet<>());
        Integer thirdOperatorPos = thirdOperatorPoss.isEmpty() ? -1 : thirdOperatorPoss.iterator().next();
        String thirdOperatorText = getDataProperty(helper, thirdOperatorPos, "text");

        int reasonPos = error.getReasonPos();
        int errorPos = error.getErrorPos();

        Error result = new Error();
        result.add(new ErrorPart(
                getOperatorTextDescriptionRu(reasonText) + reasonText + " в позиции " + reasonPos,
                "operator",
                reasonPos)
        ).add(new ErrorPart(
                "должна выполниться раньше, чем"
        )).add(new ErrorPart(
                getOperatorTextDescriptionRu(errorText) + errorText + " в позиции " + errorPos + ",",
                "operator",
                errorPos
        )).add(new ErrorPart(
                "потому что"
        ));

        if (error.Type == StudentErrorType.HIGH_PRIORITY_TO_LEFT || error.Type == StudentErrorType.HIGH_PRIORITY_TO_RIGHT) {
            result.add(new ErrorPart(
                    getOperatorTextDescriptionRu(reasonText) + reasonText,
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "имеет более высокий"
            )).add(new ErrorPart(
                    "приоритет",
                    "term"
            ));
        } else if (error.Type == StudentErrorType.LEFT_ASSOC_TO_LEFT && errorText.equals(reasonText)) {
            result.add(new ErrorPart(
                    getOperatorTextDescriptionRu(reasonText) + reasonText,
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "имеет левую"
            )).add(new ErrorPart(
                    "ассоциативность",
                    "term"
            )).add(new ErrorPart(
                    "и вычисляется слева направо"
            ));
        } else if (error.Type == StudentErrorType.LEFT_ASSOC_TO_LEFT) {
            result.add(new ErrorPart(
                    getOperatorTextDescriptionRu(reasonText) + reasonText,
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "имеет одинаковый"
            )).add(new ErrorPart(
                    "приоритет",
                    "term"
            )).add(new ErrorPart(
                    "и левую"
            )).add(new ErrorPart(
                    "ассоциативность",
                    "term"
            ));
        } else if (error.Type == StudentErrorType.RIGHT_ASSOC_TO_RIGHT && errorText.equals(reasonText)) {
            result.add(new ErrorPart(
                    getOperatorTextDescriptionRu(reasonText) + reasonText,
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "имеет правую"
            )).add(new ErrorPart(
                    "ассоциативность",
                    "term"
            )).add(new ErrorPart(
                    "и вычисляется справа налево"
            ));
        } else if (error.Type == StudentErrorType.RIGHT_ASSOC_TO_RIGHT) {
            result.add(new ErrorPart(
                    getOperatorTextDescriptionRu(reasonText) + reasonText,
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "имеет одинаковый"
            )).add(new ErrorPart(
                    "приоритет",
                    "term"
            )).add(new ErrorPart(
                    "и правую"
            )).add(new ErrorPart(
                    "ассоциативность",
                    "term"
            ));
        } else if (error.Type == StudentErrorType.IN_COMPLEX && errorText.equals("(")) {
            result.add(new ErrorPart(
                    "аргументы функции",
                    "term"
            )).add(new ErrorPart(
                    "вычисляются раньше"
            )).add(new ErrorPart(
                    "вызова функции​",
                    "term"
            ));
        } else if (error.Type == StudentErrorType.IN_COMPLEX && errorText.equals("[")) {
            result.add(new ErrorPart(
                    "операция в квадратных скобках",
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "вычисляется раньше"
            )).add(new ErrorPart(
                    "квадратных скобок",
                    "operator",
                    errorPos
            ));
        } else if (error.Type == StudentErrorType.IN_COMPLEX && thirdOperatorText.equals("(")) {
            result.add(new ErrorPart(
                    "операция в скобках",
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "вычисляется раньше"
            )).add(new ErrorPart(
                    "операции вне скобок",
                    "operator",
                    errorPos
            ));
        } else if (error.Type == StudentErrorType.IN_COMPLEX && thirdOperatorText.equals("[")) {
            result.add(new ErrorPart(
                    "операция в квадратных скобках",
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "вычисляется раньше"
            )).add(new ErrorPart(
                    "операции вне квадратных скобок",
                    "operator",
                    errorPos
            ));
        } else if (error.Type == StudentErrorType.STRICT_OPERANDS_ORDER) {
            result.add(new ErrorPart(
                    "левый операнд",
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "операции " + thirdOperatorText + " в позиции " + thirdOperatorPos,
                    "operator",
                    thirdOperatorPos
            )).add(new ErrorPart(
                    "должен быть вычислен раньше"
            )).add(new ErrorPart(
                    "правого операнда",
                    "operator",
                    errorPos
            ));
        } else {
            result.add(new ErrorPart(
                    "но ошибка неизвестна"
            ));
        }

        return result;
    }

    public static String getOperatorTextDescriptionEng(String errorText) {
        if (errorText.equals("(")) {
            return "parenthesis ";
        } else if (errorText.equals("[")) {
            return "brackets ";
        } else if (errorText.contains("(")) {
            return "function call ";
        }
        return "operator ";
    }

    public static Error getErrorDescriptionEng(StudentError error, OntologyHelper helper) {
        String errorText = getDataProperty(helper, error.getErrorPos(), "text");
        String reasonText = getDataProperty(helper, error.getReasonPos(), "text");

        HashMap<Integer, Set<Integer>> thirdOperators = getObjectPropertyRelationsByIndex(helper, "before_third_operator");
        Set<Integer> thirdOperatorPoss = thirdOperators.getOrDefault(error.getReasonPos(), new HashSet<>());
        Integer thirdOperatorPos = thirdOperatorPoss.isEmpty() ? -1 : thirdOperatorPoss.iterator().next();
        String thirdOperatorText = getDataProperty(helper, thirdOperatorPos, "text");

        int reasonPos = error.getReasonPos();
        int errorPos = error.getErrorPos();

        Error result = new Error();
        result.add(new ErrorPart(
                getOperatorTextDescriptionEng(reasonText) + reasonText + " at pos " + reasonPos,
                "operator",
                reasonPos)
        ).add(new ErrorPart(
                "should be evaluated before"
        )).add(new ErrorPart(
                getOperatorTextDescriptionEng(errorText) + errorText + " at pos " + errorPos + ",",
                "operator",
                errorPos
        )).add(new ErrorPart(
                "because"
        ));
        if (error.Type == StudentErrorType.HIGH_PRIORITY_TO_LEFT || error.Type == StudentErrorType.HIGH_PRIORITY_TO_RIGHT) {
            result.add(new ErrorPart(
                    getOperatorTextDescriptionEng(reasonText) + reasonText,
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "has higher"
            )).add(new ErrorPart(
                    "precedence",
                    "term"
            ));
        } else if (error.Type == StudentErrorType.LEFT_ASSOC_TO_LEFT && errorText.equals(reasonText)) {
            result.add(new ErrorPart(
                    getOperatorTextDescriptionEng(reasonText) + reasonText,
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "has left"
            )).add(new ErrorPart(
                    "associativity",
                    "term"
            )).add(new ErrorPart(
                    "and is evaluated from left to right"
            ));
        } else if (error.Type == StudentErrorType.LEFT_ASSOC_TO_LEFT) {
            result.add(new ErrorPart(
                    getOperatorTextDescriptionEng(reasonText) + reasonText,
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "has the same"
            )).add(new ErrorPart(
                    "precedence",
                    "term"
            )).add(new ErrorPart(
                    "and left"
            )).add(new ErrorPart(
                    "associativity",
                    "term"
            ));
        } else if (error.Type == StudentErrorType.RIGHT_ASSOC_TO_RIGHT && errorText.equals(reasonText)) {
            result.add(new ErrorPart(
                    getOperatorTextDescriptionEng(reasonText) + reasonText,
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "has right"
            )).add(new ErrorPart(
                    "associativity",
                    "term"
            )).add(new ErrorPart(
                    "and is evaluated from right to left"
            ));
        } else if (error.Type == StudentErrorType.RIGHT_ASSOC_TO_RIGHT) {
            result.add(new ErrorPart(
                    getOperatorTextDescriptionEng(reasonText) + reasonText,
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "has the same"
            )).add(new ErrorPart(
                    "precedence",
                    "term"
            )).add(new ErrorPart(
                    "and right"
            )).add(new ErrorPart(
                    "associativity",
                    "term"
            ));
        } else if (error.Type == StudentErrorType.IN_COMPLEX && errorText.equals("(")) {
            result.add(new ErrorPart(
                    "function arguments",
                    "term"
            )).add(new ErrorPart(
                    "are evaluated before"
            )).add(new ErrorPart(
                    "function call",
                    "term"
            ));
        } else if (error.Type == StudentErrorType.IN_COMPLEX && errorText.equals("[")) {
            result.add(new ErrorPart(
                    "expression in brackets",
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "is evaluated before"
            )).add(new ErrorPart(
                    "brackets",
                    "operator",
                    errorPos
            ));
        } else if (error.Type == StudentErrorType.IN_COMPLEX && thirdOperatorText.equals("(")) {
            result.add(new ErrorPart(
                    "expression in parenthesis",
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "is evaluated before"
            )).add(new ErrorPart(
                    "operator outside of them",
                    "operator",
                    errorPos
            ));
        } else if (error.Type == StudentErrorType.IN_COMPLEX && thirdOperatorText.equals("[")) {
            result.add(new ErrorPart(
                    "expression in brackets",
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "is evaluated before"
            )).add(new ErrorPart(
                    "operator outside of them",
                    "operator",
                    errorPos
            ));
        } else if (error.Type == StudentErrorType.STRICT_OPERANDS_ORDER) {
            result.add(new ErrorPart(
                    "left operand",
                    "operator",
                    reasonPos
            )).add(new ErrorPart(
                    "of the " + getOperatorTextDescriptionEng(thirdOperatorText) + thirdOperatorText + " at pos " + thirdOperatorPos,
                    "operator",
                    thirdOperatorPos
            )).add(new ErrorPart(
                    "must be evaluated before​"
            )).add(new ErrorPart(
                    "its right operand",
                    "operator",
                    errorPos
            ));
        } else {
            result.add(new ErrorPart(
                    "unknown error"
            ));
        }
        return result;
    }

    public static Error getErrorDescription(StudentError error, OntologyHelper helper, String lang) {
        if (lang.equals("ru")) {
            return getErrorDescriptionRu(error, helper);
        } else {
            return getErrorDescriptionEng(error, helper);
        }
    }
}
