package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.FloatArrayConverter;
import jodd.typeconverter.TypeConverterManager;
import java.util.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatArrayConverterBenchmark {

    private FloatArrayConverter converter;

    // Primitive arrays
    private int[] intArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;

    // Collection / Iterable inputs
    private List<Float> floatList;
    private SimpleIterable simpleIterable;

    // CharSequence (CSV) input
    private String csvString;

    // Scalar and null
    private Integer scalarInteger;
    private Object nullValue;

    @Setup(Level.Trial)
    public void setUp() {
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new FloatArrayConverter(manager);

        intArray = new int[100];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = i;
        }

        floatArray = new float[100];
        for (int i = 0; i < floatArray.length; i++) {
            floatArray[i] = i + 0.5f;
        }

        doubleArray = new double[100];
        for (int i = 0; i < doubleArray.length; i++) {
            doubleArray[i] = i + 0.25;
        }

        shortArray = new short[100];
        for (int i = 0; i < shortArray.length; i++) {
            shortArray[i] = (short) i;
        }

        byteArray = new byte[100];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte) i;
        }

        charArray = new char[100];
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = (char) ('a' + (i % 26));
        }

        booleanArray = new boolean[100];
        for (int i = 0; i < booleanArray.length; i++) {
            booleanArray[i] = (i % 2) == 0;
        }

        floatList = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            floatList.add((float) i + 1.0f);
        }

        simpleIterable = new SimpleIterable(100);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append(i);
            if (i < 99) {
                sb.append(',');
            }
        }
        csvString = sb.toString();

        scalarInteger = 42;
        nullValue = null;
    }

    @Benchmark
    public float[] benchmarkConvertNull() {
        return converter.convert(nullValue);
    }

    @Benchmark
    public float[] benchmarkConvertIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public float[] benchmarkConvertFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public float[] benchmarkConvertDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public float[] benchmarkConvertShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public float[] benchmarkConvertByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public float[] benchmarkConvertCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public float[] benchmarkConvertBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public float[] benchmarkConvertCollection() {
        return converter.convert(floatList);
    }

    @Benchmark
    public float[] benchmarkConvertIterable() {
        return converter.convert(simpleIterable);
    }

    @Benchmark
    public float[] benchmarkConvertCSVString() {
        return converter.convert(csvString);
    }

    @Benchmark
    public float[] benchmarkConvertScalar() {
        return converter.convert(scalarInteger);
    }

    // Simple non-collection Iterable implementation
    private static class SimpleIterable implements Iterable<Integer> {
        private final int size;

        SimpleIterable(int size) {
            this.size = size;
        }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<Integer>() {
                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < size;
                }

                @Override
                public Integer next() {
                    return index++;
                }
            };
        }
    }
}
