package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ByteArrayConverter;
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
public class ByteArrayConverterBenchmark {

    private ByteArrayConverter converter;

    private byte[] byteArray;
    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private char[] charArray;
    private boolean[] booleanArray;

    private Byte[] byteObjArray;
    private Integer[] intObjArray;

    private String csvString;

    private Collection<Integer> intCollection;
    private Iterable<Integer> intIterable;

    private Integer singleInt;

    @Setup(Level.Trial)
    public void setup() {
        TypeConverterManager manager = new TypeConverterManager();
        converter = new ByteArrayConverter(manager);

        int size = 1024;

        byteArray = new byte[size];
        intArray = new int[size];
        longArray = new long[size];
        floatArray = new float[size];
        doubleArray = new double[size];
        shortArray = new short[size];
        charArray = new char[size];
        booleanArray = new boolean[size];

        byteObjArray = new Byte[size];
        intObjArray = new Integer[size];

        List<Integer> list = new ArrayList<>(size);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            byte val = (byte) i;
            byteArray[i] = val;
            intArray[i] = i;
            longArray[i] = i;
            floatArray[i] = i;
            doubleArray[i] = i;
            shortArray[i] = (short) i;
            charArray[i] = (char) i;
            booleanArray[i] = (i % 2) == 0;

            byteObjArray[i] = Byte.valueOf(val);
            intObjArray[i] = Integer.valueOf(i);

            list.add(i);
            sb.append(i);
            if (i < size - 1) {
                sb.append(',');
            }
        }

        intCollection = list;
        intIterable = list;
        csvString = sb.toString();
        singleInt = Integer.valueOf(42);
    }

    @Benchmark
    public byte[] convertFromByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public byte[] convertFromIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public byte[] convertFromLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public byte[] convertFromFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public byte[] convertFromDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public byte[] convertFromShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public byte[] convertFromCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public byte[] convertFromBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public byte[] convertFromByteObjectArray() {
        return converter.convert(byteObjArray);
    }

    @Benchmark
    public byte[] convertFromIntegerObjectArray() {
        return converter.convert(intObjArray);
    }

    @Benchmark
    public byte[] convertFromCsvString() {
        return converter.convert(csvString);
    }

    @Benchmark
    public byte[] convertFromCollection() {
        return converter.convert(intCollection);
    }

    @Benchmark
    public byte[] convertFromIterable() {
        return converter.convert(intIterable);
    }

    @Benchmark
    public byte[] convertFromSingleElement() {
        return converter.convert(singleInt);
    }

    @Benchmark
    public byte[] convertFromNull() {
        return converter.convert(null);
    }
}
