package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.DoubleArrayConverter;

import jodd.typeconverter.TypeConverterManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleArrayConverterBenchmark {

    private DoubleArrayConverter converter;

    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private short[] shortArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;
    private double[] doubleArray;
    private String[] stringArray;
    private List<Double> doubleList;
    private Iterable<Double> doubleIterable;
    private String csvString;
    private Double scalarValue;
    private Object nullValue = null;

    @Setup(Level.Trial)
    public void setup() {
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new DoubleArrayConverter(manager);

        intArray = new int[]{1, 2, 3, 4, 5};
        longArray = new long[]{1L, 2L, 3L, 4L, 5L};
        floatArray = new float[]{1.1f, 2.2f, 3.3f, 4.4f, 5.5f};
        shortArray = new short[]{1, 2, 3, 4, 5};
        byteArray = new byte[]{1, 2, 3, 4, 5};
        charArray = new char[]{'1', '2', '3', '4', '5'};
        booleanArray = new boolean[]{true, false, true, false, true};
        doubleArray = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
        stringArray = new String[]{"1.0", "2.5", "3.14", "4.0", "5.0"};

        doubleList = new ArrayList<>();
        doubleList.add(1.0);
        doubleList.add(2.0);
        doubleList.add(3.0);
        doubleList.add(4.0);
        doubleList.add(5.0);

        doubleIterable = new Iterable<Double>() {
            @Override
            public Iterator<Double> iterator() {
                return doubleList.iterator();
            }
        };

        csvString = "1.0,2.5,3.14,4.0,5.0";
        scalarValue = Double.valueOf(42.0);
    }

    @Benchmark
    public double[] convertIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public double[] convertLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public double[] convertFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public double[] convertShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public double[] convertByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public double[] convertCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public double[] convertBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public double[] convertDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public double[] convertStringArray() {
        return converter.convert(stringArray);
    }

    @Benchmark
    public double[] convertCollection() {
        return converter.convert(doubleList);
    }

    @Benchmark
    public double[] convertIterable() {
        return converter.convert(doubleIterable);
    }

    @Benchmark
    public double[] convertCsvString() {
        return converter.convert(csvString);
    }

    @Benchmark
    public double[] convertScalar() {
        return converter.convert(scalarValue);
    }

    @Benchmark
    public double[] convertNull() {
        return converter.convert(nullValue);
    }
}
