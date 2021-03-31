package org.swrlapi.example;

import org.junit.jupiter.api.Assertions;

import java.util.HashMap;
import java.util.Set;

public class OntologyTestUtil {

    static public void testObjectProperty(OntologyHelper helper, String objectProperty, String jsonRelations, int maxIndex) {
        HashMap<Integer, Set<Integer>> real = OntologyUtil.getObjectPropertyRelationsByIndex(helper, objectProperty);
        HashMap<Integer, Set<Integer>> exp = OntologyUtil.getObjectPropertyRelationsByIndexFromJson(jsonRelations, maxIndex);
        Assertions.assertEquals(exp, real);
    }

    static public void testObjectProperty(javax.json.JsonObject object, String objectProperty) {
        Expression expression = OntologyUtil.getExpressionFromJson(object.get("expression").toString());
        OntologyHelper helper = new OntologyHelper(expression, "C++");
        String jsonRelations = object.get("relations").toString();
        testObjectProperty(helper, objectProperty, jsonRelations, expression.size());
    }

    static public void testObjectProperty(javax.json.JsonObject object) {
        String objectProperty = object.getString("objectProperty");
        testObjectProperty(object, objectProperty);
    }

    static public Set<StudentError> GetErrors(Expression expression, boolean debug) {
        OntologyHelper helper = new OntologyHelper(expression, "C++");
        return OntologyUtil.GetErrors(helper, debug);
    }
}
