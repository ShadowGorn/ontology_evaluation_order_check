package org.swrlapi.example;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.swrlapi.example.OntologyUtil.getObjectPropertyRelationsByIndex;
import static org.swrlapi.example.OntologyUtil.initHelper;

class BeforeTest {
    HashMap<Integer, Set<Integer>> getBefore(List<String> texts) {
        OntologyHelper helper = initHelper(texts);
        return getObjectPropertyRelationsByIndex(helper, "before");
    }

    @Test
    public void EmptyTest() {
        HashMap<Integer, Set<Integer>> realOperands = getBefore(new ArrayList<>());
        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimpleTest() {
        List<String> texts = Arrays.asList("(", ")");
        HashMap<Integer, Set<Integer>> realOperands = getBefore(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of());
        expOperands.put(2, Set.of(1));

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimpleOrTest() {
        List<String> texts = Arrays.asList("var1", "||", "var2");
        HashMap<Integer, Set<Integer>> realOperands = getBefore(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2, 3));
        expOperands.put(2, Set.of());
        expOperands.put(3, Set.of(2));

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void ComplexOrTest() {
        List<String> texts = Arrays.asList("var1", "+", "var2", "||", "var3", "+", "var4");
        HashMap<Integer, Set<Integer>> realOperands = getBefore(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2, 4, 5, 6, 7));
        expOperands.put(2, Set.of(4, 5, 6, 7));
        expOperands.put(3, Set.of(2, 4, 5, 6, 7));
        expOperands.put(4, Set.of());
        expOperands.put(5, Set.of(4, 6));
        expOperands.put(6, Set.of(4));
        expOperands.put(7, Set.of(4, 6));

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimpleAndTest() {
        List<String> texts = Arrays.asList("var1", "&&", "var2");
        HashMap<Integer, Set<Integer>> realOperands = getBefore(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2, 3));
        expOperands.put(2, Set.of());
        expOperands.put(3, Set.of(2));

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimpleCommaTest() {
        List<String> texts = Arrays.asList("var1", ",", "var2");
        HashMap<Integer, Set<Integer>> realOperands = getBefore(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2, 3));
        expOperands.put(2, Set.of());
        expOperands.put(3, Set.of(2));

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void SimpleFunctionTest() {
        List<String> texts = Arrays.asList("func", "(", "var1", ",", "var2", ")");
        HashMap<Integer, Set<Integer>> realOperands = getBefore(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2));
        expOperands.put(2, Set.of());
        expOperands.put(3, Set.of(2));
        expOperands.put(4, Set.of());
        expOperands.put(5, Set.of(2));
        expOperands.put(6, Set.of(2));

        assertEquals(expOperands, realOperands);
    }

    @Test
    public void ComplexTest() {
        List<String> texts = Arrays.asList("var1", "*", "(", "var2", "||", "(", "var3", "+", "var4", ")", ")", ",", "var5");
        HashMap<Integer, Set<Integer>> realOperands = getBefore(texts);

        HashMap<Integer, Set<Integer>> expOperands = new HashMap<>();
        expOperands.put(1, Set.of(2, 12, 13));
        expOperands.put(2, Set.of(12, 13));
        expOperands.put(3, Set.of(2, 12, 13));
        expOperands.put(4, Set.of(2, 3, 5, 6, 7, 8, 9, 10, 12, 13));
        expOperands.put(5, Set.of(2, 3, 12, 13));
        expOperands.put(6, Set.of(2, 3, 5, 12, 13));
        expOperands.put(7, Set.of(2, 3, 5, 6, 8, 12, 13));
        expOperands.put(8, Set.of(2, 3, 5, 6, 12, 13));
        expOperands.put(9, Set.of(2, 3, 5, 6, 8, 12, 13));
        expOperands.put(10, Set.of(2, 3, 5, 6, 12, 13));
        expOperands.put(11, Set.of(2, 3, 12, 13));
        expOperands.put(12, Set.of());
        expOperands.put(13, Set.of(12));

        assertEquals(expOperands, realOperands);
    }
}