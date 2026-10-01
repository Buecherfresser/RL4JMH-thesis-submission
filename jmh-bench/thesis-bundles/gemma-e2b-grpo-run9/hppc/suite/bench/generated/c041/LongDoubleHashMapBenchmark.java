package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.LongDoubleHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongDoubleHashMapBenchmark {

    // Since LongDoubleHashMap is mutable and complex, we instantiate it inside
    // the benchmark methods to ensure isolation between runs, especially for mutating operations.

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Create a fresh instance for each benchmark run to avoid state pollution
        LongDoubleHashMap map = new LongDoubleHashMap();
        try {
            map.put(1L, 1.0);
            map.put(2L, 2.0);
            bh.consume(map);
        } catch (Exception e) {
            // Ignore exceptions during benchmarking if they occur due to internal state issues
        }
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Use a static factory for a read-only test to ensure a stable, non-mutating state
        LongDoubleHashMap map = LongDoubleHashMap.from(new long[]{1L, 2L}, new double[]{1.0, 2.0});
        try {
            double result = map.get(1L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        LongDoubleHashMap map = new LongDoubleHashMap();
        try {
            bh.consume(map.size());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        LongDoubleHashMap map = new LongDoubleHashMap();
        try {
            map.put(1L, 1.0);
            map.clear();
            bh.consume(map);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkStaticFactory(Blackhole bh) {
        try {
            LongDoubleHashMap.from(new long[]{1L, 2L}, new double[]{1.0, 2.0});
            bh.consume(null);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
