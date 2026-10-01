package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.xor.XorSimple;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorSimpleBenchmark {

    private long[] inputKeys;
    private XorSimple filterInstance;
    private long testKey;
    private static final int KEY_COUNT = 1000;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        Random random = new Random(42); // Fixed seed for reproducibility
        inputKeys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            inputKeys[i] = random.nextLong();
        }

        // 2. Pre-construct the filter instance for lookup benchmarks
        // Note: Construction involves internal retries, which is fine for setup
        // as we are measuring the cost of the operation itself, not the setup time.
        filterInstance = XorSimple.construct(inputKeys);

        // 3. Generate a test key for lookup
        testKey = random.nextLong();
    }

    /**
     * Benchmarks the construction of the XorSimple filter.
     * This measures the time taken for the entire process, including internal retries
     * until a valid filter is successfully created.
     */
    @Benchmark
    public XorSimple benchmarkConstruction() {
        // We must pass a copy of the keys if we were worried about side effects,
        // but since XorSimple.construct is static and takes a reference,
        // and the inputKeys array is immutable in this context, we reuse it.
        return XorSimple.construct(inputKeys);
    }

    /**
     * Benchmarks the lookup operation (mayContain) on a pre-constructed filter.
     */
    @Benchmark
    public void benchmarkLookup(Blackhole bh) {
        boolean result = filterInstance.mayContain(testKey);
        bh.consume(result);
    }
}
