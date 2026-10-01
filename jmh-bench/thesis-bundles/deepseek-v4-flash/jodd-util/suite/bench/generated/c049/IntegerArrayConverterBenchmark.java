package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.impl.IntegerArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntegerArrayConverterBenchmark {

    private IntegerArrayConverter converter;
    private Object nullValue;
    private Integer singleInteger;
    private String singleString;
    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;
    private String[] stringArray;
    private Integer[] integerArray;
    private List<Integer> integerCollection;
    private Iterable<Integer> integerIterable;
    private String csvString;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new IntegerArrayConverter(TypeConverterManager.get());
        nullValue = null;
        singleInteger = Integer.valueOf(7);
        singleString = "7";

        int size = 1024;
        intArray = new int[size];
        longArray = new long[size];
        floatArray = new float[size];
        doubleArray = new double[size];
        shortArray = new short[size];
        byteArray = new byte[size];
        charArray = new char[size];
        booleanArray = new boolean[size];
        stringArray = new String[size];
        integerArray = new Integer[size];

        for (int i = 0; i < size; i++) {
            intArray[i] = i;
            longArray[i] = i;
            floatArray[i] = i;
            doubleArray[i] = i;
            shortArray[i] = (short) i;
            byteArray[i] = (byte) i;
            charArray[i] = (char) i;
            booleanArray[i] = (i & 1) == 0;
            stringArray[i] = Integer.toString(i);
            integerArray[i] = Integer.valueOf(i);
        }

        integerCollection = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            integerCollection.add(i);
        }

        integerIterable = new IntegerIterable(intArray);

        StringBuilder sb = new StringBuilder(size * 4);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(i);
        }
        csvString = sb.toString();
    }

    @Benchmark
    public int[] convertNull() {
        return converter.convert(nullValue);
    }

    @Benchmark
    public int[] convertSingleInteger() {
        return converter.convert(singleInteger);
    }

    @Benchmark
    public int[] convertSingleString() {
        return converter.convert(singleString);
    }

    @Benchmark
    public int[] convertIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public int[] convertLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public int[] convertFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public int[] convertDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public int[] convertShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public int[] convertByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public int[] convertCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public int[] convertBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public int[] convertStringArray() {
        return converter.convert(stringArray);
    }

    @Benchmark
    public int[] convertIntegerObjectArray() {
        return converter.convert(integerArray);
    }

    @Benchmark
    public int[] convertCollection() {
        return converter.convert(integerCollection);
    }

    @Benchmark
    public int[] convertIterable() {
        return converter.convert(integerIterable);
    }

    @Benchmark
    public int[] convertCsvString() {
        return converter.convert(csvString);
    }

    private static class IntegerIterable implements Iterable<Integer> {
        private final int[] values;

        IntegerIterable(int[] values) {
            this.values = values;
        }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<Integer>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < values.length;
                }

                @Override
                public Integer next() {
                    return values[index++];
                }
            };
        }
    }
}
