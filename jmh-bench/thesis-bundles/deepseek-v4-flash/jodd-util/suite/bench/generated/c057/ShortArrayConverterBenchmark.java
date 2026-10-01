package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.*;
import jodd.typeconverter.impl.ShortArrayConverter;
import jodd.typeconverter.TypeConverterManager;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortArrayConverterBenchmark {

    private ShortArrayConverter converter;
    private TypeConverterManager manager;

    // Inputs
    private Short scalar;
    private Collection<Short> collection;
    private Iterable<Short> iterable;
    private String csvString;
    private short[] shortArray;
    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;
    private Integer[] objectArray;

    @Setup(Level.Trial)
    public void setup() {
        manager = TypeConverterManager.get();
        converter = new ShortArrayConverter(manager);

        scalar = (short) 42;

        collection = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            collection.add((short) i);
        }

        // Iterable that is not a Collection
        iterable = new Iterable<Short>() {
            @Override
            public Iterator<Short> iterator() {
                return new Iterator<Short>() {
                    private int count = 0;
                    @Override
                    public boolean hasNext() { return count < 10; }
                    @Override
                    public Short next() { return (short) (count++); }
                };
            }
        };

        csvString = "1,2,3,4,5,6,7,8,9,10";

        shortArray = new short[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        intArray = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        longArray = new long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};
        floatArray = new float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f};
        doubleArray = new double[]{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        byteArray = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        charArray = new char[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        booleanArray = new boolean[]{true, false, true, false, true, false, true, false, true, false};
        objectArray = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    }

    @Benchmark
    public short[] convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public short[] convertScalar() {
        return converter.convert(scalar);
    }

    @Benchmark
    public short[] convertCollection() {
        return converter.convert(collection);
    }

    @Benchmark
    public short[] convertIterable() {
        return converter.convert(iterable);
    }

    @Benchmark
    public short[] convertCharSequence() {
        return converter.convert(csvString);
    }

    @Benchmark
    public short[] convertShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public short[] convertIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public short[] convertLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public short[] convertFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public short[] convertDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public short[] convertByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public short[] convertCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public short[] convertBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public short[] convertObjectArray() {
        return converter.convert(objectArray);
    }
}
