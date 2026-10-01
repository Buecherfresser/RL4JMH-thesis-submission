package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.IntegerArrayConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.StringUtil;
import jodd.typeconverter.impl.ArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntegerArrayConverterBenchmark {

    private IntegerArrayConverter converter;
    private TypeConverterManager typeConverterManager;

    // Fixed input data for testing
    private int[] fixedIntArray;
    private List<Integer> fixedCollection;
    private String fixedStringArray;
    private byte[] fixedByteArray;
    private double[] fixedDoubleArray;

    @Setup
    public void setup() {
        // Initialize TypeConverterManager
        this.typeConverterManager = TypeConverterManager.get();
        this.converter = new IntegerArrayConverter(this.typeConverterManager);

        // Setup fixed inputs
        fixedIntArray = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        fixedCollection = Arrays.asList(100, 200, 300, 400, 500);
        fixedStringArray = "1,2,3,4,5"; // String input for CharSequence path
        fixedByteArray = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        fixedDoubleArray = new double[]{1.1, 2.2, 3.3, 4.4, 5.5};
    }

    // --- Benchmarks for convert(Object value) ---

    @Benchmark
    public void convert_IntArrayInput(Blackhole bh) {
        int[] result = converter.convert(fixedIntArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_CollectionInput(Blackhole bh) {
        int[] result = converter.convert(fixedCollection);
        bh.consume(result);
    }

    @Benchmark
    public void convert_StringArrayInput(Blackhole bh) {
        // Testing CharSequence path: String input split by delimiters
        int[] result = converter.convert(fixedStringArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_ByteArrayInput(Blackhole bh) {
        // Testing non-array input path (byte[] is an array, but testing general conversion path)
        int[] result = converter.convert(fixedByteArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_DoubleArrayInput(Blackhole bh) {
        // Testing object array conversion path (double[] is an object array)
        int[] result = (int[]) converter.convert(fixedDoubleArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_NullInput(Blackhole bh) {
        int[] result = converter.convert(null);
        bh.consume(result); // Expects null
    }

    // Test case for non-array, non-collection, non-CharSequence (triggers convertToSingleElementArray)
    @Benchmark
    public void convert_SingleObjectInput(Blackhole bh) {
        // Using a simple Integer to test the fallback path
        Integer singleValue = 42;
        int[] result = converter.convert(singleValue);
        bh.consume(result); // Expects array of size 1
    }
}
