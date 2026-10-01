package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jodd.typeconverter.TypeConverterManager;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class TypeConverterManagerBenchmark {

    // Access the singleton instance once for all benchmarks
    private final TypeConverterManager manager = TypeConverterManager.get();

    /**
     * Benchmark for a simple conversion where a registered converter exists
     * (e.g., String to Integer, which is registered by default).
     */
    @Benchmark
    public void testConvertType_Registered(Blackhole bh) {
        try {
            // Test conversion of a String to Integer (registered)
            Object result = manager.convertType("12345", Integer.class);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for this specific benchmark if they are expected
        }
    }

    /**
     * Benchmark for a conversion that should fail because no converter is registered
     * for the target type (testing the failure path).
     */
    @Benchmark
    public void testConvertType_Unregistered(Blackhole bh) {
        try {
            // Attempt conversion to a type that is unlikely to be registered
            manager.convertType("some_value", java.util.Date.class);
        } catch (Exception e) {
            // Expected behavior if conversion fails due to missing converter
        }
    }

    /**
     * Benchmark for conversion to a primitive array type (testing ArrayConverter logic).
     */
    @Benchmark
    public void testConvertType_ToPrimitiveArray(Blackhole bh) {
        try {
            // Test conversion of a String to Integer[]
            Object result = manager.convertType("100", Integer[].class);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    /**
     * Benchmark for collection conversion (testing CollectionConverter logic).
     */
    @Benchmark
    public void testConvertToCollection(Blackhole bh) {
        try {
            // Test conversion of a String to a List<String> (using Object.class as component type)
            Object result = manager.convertToCollection("hello", List.class, Object.class);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    /**
     * Benchmark for conversion of null input.
     */
    @Benchmark
    public void testConvertType_NullInput(Blackhole bh) {
        try {
            Object result = manager.convertType(null, Integer.class);
            bh.consume(result);
        } catch (Exception e) {
            // Should not throw
        }
    }
}
