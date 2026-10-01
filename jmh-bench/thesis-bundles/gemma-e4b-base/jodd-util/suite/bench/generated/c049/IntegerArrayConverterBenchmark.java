package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.IntegerArrayConverter;
import jodd.typeconverter.TypeConverterManager;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntegerArrayConverterBenchmark {

    private IntegerArrayConverter converter;

    // --- Inputs for convertValueToArray ---
    private List<Integer> collectionInput;
    private List<Object> iterableInput;
    private String stringInput;
    private Integer scalarInput;

    // --- Inputs for convertArrayToArray (now used via public convert method) ---
    private Object[] objectArrayInput;
    private long[] primitiveLongArrayInput;
    private float[] primitiveFloatArrayInput;
    private double[] primitiveDoubleArrayInput;
    private short[] primitiveShortArrayInput;
    private byte[] primitiveByteArrayInput;
    private char[] primitiveCharArrayInput;
    private boolean[] primitiveBooleanArrayInput;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize TypeConverterManager. Assuming a default/functional instance is available.
        TypeConverterManager typeConverterManager = new TypeConverterManager();
        converter = new IntegerArrayConverter(typeConverterManager);

        // 1. Collection Input (List<Integer>)
        collectionInput = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            collectionInput.add(i);
        }

        // 2. Iterable Input (List<Object> containing Integers)
        iterableInput = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            iterableInput.add(i);
        }

        // 3. CharSequence Input (String with comma-separated numbers)
        stringInput = "1,2,3,4,5,6,7,8,9,10";

        // 4. Scalar Input
        scalarInput = 42;

        // 5. Object Array Input (Object[] containing Integers)
        objectArrayInput = new Object[100];
        for (int i = 0; i < 100; i++) {
            objectArrayInput[i] = i;
        }

        // 6. Primitive Array Inputs
        primitiveLongArrayInput = new long[100];
        for (int i = 0; i < 100; i++) {
            primitiveLongArrayInput[i] = i * 2L;
        }

        primitiveFloatArrayInput = new float[100];
        for (int i = 0; i < 100; i++) {
            primitiveFloatArrayInput[i] = (float) i;
        }

        primitiveDoubleArrayInput = new double[100];
        for (int i = 0; i < 100; i++) {
            primitiveDoubleArrayInput[i] = (double) i * 1.5;
        }

        primitiveShortArrayInput = new short[100];
        for (int i = 0; i < 100; i++) {
            primitiveShortArrayInput[i] = (short) i;
        }

        primitiveByteArrayInput = new byte[100];
        for (int i = 0; i < 100; i++) {
            primitiveByteArrayInput[i] = (byte) i;
        }

        primitiveCharArrayInput = new char[100];
        for (int i = 0; i < 100; i++) {
            primitiveCharArrayInput[i] = (char) i;
        }

        primitiveBooleanArrayInput = new boolean[100];
        for (int i = 0; i < 100; i++) {
            primitiveBooleanArrayInput[i] = (i % 2 == 0);
        }
    }

    // --- Benchmarks for convert(Object value) ---

    @Benchmark
    public int[] convert_nullInput(Blackhole bh) {
        int[] result = converter.convert(null);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convert_scalarInput(Blackhole bh) {
        int[] result = converter.convert(scalarInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convert_collectionInput(Blackhole bh) {
        int[] result = converter.convert(collectionInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convert_iterableInput(Blackhole bh) {
        int[] result = converter.convert(iterableInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convert_stringInput(Blackhole bh) {
        int[] result = converter.convert(stringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convert_objectArrayInput(Blackhole bh) {
        int[] result = converter.convert(objectArrayInput);
        bh.consume(result);
        return result;
    }

    // --- Benchmarks for convertArrayToArray (Primitive Conversions) ---
    // NOTE: These now call the public convert(Object value) method, which handles array conversion internally.

    @Benchmark
    public int[] convertArray_longToInteger(Blackhole bh) {
        int[] result = converter.convert(primitiveLongArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convertArray_floatToInteger(Blackhole bh) {
        int[] result = converter.convert(primitiveFloatArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convertArray_doubleToInteger(Blackhole bh) {
        int[] result = converter.convert(primitiveDoubleArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convertArray_shortToInteger(Blackhole bh) {
        int[] result = converter.convert(primitiveShortArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convertArray_byteToInteger(Blackhole bh) {
        int[] result = converter.convert(primitiveByteArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convertArray_charToInteger(Blackhole bh) {
        int[] result = converter.convert(primitiveCharArrayInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int[] convertArray_booleanToInteger(Blackhole bh) {
        int[] result = converter.convert(primitiveBooleanArrayInput);
        bh.consume(result);
        return result;
    }
}
