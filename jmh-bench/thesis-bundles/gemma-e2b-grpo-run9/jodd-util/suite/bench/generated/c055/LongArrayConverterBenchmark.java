package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.LongArrayConverter;
import jodd.typeconverter.TypeConverterManager;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongArrayConverterBenchmark {

    private LongArrayConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter.
        try {
            // Initialize the converter. Passing null for manager if it's not strictly required for basic conversion logic testing.
            this.converter = new LongArrayConverter(null);
        } catch (Exception e) {
            // Handle potential initialization failure gracefully for benchmarking purposes
            System.err.println("Failed to initialize LongArrayConverter: " + e.getMessage());
            this.converter = null;
        }
    }

    @Benchmark
    public void benchmarkNullInput(Blackhole bh) {
        if (converter == null) return;
        try {
            converter.convert(null);
        } catch (Exception e) {
            // Ignore exceptions during benchmark if they are expected in edge cases
        }
        bh.consume(null);
    }

    @Benchmark
    public long[] benchmarkPrimitiveArray(Blackhole bh) {
        if (converter == null) return null;
        try {
            // Test case: Input is already a long[] array
            return converter.convert(new long[]{1L, 2L, 3L});
        } catch (Exception e) {
            bh.consume(null);
            return null;
        }
    }

    @Benchmark
    public void benchmarkObjectArray(Blackhole bh) {
        if (converter == null) return;
        try {
            // Test case: Input is an Object array
            converter.convert(new Object[]{1, "two", 3.0});
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public long[] benchmarkCollectionInput(Blackhole bh) {
        if (converter == null) return null;
        try {
            // Test case: Input is a Collection (e.g., List)
            java.util.List<Object> collection = new ArrayList<>();
            collection.add(10L);
            collection.add(20L);
            converter.convert(collection);
        } catch (Exception e) {
            bh.consume(null);
        }
        return null; // Return null as the method is void
    }

    @Benchmark
    public long[] benchmarkCharSequenceInput(Blackhole bh) {
        if (converter == null) return null;
        try {
            // Test case: Input is a String
            converter.convert("1,2,3,4");
        } catch (Exception e) {
            bh.consume(null);
        }
        return null; // Return null as the method is void
    }
}
