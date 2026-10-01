package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.IntegerArrayConverter;
import jodd.typeconverter.TypeConverterManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntegerArrayConverterBenchmark {

    private IntegerArrayConverter converter;

    // Primitive arrays
    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;

    // Object array
    private Integer[] integerObjectArray;

    // Collection and Iterable
    private Collection<Integer> integerCollection;
    private Iterable<Integer> integerIterable;

    // CSV string
    private CharSequence csvString;

    // Single non‑array value
    private Integer singleValue;

    @Setup(Level.Trial)
    public void setUp() {
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new IntegerArrayConverter(manager);

        int size = 1024;

        intArray = new int[size];
        longArray = new long[size];
        floatArray = new float[size];
        doubleArray = new double[size];
        shortArray = new short[size];
        byteArray = new byte[size];
        charArray = new char[size];
        booleanArray = new boolean[size];
        integerObjectArray = new Integer[size];

        for (int i = 0; i < size; i++) {
            int value = i;
            intArray[i] = value;
            longArray[i] = value;
            floatArray[i] = value;
            doubleArray[i] = value;
            shortArray[i] = (short) value;
            byteArray[i] = (byte) value;
            charArray[i] = (char) value;
            booleanArray[i] = (i % 2) == 0;
            integerObjectArray[i] = value;
        }

        integerCollection = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            integerCollection.add(i);
        }

        List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        integerIterable = list;

        StringBuilder sb = new StringBuilder(size * 4);
        for (int i = 0; i < size; i++) {
            sb.append(i);
            if (i < size - 1) {
                sb.append(',');
            }
        }
        csvString = sb.toString();

        singleValue = 12345;
    }

    @Benchmark
    public int[] convertFromIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public int[] convertFromLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public int[] convertFromFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public int[] convertFromDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public int[] convertFromShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public int[] convertFromByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public int[] convertFromCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public int[] convertFromBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public int[] convertFromObjectArray() {
        return converter.convert(integerObjectArray);
    }

    @Benchmark
    public int[] convertFromCollection() {
        return converter.convert(integerCollection);
    }

    @Benchmark
    public int[] convertFromIterable() {
        return converter.convert(integerIterable);
    }

    @Benchmark
    public int[] convertFromCsvString() {
        return converter.convert(csvString);
    }

    @Benchmark
    public int[] convertFromSingleValue() {
        return converter.convert(singleValue);
    }
}
