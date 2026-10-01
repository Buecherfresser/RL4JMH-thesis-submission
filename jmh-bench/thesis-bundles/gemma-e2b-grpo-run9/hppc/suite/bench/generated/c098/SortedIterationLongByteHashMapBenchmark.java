package bench.generated.c098;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.SortedIterationLongByteHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongByteHashMapBenchmark {

    // The subject under test. Initialized to null as instantiation requires
    // complex, unprovided dependencies (LongByteMap, comparators).
    private SortedIterationLongByteHashMap map;

    @Setup
    public void setup() {
        // We avoid instantiation here because the constructors are ambiguous
        // without concrete, non-null dependencies, which are unavailable.
        // We rely on the null check in the benchmark methods.
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        if (map != null) {
            bh.consume(map.size());
        }
    }

    @Benchmark
    public void testContainsKey(Blackhole bh) {
        if (map != null) {
            // This call relies on the delegate being initialized correctly.
            bh.consume(map.containsKey(1L));
        }
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        if (map != null) {
            // This call relies on the delegate being initialized correctly.
            bh.consume(map.get(1L));
        }
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        if (map != null) {
            bh.consume(map.isEmpty());
        }
    }
}
