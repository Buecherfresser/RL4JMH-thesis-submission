package bench.generated.c043;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.LongHashSet;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongHashSetBenchmark {

    /** Shared state for all benchmarks. */
    @State(org.openjdk.jmh.annotations.Scope.Benchmark)
    public static class BenchmarkState {
        LongHashSet baseSet;
        long[] keys;      // pre‑generated distinct keys
        final long missingKey = Long.MAX_VALUE; // guaranteed not in baseSet

        @Setup
        public void setup() {
            int size = 1024;
            keys = new long[size];
            Random rnd = new Random(0);
            // generate non‑zero distinct keys
            for (int i = 0; i < size; i++) {
                long v;
                do {
                    v = rnd.nextLong();
                } while (v == 0L);
                keys[i] = v;
            }
            baseSet = new LongHashSet(size);
            for (long k : keys) {
                baseSet.add(k);
            }
        }
    }

    /** No‑op LongProcedure used by forEach benchmark. */
    private static class NoOpProcedure implements LongProcedure {
        @Override
        public void apply(long value) {
            // intentionally empty
        }
    }

    /** Predicate that always returns true (used by forEach predicate benchmark). */
    private static class AlwaysTruePredicate implements LongPredicate {
        @Override
        public boolean apply(long value) {
            return true;
        }
    }

    @Benchmark
    public boolean benchAddNewElement(BenchmarkState s) {
        LongHashSet set = new LongHashSet(s.baseSet);
        return set.add(s.missingKey);
    }

    @Benchmark
    public boolean benchRemoveExisting(BenchmarkState s) {
        LongHashSet set = new LongHashSet(s.baseSet);
        return set.remove(s.keys[0]);
    }

    @Benchmark
    public boolean benchContains(BenchmarkState s) {
        return s.baseSet.contains(s.keys[0]);
    }

    @Benchmark
    public int benchSize(BenchmarkState s) {
        return s.baseSet.size();
    }

    @Benchmark
    public long[] benchToArray(BenchmarkState s) {
        return s.baseSet.toArray();
    }

    @Benchmark
    public int benchHashCode(BenchmarkState s) {
        return s.baseSet.hashCode();
    }

    @Benchmark
    public boolean benchEqualsClone(BenchmarkState s) {
        LongHashSet clone = new LongHashSet(s.baseSet);
        return s.baseSet.equals(clone);
    }

    @Benchmark
    public int benchIterateViaCursor(BenchmarkState s) {
        int count = 0;
        for (LongCursor c : s.baseSet) {
            count += (c.value == 0L) ? 0 : 1;
        }
        return count;
    }

    @Benchmark
    public void benchForEachProcedure(BenchmarkState s, Blackhole bh) {
        s.baseSet.forEach(new NoOpProcedure());
        bh.consume(s.baseSet);
    }

    @Benchmark
    public void benchForEachPredicate(BenchmarkState s, Blackhole bh) {
        s.baseSet.forEach(new AlwaysTruePredicate());
        bh.consume(s.baseSet);
    }

    @Benchmark
    public int benchIndexOfExisting(BenchmarkState s) {
        return s.baseSet.indexOf(s.keys[0]);
    }

    @Benchmark
    public boolean benchIndexExists(BenchmarkState s) {
        int idx = s.baseSet.indexOf(s.keys[0]);
        return s.baseSet.indexExists(idx);
    }

    @Benchmark
    public long benchIndexGet(BenchmarkState s) {
        int idx = s.baseSet.indexOf(s.keys[0]);
        return s.baseSet.indexGet(idx);
    }

    @Benchmark
    public boolean benchIndexInsert(BenchmarkState s) {
        LongHashSet set = new LongHashSet(s.baseSet);
        int idx = set.indexOf(s.missingKey);
        set.indexInsert(idx, s.missingKey);
        return set.contains(s.missingKey);
    }

    @Benchmark
    public boolean benchIndexRemove(BenchmarkState s) {
        LongHashSet set = new LongHashSet(s.baseSet);
        int idx = set.indexOf(s.keys[0]);
        set.indexRemove(idx);
        return !set.contains(s.keys[0]);
    }
}
