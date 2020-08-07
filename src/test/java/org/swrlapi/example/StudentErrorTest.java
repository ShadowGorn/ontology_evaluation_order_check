package org.swrlapi.example;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.swrlapi.example.OntologyUtil.*;

class StudentErrorTest {
    Set<StudentError> GetErrors(String jsonExpression, boolean debug) {
        return GetErrors(getExpressionFromJson(jsonExpression), debug);
    }

    Set<StudentError> GetErrors(Expression expression, boolean debug) {
        Set<StudentError> resultErrors = new HashSet<>();
        OntologyHelper helper = initHelper(expression);
        if (debug) {
            helper.dump(false);
        }
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
        FillErrors(
                resultErrors,
                getObjectPropertyRelationsByIndex(helper, "student_error_strict_operands_order"),
                StudentErrorType.STRICT_OPERANDS_ORDER);
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
        boolean debug = object.containsKey("debug") && object.get("debug").toString().equals("\"true\"");
        Set<StudentError> real = GetErrors(object.get("expression").toString(), debug);
        Set<StudentError> exp = new Gson().fromJson(object.get("errors").toString(), new TypeToken<Set<StudentError>>() {}.getType());
        assertEquals(exp, real);
    }
}