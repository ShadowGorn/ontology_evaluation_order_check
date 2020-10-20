package org.swrlapi.example;

import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.params.ParameterizedTest;

import static org.swrlapi.example.OntologyTestUtil.testObjectProperty;

public class HasOperandTest {
    @ParameterizedTest
    @JsonFileSource(resources = "../../../has-operand-test-data.json")
    public void HasOperandTest(javax.json.JsonObject object) {
        testObjectProperty(object, "ast_edge");
    }
}