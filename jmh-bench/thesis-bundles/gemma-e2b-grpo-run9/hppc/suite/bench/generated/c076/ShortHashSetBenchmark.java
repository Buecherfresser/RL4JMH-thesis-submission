package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortHashSet;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortHashSetBenchmark {

    // State field is not strictly necessary if we instantiate inside the benchmark,
    // but kept for structure compliance if we were to benchmark methods on a persistent object.
    // Since ShortHashSet is mutable, we instantiate fresh objects in each benchmark
    // to ensure isolation and correctness, avoiding complex state reset logic.

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        // Create a fresh set for each invocation to ensure isolation
        ShortHashSet set = new ShortHashSet();
        set.add((short) 10);
        bh.consume(set);
    }

    @Benchmark
    public void benchmarkContains(Blackhole bh) {
        // Create a fresh set for each invocation
        ShortHashSet set = new ShortHashSet();
        set.add((short) 10);
        bh.consume(set.contains((short) 10));
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Create a fresh set for each invocation
        ShortHashSet set = new ShortHashSet();
        set.add((short) 10);
        bh.consume(set.remove((short) 10));
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Create a fresh set for each invocation
        ShortHashSet set = new ShortHashSet();
        bh.consume(set.size());
    }

    @Benchmark
    public void benchmarkFromArray(Blackhole bh) {
        // Test static factory method
        ShortHashSet set = ShortHashSet.from((short) 1, (short) 2);
        bh.consume(set);
    }

    @Benchmark
    public void benchmarkAddAll(Blackhole bh) {
        // Test addAll with varargs
        ShortHashSet set = new ShortHashSet();
        int added = set.addAll((short) 1);
        bh.consume(added);
    }

    @Benchmark
    public void benchmarkContainsNonExistent(Blackhole bh) {
        // Test contains on an empty set
        ShortHashSet set = new ShortHashSet();
        bh.consume(set.contains((short) 99));
    }
}
