package org.swrlapi.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.swrlapi.example.OntologyUtil.getObjectPropertyRelationsByIndex;
import static org.swrlapi.example.OntologyUtil.initHelper;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SimpleOntologyTest {
    OntologyHelper Helper;

    @BeforeAll
    void init() {
        Helper = initHelper(Arrays.asList("var1", "+", "var2"));
    }

    @Test
    public void NextIndexTest() {
        HashMap<Integer, Set<Integer>> realOperands = getObjectPropertyRelationsByIndex(Helper, "next_index");
        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2));
        expOperands.put(2, Set.of(3));
        expOperands.put(3, Set.of());

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void NextStepTest() {
        HashMap<Integer, Set<Integer>> realOperands = getObjectPropertyRelationsByIndex(Helper, "next_step");
        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(1));
        expOperands.put(2, Set.of(2));
        expOperands.put(3, Set.of(3));

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SameStepTest() {
        HashMap<Integer, Set<Integer>> realOperands = getObjectPropertyRelationsByIndex(Helper, "same_step");
        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(1, 2, 3));
        expOperands.put(2, Set.of(1, 2, 3));
        expOperands.put(3, Set.of(1, 2, 3));

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void NotIndexTest() {
        HashMap<Integer, Set<Integer>> realOperands = getObjectPropertyRelationsByIndex(Helper, "not_index");
        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2, 3));
        expOperands.put(2, Set.of(1, 3));
        expOperands.put(3, Set.of(1, 2));

        assertEquals(expOperands, realOperands);
    }
}
