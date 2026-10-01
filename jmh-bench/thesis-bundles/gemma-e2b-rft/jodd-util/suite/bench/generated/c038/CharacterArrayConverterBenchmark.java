package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.impl.CharacterArrayConverter;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharacterArrayConverterBenchmark {

    private CharacterArrayConverter converter;
    private TypeConverterManager typeConverterManager;

    // Fixed input data for testing
    private final String inputString = "Hello, World! This is a test string for character array conversion.";
    private final char[] inputChars = inputString.toCharArray();
    private final List<Character> charList = new ArrayList<>();

    @Setup
    public void setup() {
        // Initialize the manager and the converter
        this.typeConverterManager = TypeConverterManager.get();
        this.converter = new CharacterArrayConverter(this.typeConverterManager);

        // Prepare a collection input for testing Collection conversion path
        for (char c : inputChars) {
            charList.add(Character.valueOf(c));
        }
    }

    // Benchmark 1: Convert String (CharSequence) to char[]
    @Benchmark
    public void benchmarkConvertCharSequence(Blackhole bh) {
        char[] result = converter.convert(inputString);
        bh.consume(result);
    }

    // Benchmark 2: Convert char[] directly (Array input path)
    @Benchmark
    public void benchmarkConvertCharArray(Blackhole bh) {
        char[] result = converter.convert(inputChars);
        bh.consume(result);
    }

    // Benchmark 3: Convert Collection<Character> to char[] (Collection path)
    @Benchmark
    public void benchmarkConvertCollection(Blackhole bh) {
        // Use the pre-built list from setup
        char[] result = converter.convert(charList);
        bh.consume(result);
    }

    // Benchmark 4: Convert null input
    @Benchmark
    public void benchmarkConvertNull(Blackhole bh) {
        char[] result = converter.convert(null);
        bh.consume(result);
    }

    // Benchmark 5: Convert a single character (Object path -> Single Element Array)
    @Benchmark
    public void benchmarkConvertSingleChar(Blackhole bh) {
        char c = 'A';
        char[] result = converter.convert(c);
        bh.consume(result);
    }

    // Benchmark 6: Convert an Integer array (Primitive array path: int[])
    @Benchmark
    public void benchmarkConvertIntArray(Blackhole bh) {
        int[] intArray = {1, 2, 3, 4, 5};
        char[] result = converter.convert(intArray);
        bh.consume(result);
    }

    // Benchmark 7: Convert a Long array (Primitive array path: long[])
    @Benchmark
    public void benchmarkConvertLongArray(Blackhole bh) {
        long[] longArray = {10L, 20L, 30L};
        char[] result = converter.convert(longArray);
        bh.consume(result);
    }

    // Benchmark 8: Convert a Float array (Primitive array path: float[])
    @Benchmark
    public void benchmarkConvertFloatArray(Blackhole bh) {
        float[] floatArray = {1.1f, 2.2f, 3.3f};
        char[] result = converter.convert(floatArray);
        bh.consume(result);
    }

    // Benchmark 9: Convert a Double array (Primitive array path: double[])
    @Benchmark
    public void benchmarkConvertDoubleArray(Blackhole bh) {
        double[] doubleArray = {1.0, 2.5, 3.7};
        char[] result = converter.convert(doubleArray);
        bh.consume(result);
    }

    // Benchmark 10: Convert a Byte array (Primitive array path: byte[])
    @Benchmark
    public void benchmarkConvertByteArray(Blackhole bh) {
        byte[] byteArray = {10, 20, 30, (byte) 255};
        char[] result = converter.convert(byteArray);
        bh.consume(result);
    }

    // Benchmark 11: Convert a Boolean array (Primitive array path: boolean[])
    @Benchmark
    public void benchmarkConvertBooleanArray(Blackhole bh) {
        boolean[] booleanArray = {true, false, true};
        char[] result = converter.convert(booleanArray);
        bh.consume(result);
    }

    // Benchmark 12: Convert an Object array (Object array path)
    @Benchmark
    public void benchmarkConvertObjectArray(Blackhole bh) {
        Object[] objectArray = {1, "two", 3.0};
        char[] result = converter.convert(objectArray);
        bh.consume(result);
    }

    // Benchmark 13: Convert a CharSequence (e.g., using a String)
    @Benchmark
    public void benchmarkConvertStringAsCharSequence(Blackhole bh) {
        String s = "Test String";
        char[] result = converter.convert(s);
        bh.consume(result);
    }

    // Benchmark 14: Convert a large collection (Stress test Collection path)
    @Benchmark
    public void benchmarkConvertLargeCollection(Blackhole bh) {
        List<Character> largeList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeList.add((char) ('a' + (i % 26)));
        }
        char[] result = converter.convert(largeList);
        bh.consume(result);
    }

    // Benchmark 15: Test conversion using a specific type converter (e.g., IntegerArrayConverter path)
    @Benchmark
    public void benchmarkConvertIntegerArrayAgain(Blackhole bh) {
        int[] intArray = {100, 200, 300};
        char[] result = converter.convert(intArray);
        bh.consume(result);
    }

    // Benchmark 16: Test conversion of a large CharSequence (Stress test CharSequence path)
    @Benchmark
    public void benchmarkConvertLargeCharSequence(Blackhole bh) {
        String largeString = "A".repeat(50000);
        char[] result = converter.convert(largeString);
        bh.consume(result);
    }
}
