package org.swrlapi.example;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.swrlapi.example.OntologyUtil.*;

class StudentErrorTest {
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

    List<Optional<Integer>> parseStudentPos(List<String> studentPos) {
        List<Optional<Integer>> result = new ArrayList<>();
        for (String strPos : studentPos) {
            if (strPos.isEmpty()) {
                result.add(Optional.empty());
            } else {
                result.add(Optional.of(Integer.parseInt(strPos)));
            }
        }
        return result;
    }

    @Test
    public void EmptyTest() {
        Set<StudentError> real = GetErrors(new ArrayList<>(), new ArrayList<>());
        Set<StudentError> exp = new HashSet<>();

        assertEquals(exp, real);
    }

    @Test
    public void SimpleHighPriorityRightTest() {
        List<String> texts = Arrays.asList("var1", "+", "var2", "*", "var3");
        List<Optional<Integer>> studentPos = parseStudentPos(Arrays.asList("", "1", "", "2", ""));

        Set<StudentError> real = GetErrors(texts, studentPos);
        Set<StudentError> exp = new HashSet<>();
        exp.add(new StudentError(2, 4, StudentErrorType.HIGH_PRIORITY_TO_RIGHT));

        assertEquals(exp, real);
    }

    @Test
    public void SimpleHighPriorityLeftTest() {
        List<String> texts = Arrays.asList("var1", "*", "var2", "+", "var3");
        List<Optional<Integer>> studentPos = parseStudentPos(Arrays.asList("", "2", "", "1", ""));

        Set<StudentError> real = GetErrors(texts, studentPos);
        Set<StudentError> exp = new HashSet<>();
        exp.add(new StudentError(4, 2, StudentErrorType.HIGH_PRIORITY_TO_LEFT));

        assertEquals(exp, real);
    }

    @Test
    public void SimpleLeftAssocTest() {
        List<String> texts = Arrays.asList("var1", "+", "var2", "+", "var3");
        List<Optional<Integer>> studentPos = parseStudentPos(Arrays.asList("", "2", "", "1", ""));

        Set<StudentError> real = GetErrors(texts, studentPos);
        Set<StudentError> exp = new HashSet<>();
        exp.add(new StudentError(4, 2, StudentErrorType.LEFT_ASSOC_TO_LEFT));

        assertEquals(exp, real);
    }

    @Test
    public void SimpleRightAssocTest() {
        List<String> texts = Arrays.asList("*", "*", "var1");
        List<Optional<Integer>> studentPos = parseStudentPos(Arrays.asList("1", "2", ""));

        Set<StudentError> real = GetErrors(texts, studentPos);
        Set<StudentError> exp = new HashSet<>();
        exp.add(new StudentError(1, 2, StudentErrorType.RIGHT_ASSOC_TO_RIGHT));

        assertEquals(exp, real);
    }

    @Test
    public void SimpleInComplexTest() {
        List<String> texts = Arrays.asList("(", "var1", "+", "var2", ")");
        List<Optional<Integer>> studentPos = parseStudentPos(Arrays.asList("1", "", "2", "", ""));

        Set<StudentError> real = GetErrors(texts, studentPos);
        Set<StudentError> exp = new HashSet<>();
        exp.add(new StudentError(1, 3, StudentErrorType.IN_COMPLEX));

        assertEquals(exp, real);
    }
}