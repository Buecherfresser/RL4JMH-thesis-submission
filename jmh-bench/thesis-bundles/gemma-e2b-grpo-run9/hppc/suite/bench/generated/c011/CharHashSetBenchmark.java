package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.CharHashSet;
import com.carrotsearch.hppc.CharContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharHashSetBenchmark {

    // Since CharHashSet is mutable and we want to test its methods in isolation,
    // we instantiate it inside the benchmark method to ensure a clean state for each run.

    @Benchmark
    public void testAdd(Blackhole bh) {
        // Test adding a character. We don't care about the return value, just the operation.
        CharHashSet set = new CharHashSet();
        set.add('a');
        bh.consume(set);
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        // Test contains on an empty set (read-only operation)
        CharHashSet set = new CharHashSet();
        boolean result = set.contains('a');
        bh.consume(result);

        // Test contains on a populated set
        CharHashSet populatedSet = new CharHashSet();
        populatedSet.add('b');
        boolean result2 = populatedSet.contains('b');
        bh.consume(result2);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size on an empty set
        CharHashSet set = new CharHashSet();
        bh.consume(set.size());

        // Test size on a set with one element
        CharHashSet populatedSet = new CharHashSet();
        populatedSet.add('x');
        bh.consume(populatedSet.size());
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        // Test remove on a set that doesn't contain the key (should return false)
        CharHashSet set = new CharHashSet();
        bh.consume(set.remove('z'));

        // Test remove on a set that contains the key (should return true)
        CharHashSet populatedSet = new CharHashSet();
        populatedSet.add('a');
        bh.consume(populatedSet.remove('a'));
    }

    @Benchmark
    public void testRemoveAll(Blackhole bh) {
        // Test removeAll on an empty set
        CharHashSet set = new CharHashSet();
        bh.consume(set.removeAll('a'));

        // Test removeAll on a set that contains the key (should remove one element)
        CharHashSet populatedSet = new CharHashSet();
        populatedSet.add('a');
        bh.consume(populatedSet.removeAll('a'));
    }

    @Benchmark
    public void testFromArray(Blackhole bh) {
        // Test static factory method
        CharHashSet set = CharHashSet.from('a', 'b', 'c');
        bh.consume(set);
    }
}
