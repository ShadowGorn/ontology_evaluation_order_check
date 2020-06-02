package org.swrlapi.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.swrlapi.example.OntologyUtil.*;

class StudentErrorTest {
    Set<StudentError> GetErrors(String jsonExpression) {
        Expression expression = new Expression(
                new Gson().fromJson(
                        jsonExpression,
                        new TypeToken<List<String>>() {}.getType()));
        return GetErrors(expression.Texts, expression.StudentPos);
    }

    class Expression {
        List<String> Texts;
        List<Optional<Integer>> StudentPos;

        public Expression(List<String> expression) {
            Texts = new ArrayList<>();
            StudentPos = new ArrayList<>();
            FillFromExpression(expression);
        }

        void FillFromExpression(List<String> expression) {
            for (String part : expression) {
                Term term = new Term(part);
                Texts.add(term.Text);
                StudentPos.add(term.StudentPos);
            }
        }

        class Term {
            Term(String text) {
                String[] parts = text.split("\\$", 2);

                Text = parts[0];
                if (parts.length > 1) {
                    StudentPos = Optional.of(Integer.parseInt(parts[1]));
                } else {
                    StudentPos = Optional.empty();
                }
            }

            Optional<Integer> StudentPos;
            String Text;
        }
    }

    Set<StudentError> GetErrors(List<String> texts, List<Optional<Integer>> studentPos) {
        Set<StudentError> resultErrors = new HashSet<>();
        OntologyHelper helper = new OntologyHelper(texts, studentPos);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_more_priority_left"),
                StudentErrorType.HIGH_PRIORITY_TO_LEFT);
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_more_priority_right"),
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
        return resultErrors;
    }

    void FillErrors(Set<StudentError> resultErrors, HashMap<Integer, Set<Integer>> errors, StudentErrorType type) {
        errors.forEach((error,reasons) -> {
            for (Integer reason : reasons) {
                resultErrors.add(new StudentError(error, reason, type));
            }
        });
    }

    @ParameterizedTest
    @JsonFileSource(resources = "../../../student-error-test-data.json")
    public void StudentErrorTest(javax.json.JsonObject object) {
        Set<StudentError> real = GetErrors(object.get("expression").toString());
        Set<StudentError> exp = new Gson().fromJson(object.get("errors").toString(), new TypeToken<Set<StudentError>>() {}.getType());
        assertEquals(exp, real);
    }
}