package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.DoubleArrayConverter;
import jodd.typeconverter.TypeConverterManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleArrayConverterBenchmark {

    private DoubleArrayConverter converter;

    private double[] doubleArrayInput;
    private int[] intArrayInput;
    private long[] longArrayInput;
    private float[] floatArrayInput;
    private short[] shortArrayInput;
    private byte[] byteArrayInput;
    private char[] charArrayInput;
    private boolean[] booleanArrayInput;
    private String[] stringArrayInput;
    private Double[] boxedDoubleArrayInput;
    private String csvInput;
    private Collection<String> collectionInput;
    private Iterable<String> iterableInput;
    private Double scalarInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new DoubleArrayConverter(TypeConverterManager.get());

        int n = 64;

        doubleArrayInput = new double[n];
        intArrayInput = new int[n];
        longArrayInput = new long[n];
        floatArrayInput = new float[n];
        shortArrayInput = new short[n];
        byteArrayInput = new byte[n];
        charArrayInput = new char[n];
        booleanArrayInput = new boolean[n];
        stringArrayInput = new String[n];
        boxedDoubleArrayInput = new Double[n];

        for (int i = 0; i < n; i++) {
            double v = i * 1.5;
            doubleArrayInput[i] = v;
            intArrayInput[i] = i;
            longArrayInput[i] = i;
            floatArrayInput[i] = (float) v;
            shortArrayInput[i] = (short) i;
            byteArrayInput[i] = (byte) i;
            charArrayInput[i] = (char) ('0' + (i % 10));
            booleanArrayInput[i] = (i % 2) == 0;
            stringArrayInput[i] = Integer.toString(i);
            boxedDoubleArrayInput[i] = Double.valueOf(v);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(i * 2.5);
        }
        csvInput = sb.toString();

        List<String> backing = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            backing.add(Integer.toString(i));
        }
        collectionInput = backing;
        iterableInput = () -> backing.iterator();

        scalarInput = Double.valueOf(42.5);
    }

    @Benchmark
    public double[] convertDoubleArray() {
        return converter.convert(doubleArrayInput);
    }

    @Benchmark
    public double[] convertIntArray() {
        return converter.convert(intArrayInput);
    }

    @Benchmark
    public double[] convertLongArray() {
        return converter.convert(longArrayInput);
    }

    @Benchmark
    public double[] convertFloatArray() {
        return converter.convert(floatArrayInput);
    }

    @Benchmark
    public double[] convertShortArray() {
        return converter.convert(shortArrayInput);
    }

    @Benchmark
    public double[] convertByteArray() {
        return converter.convert(byteArrayInput);
    }

    @Benchmark
    public double[] convertCharArray() {
        return converter.convert(charArrayInput);
    }

    @Benchmark
    public double[] convertBooleanArray() {
        return converter.convert(booleanArrayInput);
    }

    @Benchmark
    public double[] convertStringArray() {
        return converter.convert(stringArrayInput);
    }

    @Benchmark
    public double[] convertBoxedDoubleArray() {
        return converter.convert(boxedDoubleArrayInput);
    }

    @Benchmark
    public double[] convertCsvString() {
        return converter.convert(csvInput);
    }

    @Benchmark
    public double[] convertCollection() {
        return converter.convert(collectionInput);
    }

    @Benchmark
    public double[] convertIterable() {
        return converter.convert(iterableInput);
    }

    @Benchmark
    public double[] convertScalar() {
        return converter.convert(scalarInput);
    }
}
