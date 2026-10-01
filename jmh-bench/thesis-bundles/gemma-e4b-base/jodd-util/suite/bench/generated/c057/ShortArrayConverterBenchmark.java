package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.impl.ShortArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortArrayConverterBenchmark {

    private ShortArrayConverter converter;

    // --- Input Fixtures ---

    // 1. Primitive Array Inputs
    private int[] intArray;
    private long[] longArray;
    private byte[] byteArray;

    // 2. Object Array Inputs
    private Integer[] integerObjectArray;
    private String[] stringObjectArray;

    // 3. Collection/Iterable Inputs
    private Collection<Integer> integerCollection;
    private List<String> stringIterable;

    // 4. String Input (CSV format)
    private String csvString;

    // 5. Scalar Input
    private Integer scalarValue;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the converter
        converter = new ShortArrayConverter(TypeConverterManager.get());

        // --- Setup Input Sizes ---
        final int SMALL_SIZE = 10;
        final int MEDIUM_SIZE = 1000;
        final int LARGE_SIZE = 10000;

        // 1. Primitive Arrays
        intArray = new int[MEDIUM_SIZE];
        Arrays.fill(intArray, 10);
        longArray = new long[MEDIUM_SIZE];
        Arrays.fill(longArray, 20L);
        byteArray = new byte[MEDIUM_SIZE];
        Arrays.fill(byteArray, (byte) 30);

        // 2. Object Arrays
        integerObjectArray = new Integer[MEDIUM_SIZE];
        for (int i = 0; i < MEDIUM_SIZE; i++) {
            integerObjectArray[i] = i;
        }
        stringObjectArray = new String[MEDIUM_SIZE];
        for (int i = 0; i < MEDIUM_SIZE; i++) {
            stringObjectArray[i] = "value" + i;
        }

        // 3. Collections/Iterables
        integerCollection = new ArrayList<>(MEDIUM_SIZE);
        for (int i = 0; i < MEDIUM_SIZE; i++) {
            integerCollection.add(i);
        }
        stringIterable = new ArrayList<>(MEDIUM_SIZE);
        for (int i = 0; i < MEDIUM_SIZE; i++) {
            stringIterable.add("item_" + i);
        }

        // 4. String Input (CSV format)
        csvString = "1,2,3,4,5,6,7,8,9,10";

        // 5. Scalar Input
        scalarValue = 42;
    }

    // --- Benchmarks ---

    /**
     * Tests conversion of a primitive int array (int[] -> short[]).
     */
    @Benchmark
    public short[] convertIntPrimitiveArray() {
        return converter.convert(intArray);
    }

    /**
     * Tests conversion of a primitive long array (long[] -> short[]).
     */
    @Benchmark
    public short[] convertLongPrimitiveArray() {
        return converter.convert(longArray);
    }

    /**
     * Tests conversion of a primitive byte array (byte[] -> short[]).
     */
    @Benchmark
    public short[] convertBytePrimitiveArray() {
        return converter.convert(byteArray);
    }

    /**
     * Tests conversion of an object array (Integer[] -> short[]).
     */
    @Benchmark
    public short[] convertIntegerObjectArray() {
        return converter.convert(integerObjectArray);
    }

    /**
     * Tests conversion of an object array (String[] -> short[]).
     */
    @Benchmark
    public short[] convertStringObjectArray() {
        return converter.convert(stringObjectArray);
    }

    /**
     * Tests conversion of a Collection (ArrayList<Integer> -> short[]).
     */
    @Benchmark
    public short[] convertIntegerCollection() {
        return converter.convert(integerCollection);
    }

    /**
     * Tests conversion of an Iterable (List<String> -> short[]).
     */
    @Benchmark
    public short[] convertStringIterable() {
        return converter.convert(stringIterable);
    }

    /**
     * Tests conversion of a CSV formatted String (CharSequence -> short[]).
     */
    @Benchmark
    public short[] convertCsvString() {
        return converter.convert(csvString);
    }

    /**
     * Tests conversion of a single scalar value (Integer -> short[]).
     */
    @Benchmark
    public short[] convertScalarValue() {
        return converter.convert(scalarValue);
    }

    /**
     * Tests conversion of null input.
     */
    @Benchmark
    public short[] convertNullInput() {
        return converter.convert(null);
    }
}
