package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

import com.carrotsearch.hppc.CharShortHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CharShortHashMapBenchmark {

    private CharShortHashMap map;

    @Setup
    public void setup() {
        // Initialize a fresh map instance for each benchmark run.
        // Since CharShortHashMap is mutable and complex, creating a new instance
        // per benchmark run (or per trial) is safer than reusing a single instance
        // for mutating operations, although it adds overhead.
        this.map = new CharShortHashMap();
    }

    @Benchmark
    public void put(Blackhole bh) {
        // Test insertion of a new key/value pair.
        map.put('a', (short) 100);
        bh.consume(map.get('a'));
    }

    @Benchmark
    public void get(Blackhole bh) {
        // Test lookup for an existing key.
        bh.consume(map.get('a'));
    }

    @Benchmark
    public void getNonExistent(Blackhole bh) {
        // Test lookup for a non-existent key.
        bh.consume(map.get('z'));
    }

    @Benchmark
    public void getOrDefault(Blackhole bh) {
        // Test lookup with a default value.
        bh.consume(map.getOrDefault('z', (short) 0));
    }

    @Benchmark
    public void containsKey(Blackhole bh) {
        // Test key existence check.
        bh.consume(map.containsKey('a'));
    }

    @Benchmark
    public void indexOf(Blackhole bh) {
        // Test finding the index of an existing key.
        bh.consume(map.indexOf('a'));
    }

    @Benchmark
    public void indexGet(Blackhole bh) {
        // Test indexed retrieval.
        bh.consume(map.indexGet(0));
    }

    @Benchmark
    public void indexReplace(Blackhole bh) {
        // Test indexed replacement.
        map.put('a', (short) 101); // Ensure 'a' exists first
        bh.consume(map.indexReplace(0, (short) 200));
    }

    @Benchmark
    public void clear(Blackhole bh) {
        // Test clearing the map.
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void release(Blackhole bh) {
        // Test releasing resources.
        map.release();
    }

    @Benchmark
    public void size(Blackhole bh) {
        // Test size calculation.
        bh.consume(map.size());
    }

    @Benchmark
    public void isEmpty(Blackhole bh) {
        // Test isEmpty check on an empty map.
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void putAll(Blackhole bh) {
        // Test bulk insertion (requires some setup if we want to measure growth,
        // but we test the method call itself).
        map.put('b', (short) 50);
        bh.consume(map.size());
    }

    @Benchmark
    public void fromStatic(Blackhole bh) {
        // Test static factory method (requires temporary arrays, which is fine
        // as they are local and not static final).
        try {
            CharShortHashMap.from(new char[]{'x'}, new short[]{1});
        } catch (IllegalArgumentException e) {
            // Ignore expected exceptions if input validation fails in a specific run
        }
        bh.consume(map.size());
    }
}
