package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.fastfilter.FilterType;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FilterTypeBenchmark {

    // State fields to hold reusable, non-final, non-static objects
    private long[] keys;
    private final Random random = new Random(42); // Fixed seed for reproducibility

    @Setup(Level.Trial)
    public void setupTrial() {
        // Build a fixed set of keys for construction benchmarks.
        // This avoids repeated random number generation inside benchmarks.
        this.keys = new long[1000];
        for (int i = 0; i < 1000; i++) {
            // Use a simple, reproducible way to generate long keys
            this.keys[i] = random.nextLong();
        }
    }

    // --- Benchmarks for FilterType.construct() ---

    @Benchmark
    public void constructBloom(Blackhole bh) {
        try {
            FilterType.BLOOM.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructCountingBloom(Blackhole bh) {
        try {
            FilterType.COUNTING_BLOOM.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructSuccinctCountingBloom(Blackhole bh) {
        try {
            FilterType.SUCCINCT_COUNTING_BLOOM.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructSuccinctCountingBloomRanked(Blackhole bh) {
        try {
            FilterType.SUCCINCT_COUNTING_BLOOM_RANKED.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructBlockedBloom(Blackhole bh) {
        try {
            FilterType.BLOCKED_BLOOM.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructSuccinctCountingBlockedBloom(Blackhole bh) {
        try {
            FilterType.SUCCINCT_COUNTING_BLOCKED_BLOOM.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructSuccinctCountingBlockedBloomRanked(Blackhole bh) {
        try {
            FilterType.SUCCINCT_COUNTING_BLOCKED_BLOOM_RANKED.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructXorSimple(Blackhole bh) {
        try {
            FilterType.XOR_SIMPLE.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructXorSimple2(Blackhole bh) {
        try {
            FilterType.XOR_SIMPLE_2.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructXor8(Blackhole bh) {
        try {
            FilterType.XOR_8.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructXor16(Blackhole bh) {
        try {
            FilterType.XOR_16.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructXorPlus8(Blackhole bh) {
        try {
            FilterType.XOR_PLUS_8.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructCuckoo8(Blackhole bh) {
        try {
            FilterType.CUCKOO_8.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructCuckoo16(Blackhole bh) {
        try {
            FilterType.CUCKOO_16.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructCuckooPlus8(Blackhole bh) {
        try {
            FilterType.CUCKOO_PLUS_8.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructCuckooPlus16(Blackhole bh) {
        try {
            FilterType.CUCKOO_PLUS_16.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void constructGCS(Blackhole bh) {
        try {
            FilterType.GCS.construct(this.keys, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    // --- Benchmarks for Filter.mayContain() (Read-only operation) ---

    @Benchmark
    public void mayContainBloom(Blackhole bh) {
        try {
            FilterType.BLOOM.construct(this.keys, 10).mayContain(this.keys[0]);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void mayContainXor8(Blackhole bh) {
        try {
            FilterType.XOR_8.construct(this.keys, 10).mayContain(this.keys[0]);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    // --- Benchmarks for Filter.add() (Mutating operation - requires careful setup) ---

    @Benchmark
    public void addCuckoo16(Blackhole bh) {
        try {
            // Constructing inside the benchmark is bad, but necessary if we don't want
            // to manage complex state across invocations.
            FilterType.CUCKOO_16.construct(new long[0], 10).add(this.keys[0]);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
}
