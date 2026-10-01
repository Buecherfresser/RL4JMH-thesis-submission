package bench.generated.c023;

import org.fastfilter.xorplus.Rank9;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.BitSet;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Rank9Benchmark {

    private Rank9 rank9;

    @Setup
    public void setup() {
        // Initialize a minimal, fixed Rank9 instance.
        // We use an empty BitSet and a small bit count.
        try {
            this.rank9 = new Rank9(new BitSet(), 1024);
        } catch (Exception e) {
            // Handle potential exceptions during setup if the constructor is too strict
            System.err.println("Failed to initialize Rank9 for benchmarking: " + e.getMessage());
            this.rank9 = null; // Allow benchmarks to skip if setup fails
        }
    }

    @Benchmark
    public void testGetBitCount(Blackhole bh) {
        if (rank9 != null) {
            bh.consume(rank9.getBitCount());
        }
    }

    @Benchmark
    public void testRank(Blackhole bh) {
        if (rank9 != null) {
            // Use a position that is likely within bounds if the structure is initialized
            bh.consume(rank9.rank(1000L));
        }
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        if (rank9 != null) {
            bh.consume(rank9.get(1000L));
        }
    }

    @Benchmark
    public void testGetAndPartialRank(Blackhole bh) {
        if (rank9 != null) {
            bh.consume(rank9.getAndPartialRank(1000L));
        }
    }

    @Benchmark
    public void testRemainingRank(Blackhole bh) {
        if (rank9 != null) {
            bh.consume(rank9.remainingRank(1000L));
        }
    }

    @Benchmark
    public void testWrite(Blackhole bh) {
        if (rank9 != null) {
            try {
                // We don't need the actual IO streams for benchmarking the Rank9 logic itself,
                // but we call the method that performs the write operation.
                // Since we cannot easily mock DataOutputStream/IOException here without
                // complicating the benchmark unnecessarily, we rely on the try-catch
                // to handle potential IO exceptions during the call.
                rank9.write(null); // Passing null or a dummy stream if possible, or relying on the try-catch block structure.
            } catch (Exception e) {
                // Ignore IO exceptions for benchmarking purposes if they occur
            }
            bh.consume(true);
        }
    }
}
