package org.vstu.compprehension.models.businesslogic.backend;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.swrlapi.example.Expression;
import org.vstu.compprehension.models.businesslogic.Law;
import org.vstu.compprehension.models.businesslogic.PositiveLaw;
import org.vstu.compprehension.models.businesslogic.domains.ProgrammingLanguageExpressionDomain;
import org.vstu.compprehension.models.entities.BackendFactEntity;
import org.vstu.compprehension.utils.DomainAdapter;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Relation {
    int indexFrom;
    List<Integer> indexesTo;
}

class PelletBackendTest {
    ProgrammingLanguageExpressionDomain domain = (ProgrammingLanguageExpressionDomain)DomainAdapter.getDomain("ProgrammingLanguageExpressionDomain");

    static Optional<Integer> getIndexFromName(String name, boolean allowNotZeroStep) {
        Assertions.assertTrue(name.startsWith("op__"), name);
        String[] parts = name.split("__");
        assertEquals(3, parts.length, name);
        if (allowNotZeroStep || parts[1].equals("0")) {
            return Optional.of(Integer.parseInt(parts[2]));
        }
        return Optional.empty();
    }

    static HashMap<Integer, Set<Integer>> getObjectPropertyRelationsByIndex(List<BackendFactEntity> solution) {
        HashMap<Integer, Set<Integer>> result = new HashMap<>();
        for (BackendFactEntity fact : solution) {
            Optional<Integer> from = getIndexFromName(fact.getSubject(), false);
            Optional<Integer> to = getIndexFromName(fact.getObject(), true);
            if (from.isPresent() && to.isPresent()) {
                if (!result.containsKey(from.get())) {
                    result.put(from.get(), new HashSet<>());
                }
                result.get(from.get()).add(to.get());
            }
        }
        return result;
    }

    static public HashMap<Integer, Set<Integer>> getObjectPropertyRelationsByIndexFromJson(String jsonRelations, int maxIndex) {
        HashMap<Integer, Set<Integer>> result = new HashMap<>();

        Relation[] relations = new Gson().fromJson(
                jsonRelations,
                Relation[].class);

        for (Relation relation : relations) {
            if (!result.containsKey(relation.indexFrom)) {
                result.put(relation.indexFrom, new HashSet<>());
            }
            for (Integer indexTo : relation.indexesTo) {
                result.get(relation.indexFrom).add(indexTo);
            }
        }

        return result;
    }

    static public void checkObjectProperty(List<BackendFactEntity> solution, String jsonRelations, int maxIndex) {
        HashMap<Integer, Set<Integer>> real = getObjectPropertyRelationsByIndex(solution);
        HashMap<Integer, Set<Integer>> exp = getObjectPropertyRelationsByIndexFromJson(jsonRelations, maxIndex);
        Assertions.assertEquals(exp, real);
    }

    static public Expression getExpressionFromJson(String jsonExpression) {
        return new Expression(
                new Gson().fromJson(
                        jsonExpression,
                        new TypeToken<List<String>>() {}.getType()));
    }

    public void checkObjectProperty(javax.json.JsonObject object, String objectProperty) {
        Expression expression = getExpressionFromJson(object.get("expression").toString());
        String jsonRelations = object.get("relations").toString();

        JenaBackend backend = new JenaBackend();
        List<Law> laws = new ArrayList<>();
        for (PositiveLaw law : domain.getPositiveLaws()) {
            if (!law.getName().contains("python") && !law.getName().contains("c#")) {
                laws.add(law);
            }
        }
        List<BackendFactEntity> statement = domain.getBackendFacts(expression.getTokens());
        List<BackendFactEntity> solution = backend.solve(laws, statement, List.of(objectProperty));

        checkObjectProperty(solution, jsonRelations, expression.size());
    }

    public void checkObjectProperty(javax.json.JsonObject object) {
        String objectProperty = object.getString("objectProperty");
        checkObjectProperty(object, objectProperty);
    }

    @ParameterizedTest
    @JsonFileSource(resources = "../../../../../../before-test-data.json")
    public void BeforeTest(javax.json.JsonObject object) {
        checkObjectProperty(object, "before");
    }

    @ParameterizedTest
    @JsonFileSource(resources = "../../../../../../has-operand-test-data.json")
    public void HasOperandTest(javax.json.JsonObject object) {
        checkObjectProperty(object, "ast_edge");
    }
}