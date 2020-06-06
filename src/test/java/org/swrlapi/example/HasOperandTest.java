package org.swrlapi.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.swrlapi.example.OntologyUtil.*;

import net.joshka.junit.json.params.JsonFileSource;
import net.joshka.junit.json.params.JsonSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.*;

public class HasOperandTest {
    @Test
    @Disabled("Core bug. Prefix/postfix define by previous term. Operation - prefix, operand - postfix")
    @JsonSource("{" +
            "\"expression\": " + "[\"var\", \"--\", \"--\", \"--\"], " +
            "\"relations\": [" +
            "{\"indexFrom\": 2, \"indexesTo\": [1]}," +
            "{\"indexFrom\": 3, \"indexesTo\": [2]}," +
            "{\"indexFrom\": 4, \"indexesTo\": [3]}]}\n")
    public void MultipleSuffixTest(javax.json.JsonObject object) {
        testObjectProperty(object, "has_operand");
    }

    @ParameterizedTest
    @JsonFileSource(resources = "../../../has-operand-test-data.json")
    public void HasOperandTest(javax.json.JsonObject object) {
        testObjectProperty(object, "has_operand");
    }
}