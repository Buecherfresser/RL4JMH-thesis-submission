package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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

        // Initialize ArrayConverter with the target type
        this.arrayConverter = new ArrayConverter<>(typeConverterManager, targetType);

        // Setup Integer Array fixture
        integerArray = new Integer[]{1, 2, 3, 4, 5};

        // Setup String Array fixture
        stringArray = new String[]{"a", "b", "c", "d", "e"};

        // Setup Collection fixture (List of Integers)
        List<Integer> intList = new ArrayList<>();
        intList.add(10);
        intList.add(20);
        intList.add(30);
        collectionObject = intList;

        // Setup Byte Array fixture
        byteArray = new byte[]{1, 2, 3, 4, 5};
    }

    @Benchmark
    public void convertArray_FromIntegerArray(Blackhole bh) {
        // Test conversion from an Integer array
        Integer[] result = arrayConverter.convert(integerArray);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_FromStringArray(Blackhole bh) {
        // Test conversion from a String array
        Integer[] result = arrayConverter.convert(stringArray);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_FromCollection(Blackhole bh) {
        // Test conversion from a Collection (List<Integer>)
        Integer[] result = arrayConverter.convert(collectionObject);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_ToSingleElementArray_FromInteger(Blackhole bh) {
        // Test conversion of a single Integer value
        Integer singleValue = 99;
        Integer[] result = arrayConverter.convert(singleValue);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_FromPrimitiveArray(Blackhole bh) {
        // Test conversion of a primitive array (int[])
        int[] primitiveIntArray = {10, 20, 30};
        Integer[] result = arrayConverter.convert(primitiveIntArray);
        bh.consume(result);
    }

    @Benchmark
    public void convertArray_FromByteArray(Blackhole bh) {
        // Test conversion of a byte array
        byte[] inputBytes = {1, 2, 3, 4, 5};
        Integer[] result = arrayConverter.convert(inputBytes);
        bh.consume(result);
    }
}
