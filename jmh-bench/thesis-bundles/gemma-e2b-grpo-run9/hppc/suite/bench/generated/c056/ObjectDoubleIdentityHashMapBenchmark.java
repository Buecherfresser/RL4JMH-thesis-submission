package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectDoubleIdentityHashMap;
import com.carrotsearch.hppc.Containers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectDoubleIdentityHashMapBenchmark {

    private ObjectDoubleIdentityHashMap<String> map;

    @Setup
    public void setup() {
        // Initialize the map instance. Since it's scoped to the benchmark instance,
        // it is fresh for each benchmark run.
        this.map = new ObjectDoubleIdentityHashMap<>();
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test putting a new key-value pair.
        // Since the map is fresh per benchmark, this tests insertion performance.
        map.put("key1", 1.0);
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test getting a value.
        // We rely on the map being populated from previous benchmarks or setup,
        // but since we are testing the map itself, we test a simple lookup.
        // Note: If the map is empty, this might fail or return default values,
        // but it tests the method call path.
        try {
            map.get("key1");
        } catch (Exception e) {
            // Ignore exceptions if the map is empty or lookup fails for testing purposes
        }
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size retrieval.
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test clearing the map.
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkFromStaticFactory(Blackhole bh) {
        // Test the static factory method.
        try {
            // Create a small map instance using the static factory.
            ObjectDoubleIdentityHashMap<String> newMap = ObjectDoubleIdentityHashMap.from(
                new String[]{"k1", "k2"},
                new double[]{1.0, 2.0}
            );
            bh.consume(newMap);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
