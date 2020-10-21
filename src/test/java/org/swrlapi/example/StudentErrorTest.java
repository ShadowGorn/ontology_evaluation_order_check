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
        return OntologyTestUtil.GetErrors(getExpressionFromJson(jsonExpression), debug);
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