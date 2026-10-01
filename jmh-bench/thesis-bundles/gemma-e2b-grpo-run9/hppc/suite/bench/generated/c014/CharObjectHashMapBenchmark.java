package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

import com.carrotsearch.hppc.CharObjectHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CharObjectHashMapBenchmark {

    // Since CharObjectHashMap is mutable and stateful, we create a new instance
    // inside each benchmark method to ensure isolation and avoid mutation side effects.

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Create a fresh map instance for each run
        CharObjectHashMap<Integer> map = new CharObjectHashMap<>();
        
        // Perform a put operation
        map.put('a', 100);
        
        // Consume the result (void method)
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        CharObjectHashMap<Integer> map = new CharObjectHashMap<>();
        map.put('a', 100);
        
        // Test successful get
        bh.consume(map.get('a'));
        
        // Test failed get (should return null)
        bh.consume(map.get('z'));
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        CharObjectHashMap<Integer> map = new CharObjectHashMap<>();
        map.put('a', 100);
        
        // Test true case
        bh.consume(map.containsKey('a'));
        
        // Test false case
        bh.consume(map.containsKey('b'));
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        CharObjectHashMap<Integer> map = new CharObjectHashMap<>();
        map.put('a', 100);
        
        // Remove existing key
        bh.consume(map.remove('a'));
        
        // Attempt to remove non-existent key (should return null)
        bh.consume(map.remove('b'));
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        CharObjectHashMap<Integer> map = new CharObjectHashMap<>();
        map.put('a', 100);
        map.put('b', 200);
        
        map.clear();
        
        // Check if map is empty (size should be 0)
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        CharObjectHashMap<Integer> map = new CharObjectHashMap<>();
        map.put('a', 1);
        map.put('b', 2);
        
        bh.consume(map.size());
    }
}
