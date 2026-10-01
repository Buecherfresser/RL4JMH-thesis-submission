package bench.generated.c062;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.StringArrayConverter;
import jodd.typeconverter.TypeConverterManager;
import java.util.List;
import java.util.ArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringArrayConverterBenchmark {

    private StringArrayConverter converter;

    private int[] intArray;
    private long[] longArray;
    private float[] floatArray;
    private double[] doubleArray;
    private short[] shortArray;
    private byte[] byteArray;
    private char[] charArray;
    private boolean[] booleanArray;

    private String csvString;
    private String[] stringArray;
    private List<String> stringList;

    @org.openjdk.jmh.annotations.Setup
    public void setUp() {
        TypeConverterManager manager = new TypeConverterManager();
        converter = new StringArrayConverter(manager);

        int size = 128;

        intArray = new int[size];
        longArray = new long[size];
        floatArray = new float[size];
        doubleArray = new double[size];
        shortArray = new short[size];
        byteArray = new byte[size];
        charArray = new char[size];
        booleanArray = new boolean[size];

        for (int i = 0; i < size; i++) {
            intArray[i] = i;
            longArray[i] = i;
            floatArray[i] = i + 0.5f;
            doubleArray[i] = i + 0.25;
            shortArray[i] = (short) i;
            byteArray[i] = (byte) i;
            charArray[i] = (char) ('a' + (i % 26));
            booleanArray[i] = (i % 2) == 0;
        }

        stringList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            stringList.add("value" + i);
        }

        // Build CSV string from the list
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(stringList.get(i));
        }
        csvString = sb.toString();

        // Prepare a String[] directly
        stringArray = stringList.toArray(new String[0]);
    }

    @Benchmark
    public String[] convertFromIntArray() {
        return converter.convert(intArray);
    }

    @Benchmark
    public String[] convertFromLongArray() {
        return converter.convert(longArray);
    }

    @Benchmark
    public String[] convertFromFloatArray() {
        return converter.convert(floatArray);
    }

    @Benchmark
    public String[] convertFromDoubleArray() {
        return converter.convert(doubleArray);
    }

    @Benchmark
    public String[] convertFromShortArray() {
        return converter.convert(shortArray);
    }

    @Benchmark
    public String[] convertFromByteArray() {
        return converter.convert(byteArray);
    }

    @Benchmark
    public String[] convertFromCharArray() {
        return converter.convert(charArray);
    }

    @Benchmark
    public String[] convertFromBooleanArray() {
        return converter.convert(booleanArray);
    }

    @Benchmark
    public String[] convertFromCsvString() {
        return converter.convert(csvString);
    }

    @Benchmark
    public String[] convertFromStringArray() {
        return converter.convert(stringArray);
    }

    @Benchmark
    public String[] convertFromStringList(Blackhole bh) {
        String[] result = converter.convert(stringList);
        bh.consume(result);
        return result;
    }
}
