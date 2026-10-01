package bench.generated.c057;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
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

    private short[] shortArray;
    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;

    private Integer[] integerObjectArray;
    private String[] stringArray;

    private java.util.List<Short> shortList;
    private java.util.List<Integer> intList;
    private java.util.Set<Short> shortSet;

    private String csvString;
    private Integer scalarInt;
    private Object nullValue;

    @Setup
    public void setup() {
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new ShortArrayConverter(manager);

        int size = 128;

        shortArray = new short[size];
        intArray = new int[size];
        longArray = new long[size];
        floatArray = new float[size];
        doubleArray = new double[size];
        byteArray = new byte[size];
        charArray = new char[size];
        booleanArray = new boolean[size];

        integerObjectArray = new Integer[size];
        stringArray = new String[size];

        shortList = new java.util.ArrayList<>(size);
        intList = new java.util.ArrayList<>(size);
        shortSet = new java.util.HashSet<>(size);

        StringBuilder csvBuilder = new StringBuilder();

        for (int i = 0; i < size; i++) {
            shortArray[i] = (short) i;
            intArray[i] = i;
            longArray[i] = i;
            floatArray[i] = i + 0.5f;
            doubleArray[i] = i + 0.5;
            byteArray[i] = (byte) i;
            charArray[i] = (char) ('a' + (i % 26));
            booleanArray[i] = (i % 2) == 0;

            integerObjectArray[i] = i;
            stringArray[i] = Integer.toString(i);

            shortList.add((short) i);
            intList.add(i);
            shortSet.add((short) i);

            csvBuilder.append(i);
            if (i < size - 1) {
                csvBuilder.append(',');
            }
        }

        csvString = csvBuilder.toString();
        scalarInt = 42;
        nullValue = null;
    }

    @Benchmark
    public short[] convertFromShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public short[] convertFromIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public short[] convertFromLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public short[] convertFromFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public short[] convertFromDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public short[] convertFromByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public short[] convertFromCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public short[] convertFromBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public short[] convertFromIntegerObjectArray() {
        return converter.convert(integerObjectArray);
    }

    @Benchmark
    public short[] convertFromStringArray() {
        return converter.convert(stringArray);
    }

    @Benchmark
    public short[] convertFromShortList() {
        return converter.convert(shortList);
    }

    @Benchmark
    public short[] convertFromIntList() {
        return converter.convert(intList);
    }

    @Benchmark
    public short[] convertFromShortSet() {
        return converter.convert(shortSet);
    }

    @Benchmark
    public short[] convertFromCsvString() {
        return converter.convert(csvString);
    }

    @Benchmark
    public short[] convertFromScalarInteger() {
        return converter.convert(scalarInt);
    }

    @Benchmark
    public short[] convertFromNull() {
        return converter.convert(nullValue);
    }
}
