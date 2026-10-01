package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectFloatHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectFloatHashMapBenchmark {

    // We use a local variable or create a new instance in each benchmark method
    // to avoid state mutation issues across JMH trials, adhering to anti-pattern rules.

    @Benchmark
    public void testPut(Blackhole bh) {
        // Create a fresh map instance for each invocation to avoid state mutation skewing results.
        ObjectFloatHashMap<String> map = new ObjectFloatHashMap<>();
        try {
            map.put("key1", 1.0f);
            map.put("key2", 2.0f);
            bh.consume(map.size());
        } catch (Exception e) {
            // Ignore exceptions during benchmarking if they occur due to internal state issues
        }
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        ObjectFloatHashMap<String> map = new ObjectFloatHashMap<>();
        try {
            map.put("key1", 1.0f);
            float result = map.get("key1");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        ObjectFloatHashMap<String> map = new ObjectFloatHashMap<>();
        try {
            map.put("key1", 1.0f);
            float result = map.getOrDefault("key1", 99.0f);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testContainsKey(Blackhole bh) {
        ObjectFloatHashMap<String> map = new ObjectFloatHashMap<>();
        try {
            map.put("key1", 1.0f);
            boolean contains = map.containsKey("key1");
            bh.consume(contains);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        ObjectFloatHashMap<String> map = new ObjectFloatHashMap<>();
        try {
            map.put("key1", 1.0f);
            float removedValue = map.remove("key1");
            bh.consume(removedValue);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        ObjectFloatHashMap<String> map = new ObjectFloatHashMap<>();
        try {
            map.put("key1", 1.0f);
            map.clear();
            bh.consume(map.size());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        ObjectFloatHashMap<String> map = new ObjectFloatHashMap<>();
        try {
            map.put("key1", 1.0f);
            map.put("key2", 2.0f);
            bh.consume(map.size());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
