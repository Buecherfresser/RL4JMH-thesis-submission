package bench.generated.c033;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.BooleanArrayConverter;
import jodd.typeconverter.TypeConverterManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BooleanArrayConverterBenchmark {

    private BooleanArrayConverter converter;

    private boolean[] boolArray;
    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private byte[] byteArray;
    private char[] charArray;
    private String[] stringArray;

    private List<Boolean> boolList;
    private List<Integer> intList;
    private Iterable<Boolean> boolIterable;

    private CharSequence csvString;
    private Object singleBoolean;
    private Object singleInteger;
    private Object singleString;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setup() {
        TypeConverterManager tcm = TypeConverterManager.get();
        converter = new BooleanArrayConverter(tcm);

        boolArray = new boolean[] {true, false, true, false};
        intArray = new int[] {1, 0, 2, 0};
        longArray = new long[] {1L, 0L, 3L, 0L};
        floatArray = new float[] {1.0f, 0.0f, 2.5f, 0.0f};
        doubleArray = new double[] {1.0, 0.0, 3.3, 0.0};
        shortArray = new short[] {1, 0, 2, 0};
        byteArray = new byte[] {1, 0, 2, 0};
        charArray = new char[] {'a', '\0', 'b', '\0'};
        stringArray = new String[] {"true", "false", "true", "false"};

        boolList = Arrays.asList(true, false, true, false);
        intList = Arrays.asList(1, 0, 2, 0);
        boolIterable = boolList;

        csvString = "true,false,true,false";

        singleBoolean = Boolean.TRUE;
        singleInteger = Integer.valueOf(1);
        singleString = "true";
    }

    @Benchmark
    public boolean[] convertBooleanArray() {
        return converter.convert(boolArray);
    }

    @Benchmark
    public boolean[] convertIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public boolean[] convertLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public boolean[] convertFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public boolean[] convertDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public boolean[] convertShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public boolean[] convertByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public boolean[] convertCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public boolean[] convertStringArray() {
        return converter.convert(stringArray);
    }

    @Benchmark
    public boolean[] convertBooleanCollection() {
        return converter.convert(boolList);
    }

    @Benchmark
    public boolean[] convertIntegerCollection() {
        return converter.convert(intList);
    }

    @Benchmark
    public boolean[] convertBooleanIterable() {
        return converter.convert(boolIterable);
    }

    @Benchmark
    public boolean[] convertCsvString() {
        return converter.convert(csvString);
    }

    @Benchmark
    public boolean[] convertSingleBoolean() {
        return converter.convert(singleBoolean);
    }

    @Benchmark
    public boolean[] convertSingleInteger() {
        return converter.convert(singleInteger);
    }

    @Benchmark
    public boolean[] convertSingleString() {
        return converter.convert(singleString);
    }
}
