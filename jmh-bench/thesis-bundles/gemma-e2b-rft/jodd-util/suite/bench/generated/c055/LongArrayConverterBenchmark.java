package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.util.StringUtil;
import jodd.typeconverter.impl.LongArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class LongArrayConverterBenchmark {

    private LongArrayConverter converter;
    private TypeConverterManager typeConverterManager;

    // Fixed input data for testing
    private long[] fixedLongArray;
    private List<Long> fixedLongList;
    private String fixedStringArray;
    private byte[] fixedByteArray;
    private int[] fixedIntArray;
    private double[] fixedDoubleArray;

    // Helper for setup
    @Setup
    public void setup() {
        this.typeConverterManager = new TypeConverterManager();
        this.converter = new LongArrayConverter(typeConverterManager);

        // 1. Fixed Long Array (for array input)
        this.fixedLongArray = new long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};

        // 2. Fixed Long List (for Collection input)
        this.fixedLongList = Arrays.asList(100L, 200L, 300L, 400L, 500L);

        // 3. Fixed String Array (for CharSequence input)
        this.fixedStringArray = "10,20,30,40,50";

        // 4. Fixed Byte Array (for primitive array input)
        this.fixedByteArray = new byte[]{1, 2, 3, 4, 5};

        // 5. Fixed Int Array (for primitive array input, testing conversion to long[])
        this.fixedIntArray = new int[]{1, 2, 3, 4, 5};

        // 6. Fixed Double Array (for primitive array input, testing conversion to long[])
        this.fixedDoubleArray = new double[]{1.1, 2.2, 3.3, 4.4, 5.5};
    }

    // --- Benchmarks for LongArrayConverter.convert(Object value) ---

    @Benchmark
    public void convert_LongArrayInput(Blackhole bh) {
        long[] result = converter.convert(fixedLongArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_CollectionInput(Blackhole bh) {
        // Test conversion from Collection (List)
        long[] result = converter.convert(fixedLongList);
        bh.consume(result);
    }

    @Benchmark
    public void convert_CharSequenceInput(Blackhole bh) {
        // Test conversion from CharSequence (String)
        long[] result = converter.convert(fixedStringArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_PrimitiveByteArrayInput(Blackhole bh) {
        // Test conversion from primitive array (byte[])
        long[] result = converter.convert(fixedByteArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_PrimitiveIntArrayInput(Blackhole bh) {
        // Test conversion from primitive array (int[])
        long[] result = converter.convert(fixedIntArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_PrimitiveDoubleArrayInput(Blackhole bh) {
        // Test conversion from primitive array (double[])
        long[] result = converter.convert(fixedDoubleArray);
        bh.consume(result);
    }

    @Benchmark
    public void convert_NullInput(Blackhole bh) {
        // Test null handling
        long[] result = converter.convert(null);
        bh.consume(result);
    }
}
