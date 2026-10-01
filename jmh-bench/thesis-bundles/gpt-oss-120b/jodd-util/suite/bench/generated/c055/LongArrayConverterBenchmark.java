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
    private TypeConverterManager manager;

    private long[] longArray;
    private int[] intArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;
    private String[] stringArray;
    private Collection<Long> longCollection;
    private Iterable<Long> longIterable;
    private CharSequence csvString;
    private Long scalarValue;

    @Setup(Level.Trial)
    public void setup() {
        manager = new TypeConverterManager();
        converter = new LongArrayConverter(manager);

        int size = 100;
        longArray = new long[size];
        intArray = new int[size];
        floatArray = new float[size];
        doubleArray = new double[size];
        shortArray = new short[size];
        byteArray = new byte[size];
        charArray = new char[size];
        booleanArray = new boolean[size];
        stringArray = new String[size];
        List<Long> list = new ArrayList<>(size);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            long val = i * 10L;
            longArray[i] = val;
            intArray[i] = (int) val;
            floatArray[i] = (float) val;
            doubleArray[i] = (double) val;
            shortArray[i] = (short) val;
            byteArray[i] = (byte) val;
            charArray[i] = (char) (i % 65536);
            booleanArray[i] = (i % 2 == 0);
            stringArray[i] = Long.toString(val);
            list.add(val);
            sb.append(val);
            if (i < size - 1) {
                sb.append(',');
            }
        }
        longCollection = list;
        longIterable = list;
        csvString = sb.toString();
        scalarValue = 12345L;
    }

    @Benchmark
    public long[] benchmarkLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public long[] benchmarkIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public long[] benchmarkFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public long[] benchmarkDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public long[] benchmarkShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public long[] benchmarkByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public long[] benchmarkCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public long[] benchmarkBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public long[] benchmarkStringArray() {
        return converter.convert(stringArray);
    }

    @Benchmark
    public long[] benchmarkCollection() {
        return converter.convert(longCollection);
    }

    @Benchmark
    public long[] benchmarkIterable() {
        return converter.convert(longIterable);
    }

    @Benchmark
    public long[] benchmarkCharSequence() {
        return converter.convert(csvString);
    }

    @Benchmark
    public long[] benchmarkScalar() {
        return converter.convert(scalarValue);
    }
}
