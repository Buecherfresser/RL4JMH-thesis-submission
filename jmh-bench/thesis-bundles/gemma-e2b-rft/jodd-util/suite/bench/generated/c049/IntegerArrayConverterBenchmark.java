package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.StringUtil;
import jodd.typeconverter.impl.IntegerArrayConverter;

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
        fixedCollection = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            fixedCollection.add(i * 10);
        }
        fixedStringArray = "10,20,30,40,50"; // CSV-like string input
        fixedByteArray = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        fixedDoubleArray = new double[]{1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.8, 9.9, 10.0};
    }

    // --- Benchmarks for convert(Object value) ---

    @Benchmark
    public void convert_IntArrayInput(Blackhole bh) {
        int[] result = converter.convert(fixedIntArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_CollectionInput(Blackhole bh) {
        // Test conversion from Collection (List<Integer>)
        Collection<Integer> input = fixedCollection;
        int[] result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_CharSequenceInput(Blackhole bh) {
        // Test conversion from CharSequence (String) using StringUtil.splitc
        String input = fixedStringArray;
        int[] result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convert_PrimitiveArrayInput(Blackhole bh) {
        // Test conversion when input is already an array (primitive int[])
        int[] result = converter.convert(fixedIntArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_NullInput(Blackhole bh) {
        // Test handling of null input
        int[] result = converter.convert(null);
        bh.consume(result);
    }

    // --- Benchmarks for internal logic paths (using public API) ---

    @Benchmark
    public void convertArrayToArray_ObjectArray(Blackhole bh) {
        // Test path for non-primitive object arrays
        Object[] input = new Object[]{1, 2.0, 3.0};
        int[] result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convertPrimitiveArrayToArray_LongArray(Blackhole bh) {
        // Test path for primitive array conversion (long[] to int[])
        long[] input = {10L, 20L, 30L};
        int[] result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convertPrimitiveArrayToArray_DoubleArray(Blackhole bh) {
        // Test path for primitive array conversion (double[] to int[])
        double[] input = {1.5, 2.5, 3.5};
        int[] result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void convertPrimitiveArrayToArray_BooleanArray(Blackhole bh) {
        // Test path for primitive array conversion (boolean[] to int[])
        boolean[] input = {true, false, true, false};
        int[] result = converter.convert(input);
        bh.consume(result);
    }
}
