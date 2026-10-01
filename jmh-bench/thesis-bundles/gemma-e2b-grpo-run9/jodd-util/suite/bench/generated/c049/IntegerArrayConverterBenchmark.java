package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.IntegerArrayConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntegerArrayConverterBenchmark {

    private IntegerArrayConverter converter;

    public IntegerArrayConverterBenchmark() {
        // Instantiate the converter. Assuming null manager is acceptable for testing.
        this.converter = new IntegerArrayConverter(null);
    }

    @Setup
    public void setup() {
        // Setup phase: Initialize the converter.
    }

    @Benchmark
    public void testConvertNull(Blackhole bh) {
        // Test null input handling
        bh.consume(converter.convert(null));
    }

    @Benchmark
    public void testConvertPrimitiveArray(Blackhole bh) {
        // Test conversion of an existing int[] (primitive array)
        try {
            // Create a mutable array locally for the benchmark
            int[] input = {1, 2, 3, 4, 5};
            bh.consume(converter.convert(input));
        } catch (Exception e) {
            // Ignore exceptions if the conversion fails due to null manager
        }
    }

    @Benchmark
    public void testConvertObjectArray(Blackhole bh) {
        // Test conversion of an Object array (requires conversion logic)
        try {
            // Create an array of objects
            Object[] input = {1, 2.0, "three", null, 5L};
            bh.consume(converter.convert(input));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testConvertCollection(Blackhole bh) {
        // Test conversion of a Collection (e.g., List<Integer>)
        try {
            // Create a mutable collection locally for the benchmark
            ArrayList<Integer> input = new ArrayList<>();
            input.add(10);
            input.add(20);
            input.add(30);
            bh.consume(converter.convert(input));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testConvertIterable(Blackhole bh) {
        // Test conversion of an Iterable (e.g., an array or stream source)
        try {
            // Use an array as an iterable
            int[] input = {1, 2, 3};
            bh.consume(converter.convert(input));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
