package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jodd.typeconverter.TypeConverterManager;
import jodd.typeconverter.impl.ArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ArrayConverterBenchmark {

    private TypeConverterManager typeConverterManager;
    private ArrayConverter<Integer> arrayConverter;
    private final Class<Integer> targetType = Integer.class;

    // Fixtures for testing
    private Integer[] integerArray;
    private String[] stringArray;
    private Object collectionObject;
    private byte[] byteArray;

    @Setup
    public void setup() {
        // Initialize TypeConverterManager
        this.typeConverterManager = TypeConverterManager.get();

        this.arrayConverter = new ArrayConverter<>(typeConverterManager, targetType);

        // Setup Integer Array fixture
        integerArray = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        // Setup String Array fixture (not strictly used in benchmarks but kept for completeness)
        stringArray = new String[]{"a", "b", "c", "d", "e"};

        // Setup Collection Object fixture (List<Integer>)
        List<Integer> integerList = new ArrayList<>();
        integerList.add(100);
        integerList.add(200);
        integerList.add(300);
        collectionObject = integerList;

        // Setup Byte Array fixture
        byteArray = new byte[]{1, 2, 3, 4, 5};
    }

    @Benchmark
    public void convertArray_NonArrayInput_Collection(Blackhole bh) {
        // Test conversion from a Collection (List<Integer>)
        Integer[] result = arrayConverter.convert(collectionObject);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_NonArrayInput_String(Blackhole bh) {
        // Test conversion from a CharSequence (String)
        String inputString = "hello,world;test\nline2";
        Integer[] result = arrayConverter.convert(inputString);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_NonArrayInput_PrimitiveArray(Blackhole bh) {
        // Test conversion from a primitive array (int[])
        int[] inputIntArray = {1, 2, 3, 4};
        Integer[] result = arrayConverter.convert(inputIntArray);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_ArrayInput_SameType(Blackhole bh) {
        // Test conversion where source array component type matches target type (Integer[])
        Integer[] result = arrayConverter.convert(integerArray);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_ArrayInput_ObjectArray(Blackhole bh) {
        // Test conversion from an Object array (e.g., Object[])
        Object[] objectArray = new Object[]{1, "two", 3.0, true};
        Integer[] result = arrayConverter.convert(objectArray);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_ArrayInput_PrimitiveArray_Long(Blackhole bh) {
        // Test conversion from a primitive array (long[])
        long[] inputLongArray = {10L, 20L, 30L};
        Integer[] result = arrayConverter.convert(inputLongArray);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_ArrayInput_PrimitiveArray_Byte(Blackhole bh) {
        // Test conversion from a primitive array (byte[])
        byte[] inputByteArray = {(byte) 1, (byte) 2, (byte) 3};
        Integer[] result = arrayConverter.convert(inputByteArray);
        bh.consume(result);
    }
}
