package bench.generated.c085;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import jodd.util.Util;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class UtilBenchmark {

    // --- State Fields for Benchmarking ---

    private String testString;
    private List<String> testList;
    private Map<String, Integer> testMap;
    private byte[] testArray;
    private Object testArrayObject;
    private List<Integer> testIntegerList;
    private Object testIterator;
    private Map<String, Integer> testMapForContains;

    @Setup
    public void setup() {
        // Setup String
        testString = "This is a moderately long test string designed to stress string operations.";

        // Setup List
        testList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            testList.add("item_" + i);
        }

        // Setup Map
        testMap = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            testMap.put("key" + i, i * 10);
        }
        testMapForContains = new HashMap<>(testMap);

        // Setup Array (byte array)
        testArray = new byte[1024];
        Arrays.fill(testArray, (byte) 0xAA);
        testArrayObject = testArray;

        // Setup Integer List (for length/contains testing)
        testIntegerList = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            testIntegerList.add(i);
        }

        // Setup Iterator
        testIterator = testList.iterator();
    }

    // --- Benchmarks for Util.toString(Object) ---

    @Benchmark
    public void benchmarkToString(Blackhole bh) {
        String result = Util.toString(testString);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToStringNull(Blackhole bh) {
        String result = Util.toString(null);
        bh.consume(result);
    }

    // --- Benchmarks for Util.length(Object) ---

    @Benchmark
    public void benchmarkLengthString(Blackhole bh) {
        int length = Util.length(testString);
        bh.consume(length);
    }

    @Benchmark
    public void benchmarkLengthList(Blackhole bh) {
        int length = Util.length(testList);
        bh.consume(length);
    }

    @Benchmark
    public void benchmarkLengthMap(Blackhole bh) {
        int length = Util.length(testMap);
        bh.consume(length);
    }

    @Benchmark
    public void benchmarkLengthArray(Blackhole bh) {
        int length = Util.length(testArrayObject);
        bh.consume(length);
    }

    @Benchmark
    public void benchmarkLengthIterator(Blackhole bh) {
        int length = Util.length(testIterator);
        bh.consume(length);
    }

    // --- Benchmarks for Util.containsElement(Object, Object) ---

    @Benchmark
    public void benchmarkContainsStringInString(Blackhole bh) {
        boolean result = Util.containsElement(testString, "moderately");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsElementInList(Blackhole bh) {
        // Check for an element that exists
        boolean result = Util.containsElement(testList, "item_50");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsElementNotInList(Blackhole bh) {
        // Check for an element that does not exist
        boolean result = Util.containsElement(testList, "item_999");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsValueInMap(Blackhole bh) {
        // Check if a value exists in the map's values
        boolean result = Util.containsElement(testMap, 1000);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsElementInArray(Blackhole bh) {
        // Check if a specific byte value exists in the array
        boolean result = Util.containsElement(testArrayObject, (byte) 0xAA);
        bh.consume(result);
    }

    // --- Benchmarks for Util.toPrettyString(Object) ---

    @Benchmark
    public void benchmarkToPrettyStringString(Blackhole bh) {
        String result = Util.toPrettyString(testString);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToPrettyStringList(Blackhole bh) {
        String result = Util.toPrettyString(testList);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToPrettyStringArray(Blackhole bh) {
        String result = Util.toPrettyString(testArrayObject);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToPrettyStringMap(Blackhole bh) {
        String result = Util.toPrettyString(testMap);
        bh.consume(result);
    }
}
