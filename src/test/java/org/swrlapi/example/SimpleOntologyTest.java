package org.swrlapi.example;

import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.params.ParameterizedTest;

import static org.swrlapi.example.OntologyUtil.*;

public class SimpleOntologyTest {
    @ParameterizedTest
    @JsonFileSource(resources = "../../../simple-ontology-test-data.json")
    public void SimpleOntologyTest(javax.json.JsonObject object) {
        testObjectProperty(object);
    }
}
