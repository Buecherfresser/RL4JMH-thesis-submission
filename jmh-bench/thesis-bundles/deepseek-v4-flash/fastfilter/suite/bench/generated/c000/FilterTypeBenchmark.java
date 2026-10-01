package bench.generated.c000;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.fastfilter.Filter;
import org.fastfilter.FilterType;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FilterTypeBenchmark {

    private long[] keys;
    private int setting = 8;

    @Setup(Level.Trial)
    public void setup() {
        int n = 10000;
        Random random = new Random(12345);
        keys = new long[n];
        for (int i = 0; i < n; i++) {
            keys[i] = random.nextLong();
        }
    }

    // Retry for XOR_SIMPLE and XOR_SIMPLE_2 (may throw ArrayIndexOutOfBoundsException)
    private Filter constructRetry(FilterType type, long[] keys, int setting) {
        if (type == FilterType.XOR_SIMPLE || type == FilterType.XOR_SIMPLE_2) {
            for (int attempt = 0; attempt < 100; attempt++) {
                try {
                    return type.construct(keys, setting);
                } catch (ArrayIndexOutOfBoundsException e) {
                    // retry
                }
            }
            throw new RuntimeException("Failed to construct after 100 attempts");
        } else {
            return type.construct(keys, setting);
        }
    }

    @Benchmark
    public Filter bloom() {
        return constructRetry(FilterType.BLOOM, keys, setting);
    }

    @Benchmark
    public Filter countingBloom() {
        return constructRetry(FilterType.COUNTING_BLOOM, keys, setting);
    }

    @Benchmark
    public Filter succinctCountingBloom() {
        return constructRetry(FilterType.SUCCINCT_COUNTING_BLOOM, keys, setting);
    }

    @Benchmark
    public Filter succinctCountingBloomRanked() {
        return constructRetry(FilterType.SUCCINCT_COUNTING_BLOOM_RANKED, keys, setting);
    }

    @Benchmark
    public Filter blockedBloom() {
        return constructRetry(FilterType.BLOCKED_BLOOM, keys, setting);
    }

    @Benchmark
    public Filter succinctCountingBlockedBloom() {
        return constructRetry(FilterType.SUCCINCT_COUNTING_BLOCKED_BLOOM, keys, setting);
    }

    @Benchmark
    public Filter succinctCountingBlockedBloomRanked() {
        return constructRetry(FilterType.SUCCINCT_COUNTING_BLOCKED_BLOOM_RANKED, keys, setting);
    }

    @Benchmark
    public Filter xorSimple() {
        return constructRetry(FilterType.XOR_SIMPLE, keys, setting);
    }

    @Benchmark
    public Filter xorSimple2() {
        return constructRetry(FilterType.XOR_SIMPLE_2, keys, setting);
    }

    @Benchmark
    public Filter xor8() {
        return constructRetry(FilterType.XOR_8, keys, setting);
    }

    @Benchmark
    public Filter xor16() {
        return constructRetry(FilterType.XOR_16, keys, setting);
    }

    @Benchmark
    public Filter xorPlus8() {
        return constructRetry(FilterType.XOR_PLUS_8, keys, setting);
    }

    @Benchmark
    public Filter cuckoo8() {
        return constructRetry(FilterType.CUCKOO_8, keys, setting);
    }

    @Benchmark
    public Filter cuckoo16() {
        return constructRetry(FilterType.CUCKOO_16, keys, setting);
    }

    @Benchmark
    public Filter cuckooPlus8() {
        return constructRetry(FilterType.CUCKOO_PLUS_8, keys, setting);
    }

    @Benchmark
    public Filter cuckooPlus16() {
        return constructRetry(FilterType.CUCKOO_PLUS_16, keys, setting);
    }

    @Benchmark
    public Filter gcs() {
        return constructRetry(FilterType.GCS, keys, setting);
    }
}
