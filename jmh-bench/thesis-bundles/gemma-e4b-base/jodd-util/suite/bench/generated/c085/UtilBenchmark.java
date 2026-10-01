package bench.generated.c085;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Collections;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UtilBenchmark {

    // --- Inputs for toString and length ---
    private Object nullInput = null;
    private String stringInput;
    private List<String> collectionInput;
    private Map<String, Integer> mapInput;
    private Object[] objectArrayInput;
    private int[] primitiveArrayInput;
    private Vector<String> enumerationInput;
    private Iterator<String> iteratorInput;

    // --- Inputs for containsElement ---
    private Object containerString;
    private Object containerCollection;
    private Object containerMap;
    private Object containerArray;
    private Object containerIterator;
    private Object containerEnumeration;
    private Object elementToFind;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Basic types
        stringInput = "Hello World Test String";
        
        // 2. Collection
        collectionInput = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
        
        // 3. Map
        mapInput = new HashMap<>();
        mapInput.put("key1", 100);
        mapInput.put("key2", 200);
        
        // 4. Arrays
        objectArrayInput = new Object[]{"apple", 123, true};
        primitiveArrayInput = new int[]{1, 2, 3, 4, 5};
        
        // 5. Iterators/Enumerations
        enumerationInput = new Vector<>(Arrays.asList("E1", "E2"));
        iteratorInput = collectionInput.iterator();

        // 6. Setup for containsElement
        containerString = "Hello World Test String";
        containerCollection = new ArrayList<>(Arrays.asList("X", "Y", "Z"));
        containerMap = new HashMap<>(Map.of("k1", "v1", "k2", "v2"));
        containerArray = new Object[]{"target", "other"};
        containerIterator = new ArrayList<>(Arrays.asList("i1", "i2")).iterator();
        containerEnumeration = new Vector<>(Arrays.asList("e1", "e2")).elements();
        
        elementToFind = "target";
    }

    // =========================================================================
    // Benchmarks for jodd.util.Util.toString(Object value)
    // =========================================================================

    @Benchmark
    public String benchToString_String() {
        return Util.toString(stringInput);
    }

    @Benchmark
    public String benchToString_Collection() {
        return Util.toString(collectionInput);
    }

    @Benchmark
    public String benchToString_Map() {
        return Util.toString(mapInput);
    }

    @Benchmark
    public String benchToString_ObjectArray() {
        return Util.toString(objectArrayInput);
    }

    @Benchmark
    public String benchToString_PrimitiveArray() {
        return Util.toString(primitiveArrayInput);
    }

    @Benchmark
    public String benchToString_Null() {
        return Util.toString(nullInput);
    }

    // =========================================================================
    // Benchmarks for jodd.util.Util.length(Object obj)
    // =========================================================================

    @Benchmark
    public int benchLength_String() {
        return Util.length(stringInput);
    }

    @Benchmark
    public int benchLength_Collection() {
        return Util.length(collectionInput);
    }

    @Benchmark
    public int benchLength_Map() {
        return Util.length(mapInput);
    }

    @Benchmark
    public int benchLength_ObjectArray() {
        return Util.length(objectArrayInput);
    }

    @Benchmark
    public int benchLength_PrimitiveArray() {
        return Util.length(primitiveArrayInput);
    }

    @Benchmark
    public int benchLength_Iterator() {
        return Util.length(iteratorInput);
    }

    @Benchmark
    public int benchLength_Enumeration() {
        return Util.length(enumerationInput);
    }

    @Benchmark
    public int benchLength_Null() {
        return Util.length(nullInput);
    }

    // =========================================================================
    // Benchmarks for jodd.util.Util.containsElement(Object obj, Object element)
    // =========================================================================

    @Benchmark
    public boolean benchContainsElement_String() {
        return Util.containsElement(containerString, elementToFind);
    }

    @Benchmark
    public boolean benchContainsElement_Collection() {
        return Util.containsElement(containerCollection, elementToFind);
    }

    @Benchmark
    public boolean benchContainsElement_Map() {
        // Checks values
        return Util.containsElement(containerMap, 200);
    }

    @Benchmark
    public boolean benchContainsElement_ObjectArray() {
        return Util.containsElement(containerArray, elementToFind);
    }

    @Benchmark
    public boolean benchContainsElement_Iterator() {
        return Util.containsElement(containerIterator, "i2");
    }

    @Benchmark
    public boolean benchContainsElement_Enumeration() {
        return Util.containsElement(containerEnumeration, "e1");
    }

    @Benchmark
    public boolean benchContainsElement_NullContainer() {
        return Util.containsElement(nullInput, elementToFind);
    }

    // =========================================================================
    // Benchmarks for jodd.util.Util.toPrettyString(Object value)
    // =========================================================================

    @Benchmark
    public String benchToPrettyString_String() {
        return Util.toPrettyString(stringInput);
    }

    @Benchmark
    public String benchToPrettyString_Null() {
        return Util.toPrettyString(nullInput);
    }

    @Benchmark
    public String benchToPrettyString_ObjectArray() {
        return Util.toPrettyString(objectArrayInput);
    }

    @Benchmark
    public String benchToPrettyString_PrimitiveArray() {
        return Util.toPrettyString(primitiveArrayInput);
    }

    @Benchmark
    public String benchToPrettyString_Collection() {
        return Util.toPrettyString(collectionInput);
    }
}
