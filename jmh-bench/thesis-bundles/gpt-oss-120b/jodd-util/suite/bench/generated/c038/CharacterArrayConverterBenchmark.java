package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.CharacterArrayConverter;
import jodd.typeconverter.TypeConverterManager;
import java.util.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharacterArrayConverterBenchmark {

    private CharacterArrayConverter converter;

    // Inputs
    private Object nullInput;
    private String stringInput;
    private Collection<Character> charCollectionInput;
    private Iterable<Integer> iterableInput;
    private int[] intArrayInput;
    private long[] longArrayInput;
    private float[] floatArrayInput;
    private double[] doubleArrayInput;
    private short[] shortArrayInput;
    private byte[] byteArrayInput;
    private boolean[] booleanArrayInput;
    private char[] charArrayInput;
    private Object[] objectArrayInput;

    @Setup(Level.Trial)
    public void setup() {
        TypeConverterManager manager = TypeConverterManager.get();
        converter = new CharacterArrayConverter(manager);

        nullInput = null;

        stringInput = "HelloWorld";

        charCollectionInput = new ArrayList<>();
        for (char c = 'a'; c <= 'j'; c++) {
            charCollectionInput.add(c);
        }

        List<Integer> iterableList = new ArrayList<>();
        for (int i = 65; i <= 74; i++) {
            iterableList.add(i);
        }
        iterableInput = iterableList;

        intArrayInput = new int[]{65, 66, 67, 68};
        longArrayInput = new long[]{69L, 70L, 71L};
        floatArrayInput = new float[]{72.0f, 73.0f, 74.0f};
        doubleArrayInput = new double[]{75.0, 76.0, 77.0};
        shortArrayInput = new short[]{78, 79, 80};
        byteArrayInput = new byte[]{81, 82, 83};
        booleanArrayInput = new boolean[]{true, false, true};
        charArrayInput = new char[]{'x', 'y', 'z'};

        objectArrayInput = new Object[]{"a", "b", "c"};
    }

    @Benchmark
    public char[] convertNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public char[] convertString() {
        return converter.convert(stringInput);
    }

    @Benchmark
    public char[] convertCharCollection() {
        return converter.convert(charCollectionInput);
    }

    @Benchmark
    public char[] convertIterable() {
        return converter.convert(iterableInput);
    }

    @Benchmark
    public char[] convertIntArray() {
        return converter.convert(intArrayInput);
    }

    @Benchmark
    public char[] convertLongArray() {
        return converter.convert(longArrayInput);
    }

    @Benchmark
    public char[] convertFloatArray() {
        return converter.convert(floatArrayInput);
    }

    @Benchmark
    public char[] convertDoubleArray() {
        return converter.convert(doubleArrayInput);
    }

    @Benchmark
    public char[] convertShortArray() {
        return converter.convert(shortArrayInput);
    }

    @Benchmark
    public char[] convertByteArray() {
        return converter.convert(byteArrayInput);
    }

    @Benchmark
    public char[] convertBooleanArray() {
        return converter.convert(booleanArrayInput);
    }

    @Benchmark
    public char[] convertCharArray() {
        return converter.convert(charArrayInput);
    }

    @Benchmark
    public char[] convertObjectArray() {
        return converter.convert(objectArrayInput);
    }
}
