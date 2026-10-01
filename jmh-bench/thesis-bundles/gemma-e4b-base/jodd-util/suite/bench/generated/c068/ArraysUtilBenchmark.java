package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.ArraysUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArraysUtilBenchmark {

    private int[] smallIntArray;
    private int[] mediumIntArray;
    private int[] largeIntArray;
    private int[] smallIntSubArray;
    private int[] smallIntSourceArray;
    private int[] smallIntDestArray;

    private String[] smallStringArray;
    private String[] smallStringSourceArray;
    private String[] smallStringDestArray;

    @Setup(Level.Trial)
    public void setup() {
        // Setup Int arrays
        smallIntArray = new int[10];
        mediumIntArray = new int[100];
        largeIntArray = new int[1000];

        for (int i = 0; i < smallIntArray.length; i++) smallIntArray[i] = i;
        for (int i = 0; i < mediumIntArray.length; i++) mediumIntArray[i] = i;
        for (int i = 0; i < largeIntArray.length; i++) largeIntArray[i] = i;

        // Setup arrays for mutation/manipulation tests
        smallIntSubArray = new int[5];
        smallIntSourceArray = new int[5];
        smallIntDestArray = new int[10];
        for (int i = 0; i < 5; i++) smallIntSubArray[i] = i;
        for (int i = 0; i < 5; i++) smallIntSourceArray[i] = i + 100;
        for (int i = 0; i < 10; i++) smallIntDestArray[i] = i * 2;


        // Setup String arrays
        smallStringArray = new String[10];
        smallStringSourceArray = new String[5];
        smallStringDestArray = new String[10];
        for (int i = 0; i < 10; i++) smallStringArray[i] = "Str" + i;
        for (int i = 0; i < 5; i++) smallStringSourceArray[i] = "Src" + i;
        for (int i = 0; i < 10; i++) smallStringDestArray[i] = "Dest" + i;
    }

    // --- Join Benchmarks ---

    @Benchmark
    public int[] benchmarkJoinInts() {
        // Join two small arrays
        int[] result = ArraysUtil.join(smallIntArray, smallIntSubArray);
        return result;
    }

    @Benchmark
    public String[] benchmarkJoinStrings() {
        // Join two small string arrays
        String[] result = ArraysUtil.join(smallStringArray, smallStringSourceArray);
        return result;
    }

    // --- Resize Benchmarks ---

    @Benchmark
    public int[] benchmarkResizeInts() {
        // Resize small array to medium size
        int[] result = ArraysUtil.resize(smallIntArray, mediumIntArray.length);
        return result;
    }

    @Benchmark
    public String[] benchmarkResizeStrings() {
        // Resize small string array to larger size
        String[] result = ArraysUtil.resize(smallStringArray, 20);
        return result;
    }

    // --- Append Benchmarks ---

    @Benchmark
    public int[] benchmarkAppendInts() {
        // Append one element to small array
        int[] result = ArraysUtil.append(smallIntArray, 99);
        return result;
    }

    @Benchmark
    public String[] benchmarkAppendStrings() {
        // Append one element to small string array
        String[] result = ArraysUtil.append(smallStringArray, "New");
        return result;
    }

    // --- Remove Benchmarks ---

    @Benchmark
    public int[] benchmarkRemoveInts() {
        // Remove 3 elements from the middle of the medium array
        int[] result = ArraysUtil.remove(mediumIntArray, 30, 3);
        return result;
    }

    @Benchmark
    public String[] benchmarkRemoveStrings() {
        // Remove 2 elements from the middle of the small string array
        String[] result = ArraysUtil.remove(smallStringArray, 4, 2);
        return result;
    }

    // --- Subarray Benchmarks ---

    @Benchmark
    public int[] benchmarkSubarrayInts() {
        // Extract a subarray from the large array
        int[] result = ArraysUtil.subarray(largeIntArray, 100, 50);
        return result;
    }

    @Benchmark
    public String[] benchmarkSubarrayStrings() {
        // Extract a subarray from the small string array
        String[] result = ArraysUtil.subarray(smallStringArray, 2, 5);
        return result;
    }

    // --- Insert Benchmarks ---

    @Benchmark
    public int[] benchmarkInsertInts() {
        // Insert smallIntSourceArray into smallIntDestArray at offset 2
        int[] result = ArraysUtil.insert(smallIntDestArray, smallIntSourceArray, 2);
        return result;
    }

    @Benchmark
    public String[] benchmarkInsertStrings() {
        // Insert smallStringSourceArray into smallStringDestArray at offset 5
        String[] result = ArraysUtil.insert(smallStringDestArray, smallStringSourceArray, 5);
        return result;
    }

    @Benchmark
    public int[] benchmarkInsertAtInts() {
        // Insert smallIntSourceArray into smallIntDestArray at offset 3
        int[] result = ArraysUtil.insertAt(smallIntDestArray, smallIntSourceArray, 3);
        return result;
    }

    // --- Search Benchmarks (indexOf/contains) ---

    @Benchmark
    public int benchmarkIndexOfInts() {
        // Search for a value in the large array (should be present)
        int value = 500;
        int index = ArraysUtil.indexOf(largeIntArray, value);
        return index;
    }

    @Benchmark
    public boolean benchmarkContainsInts() {
        // Check if a value exists in the medium array
        int value = 50;
        boolean contains = ArraysUtil.contains(mediumIntArray, value);
        return contains;
    }

    @Benchmark
    public int benchmarkIndexOfIntsRange() {
        // Search for a value in a specific range of the large array
        int value = 999;
        int index = ArraysUtil.indexOf(largeIntArray, value, 500, 700);
        return index;
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public Integer[] benchmarkValuesOfInts() {
        // Convert primitive int[] to Integer[]
        Integer[] result = ArraysUtil.valuesOf(smallIntArray);
        return result;
    }

    @Benchmark
    public int[] benchmarkValuesInts() {
        // Convert Integer[] to primitive int[]
        Integer[] input = new Integer[10];
        for (int i = 0; i < 10; i++) input[i] = i;
        int[] result = ArraysUtil.values(input);
        return result;
    }

    // --- ToString Benchmarks ---

    @Benchmark
    public String benchmarkToStringInts() {
        // Convert int[] to comma-separated String
        String result = ArraysUtil.toString(smallIntArray);
        return result;
    }

    @Benchmark
    public String[] benchmarkToStringArrayInts() {
        // Convert int[] to String[]
        String[] result = ArraysUtil.toStringArray(smallIntArray);
        return result;
    }
}
