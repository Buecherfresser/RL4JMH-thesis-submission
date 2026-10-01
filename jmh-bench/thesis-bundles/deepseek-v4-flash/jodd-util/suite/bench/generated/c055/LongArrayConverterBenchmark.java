package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LongArrayConverter;
import jodd.typeconverter.TypeConverterManager;
import java.util.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongArrayConverterBenchmark {

    private LongArrayConverter converter;
    private Object nullInput;
    private Long singleLong;
    private String singleString;
    private String csvString;
    private Collection<Long> collection;
    private Collection<Long> setCollection;
    private Iterable<Long> iterable;
    private long[] longArray;
    private int[] intArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;
    private Object[] objectArray;
    private Long[] longObjectArray;
    private String[] stringArray;

    @Setup(Level.Trial)
    public void setup() {
        converter = new LongArrayConverter(TypeConverterManager.get());
        nullInput = null;
        singleLong = 42L;
        singleString = "42";
        csvString = "1,2,3,4,5,6,7,8,9,10";
        collection = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        setCollection = new LinkedHashSet<>(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L));
        iterable = () -> Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L).iterator();
        longArray = new long[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        intArray = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        floatArray = new float[]{1.1f, 2.2f, 3.3f, 4.4f, 5.5f, 6.6f, 7.7f, 8.8f, 9.9f, 10.1f};
        doubleArray = new double[]{1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.8, 9.9, 10.1};
        shortArray = new short[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        byteArray = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        charArray = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        booleanArray = new boolean[]{true, false, true, true, false, true, false, true, false, true};
        objectArray = new Object[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};
        longObjectArray = new Long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};
        stringArray = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    }

    @Benchmark
    public long[] convertNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public long[] convertLong() {
        return converter.convert(singleLong);
    }

    @Benchmark
    public long[] convertString() {
        return converter.convert(singleString);
    }

    @Benchmark
    public long[] convertCsvString() {
        return converter.convert(csvString);
    }

    @Benchmark
    public long[] convertCollection() {
        return converter.convert(collection);
    }

    @Benchmark
    public long[] convertSet() {
        return converter.convert(setCollection);
    }

    @Benchmark
    public long[] convertIterable() {
        return converter.convert(iterable);
    }

    @Benchmark
    public long[] convertLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public long[] convertIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public long[] convertFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public long[] convertDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public long[] convertShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public long[] convertByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public long[] convertCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public long[] convertBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public long[] convertObjectArray() {
        return converter.convert(objectArray);
    }

    @Benchmark
    public long[] convertLongObjectArray() {
        return converter.convert(longObjectArray);
    }

    @Benchmark
    public long[] convertStringArray() {
        return converter.convert(stringArray);
    }
}
