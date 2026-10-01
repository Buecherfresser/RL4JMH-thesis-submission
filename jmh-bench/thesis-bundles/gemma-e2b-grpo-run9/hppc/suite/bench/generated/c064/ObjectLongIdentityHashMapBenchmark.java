package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ObjectLongIdentityHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectLongIdentityHashMapBenchmark {

    @Benchmark
    public void testStaticFrom(Blackhole bh) {
        try {
            // Test the static factory method. Inputs are created locally.
            ObjectLongIdentityHashMap<Object> map = ObjectLongIdentityHashMap.from(
                new Object[]{}, new long[]{});
            bh.consume(map);
        } catch (Exception e) {
            // Ignore exceptions for benchmark stability
        }
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test a simple put operation. Create a new map instance per invocation.
        ObjectLongIdentityHashMap<Object> map = new ObjectLongIdentityHashMap<>();
        try {
            map.put(new Object(), 123L);
            bh.consume(map);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test a simple get operation. Create a new map instance per invocation.
        ObjectLongIdentityHashMap<Object> map = new ObjectLongIdentityHashMap<>();
        try {
            // Attempt to get a key that definitely doesn't exist
            map.get(new Object());
            bh.consume(map);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test the size operation. Create a new map instance per invocation.
        ObjectLongIdentityHashMap<Object> map = new ObjectLongIdentityHashMap<>();
        try {
            map.size();
            bh.consume(map);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test the clear operation. Create a new map instance per invocation.
        ObjectLongIdentityHashMap<Object> map = new ObjectLongIdentityHashMap<>();
        try {
            map.clear();
            bh.consume(map);
        } catch (Exception e) {
            // Ignore
        }
    }
}
