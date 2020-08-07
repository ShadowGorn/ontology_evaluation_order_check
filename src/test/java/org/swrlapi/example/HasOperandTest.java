package org.swrlapi.example;

import static org.swrlapi.example.OntologyUtil.*;

import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.params.ParameterizedTest;

public class HasOperandTest {
    @ParameterizedTest
    @JsonFileSource(resources = "../../../has-operand-test-data.json")
    public void HasOperandTest(javax.json.JsonObject object) {
        testObjectProperty(object, "has_operand");
    }
}