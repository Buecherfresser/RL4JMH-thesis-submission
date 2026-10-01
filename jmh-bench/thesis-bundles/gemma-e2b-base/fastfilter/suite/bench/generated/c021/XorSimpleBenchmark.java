package bench.generated.c021;

import org.fastfilter.Filter;
import org.fastfilter.xor.XorSimple;
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
public class XorSimpleBenchmark {

    private XorSimple filter;
    private long[] keys;
    private long lookupKey;

    @Setup
    public void setup() {
        // 1. Generate a large set of keys for filter construction
        int keyCount = 10000;
        keys = new long[keyCount];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < keyCount; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the XorSimple filter instance
        this.filter = XorSimple.construct(keys);

        // 3. Select a key for lookup
        this.lookupKey = keys[keyCount / 2];
    }

    @Benchmark
    public void mayContain_Positive(Blackhole bh) {
        boolean result = filter.mayContain(lookupKey);
        bh.consume(result);
    }

    @Benchmark
    public void mayContain_Negative(Blackhole bh) {
        // Use a key guaranteed not to be in the set (e.g., a random large number)
        long negativeKey = 9999999999999999L;
        boolean result = filter.mayContain(negativeKey);
        bh.consume(result);
    }

    @Benchmark
    public void getBitCount(Blackhole bh) {
        // Benchmarking a simple read operation
        long bitCount = filter.getBitCount();
        bh.consume(bitCount);
    }
}
