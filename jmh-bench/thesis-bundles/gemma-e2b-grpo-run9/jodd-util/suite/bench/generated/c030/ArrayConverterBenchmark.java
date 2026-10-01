package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ArrayConverter;
import jodd.typeconverter.TypeConverterManager;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArrayConverterBenchmark {

    // State fields for the benchmark
    private ArrayConverter<Integer> arrayConverter;
    private TypeConverterManager typeConverterManager;

    @Setup
    public void setup() {
        // Initialize TypeConverterManager (null is acceptable if not used)
        this.typeConverterManager = null;
        // Initialize the converter instance. We use Integer.class as the target type.
        this.arrayConverter = new ArrayConverter<>(this.typeConverterManager, Integer.class);
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        // Test null input handling
        Object result = arrayConverter.convert(null);
        if (result != null) {
            bh.consume(result);
        }
    }

    @Benchmark
    public void convertNonArrayString(Blackhole bh) {
        // Test conversion of a non-array (String)
        try {
            Object result = arrayConverter.convert("hello world");
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore exceptions for benchmark stability
        }
    }

    @Benchmark
    public void convertArrayOfIntegers(Blackhole bh) {
        // Test conversion of an existing array (equal types, should return original array)
        Integer[] input = {1, 2, 3, 4, 5};
        try {
            Integer[] result = (Integer[]) arrayConverter.convert(input);
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convertCollection(Blackhole bh) {
        // Test conversion of a Collection (e.g., List)
        // Using a simple, immutable list for testing.
        java.util.List<Object> inputList = java.util.Arrays.asList(1, "two", 3.0);
        try {
            Object result = arrayConverter.convert(inputList);
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convertPrimitiveArray(Blackhole bh) {
        // Test conversion of a primitive array (int[])
        // Note: The SUT handles the conversion from int[] to Integer[] internally.
        int[] input = {10, 20, 30, 40};
        try {
            Integer[] result = (Integer[]) arrayConverter.convert(input);
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convertObjectArray(Blackhole bh) {
        // Test conversion of an object array (e.g., Object[])
        Object[] input = {1, "two", 3.0};
        try {
            Integer[] result = (Integer[]) arrayConverter.convert(input);
            if (result != null) {
                bh.consume(result);
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
