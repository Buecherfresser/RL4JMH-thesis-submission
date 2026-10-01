package bench.generated.c004;

import org.fastfilter.bloom.count.SuccinctCountingBlockedBloom;
import org.fastfilter.utils.Hash;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBlockedBloomBenchmark {

    // State fields for the benchmark
    private SuccinctCountingBlockedBloom filter;
    private long[] testKeys;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize a fixed set of keys for testing.
        Random random = new Random(42);
        
        // Create a set of 100 random long keys.
        this.testKeys = new long[100];
        for (int i = 0; i < 100; i++) {
            this.testKeys[i] = random.nextLong();
        }

        try {
            // Construct the filter instance. This measures construction time.
            // We use a fixed setting (e.g., 10 bits per key)
            this.filter = SuccinctCountingBlockedBloom.construct(this.testKeys, 10);
        } catch (Exception e) {
            // Handle potential exceptions during construction if necessary, 
            // though for this benchmark, we assume success.
        }
    }

    @Benchmark
    public void testMayContain(Blackhole bh) {
        // Test a read-only operation.
        if (filter != null) {
            // Test containment of a key that is likely present (or not)
            boolean result = filter.mayContain(testKeys[0]);
            bh.consume(result);
        }
    }

    @Benchmark
    public void testCardinality(Blackhole bh) {
        // Test a read-only operation.
        if (filter != null) {
            long count = filter.cardinality();
            bh.consume(count);
        }
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        // Test a mutating operation. 
        if (filter != null) {
            try {
                filter.add(testKeys[0]);
            } catch (Exception e) {
                // Ignore exceptions during benchmark if they occur due to internal state issues
            }
        }
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        // Test another mutating operation.
        if (filter != null) {
            try {
                filter.remove(testKeys[0]);
            } catch (Exception e) {
                // Ignore exceptions
            }
        }
    }
    
    @Benchmark
    public void testConstructStaticFactory(Blackhole bh) {
        // Test the static factory method. This should be fast if the setup is efficient.
        try {
            // We call the static method directly.
            SuccinctCountingBlockedBloom.construct(testKeys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
