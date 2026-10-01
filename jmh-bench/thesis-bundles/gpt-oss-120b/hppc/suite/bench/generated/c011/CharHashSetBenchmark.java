package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.CharHashSet;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.procedures.CharProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharHashSetBenchmark {

    /* --------------------------------------------------------------------- */
    /* Shared state for read‑only benchmarks                                 */
    /* --------------------------------------------------------------------- */
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        CharHashSet set;
        char existingKey;
        char missingKey;
        CharHashSet otherSet;

        @Setup(Level.Trial)
        public void setup() {
            int size = 1024;
            set = new CharHashSet(size);
            for (int i = 0; i < size; i++) {
                // avoid the special empty key (0)
                set.add((char) (i + 1));
            }
            existingKey = (char) 500;
            missingKey = (char) 2000; // outside the filled range
            otherSet = new CharHashSet(set);
        }
    }

    /* --------------------------------------------------------------------- */
    /* State for add‑new benchmark (fresh set per invocation)               */
    /* --------------------------------------------------------------------- */
    @State(Scope.Benchmark)
    public static class AddState {
        CharHashSet base;
        char newKey;
        CharHashSet fresh;

        @Setup(Level.Trial)
        public void init() {
            int size = 1024;
            base = new CharHashSet(size);
            for (int i = 0; i < size; i++) {
                base.add((char) (i + 1));
            }
            newKey = (char) 2000;
        }

        @Setup(Level.Invocation)
        public void perInvocation() {
            fresh = base.clone();
        }
    }

    /* --------------------------------------------------------------------- */
    /* State for remove‑existing benchmark (fresh set per invocation)        */
    /* --------------------------------------------------------------------- */
    @State(Scope.Benchmark)
    public static class RemoveState {
        CharHashSet base;
        char existingKey;
        CharHashSet fresh;

        @Setup(Level.Trial)
        public void init() {
            int size = 1024;
            base = new CharHashSet(size);
            for (int i = 0; i < size; i++) {
                base.add((char) (i + 1));
            }
            existingKey = (char) 500;
        }

        @Setup(Level.Invocation)
        public void perInvocation() {
            fresh = base.clone();
        }
    }

    /* --------------------------------------------------------------------- */
    /* Helper procedure for forEach benchmark                               */
    /* --------------------------------------------------------------------- */
    public static class SumProcedure implements CharProcedure {
        long sum = 0L;

        @Override
        public void apply(char value) {
            sum += value;
        }
    }

    /* --------------------------------------------------------------------- */
    /* Benchmarks                                                            */
    /* --------------------------------------------------------------------- */

    @Benchmark
    public boolean containsExisting(BenchmarkState s) {
        return s.set.contains(s.existingKey);
    }

    @Benchmark
    public boolean containsMissing(BenchmarkState s) {
        return s.set.contains(s.missingKey);
    }

    @Benchmark
    public boolean addNew(AddState s) {
        return s.fresh.add(s.newKey);
    }

    @Benchmark
    public boolean removeExisting(RemoveState s) {
        return s.fresh.remove(s.existingKey);
    }

    @Benchmark
    public long iteratorSum(BenchmarkState s) {
        long sum = 0L;
        for (CharCursor c : s.set) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public long forEachProcedure(BenchmarkState s) {
        SumProcedure proc = new SumProcedure();
        s.set.forEach(proc);
        return proc.sum;
    }

    @Benchmark
    public int toArrayLength(BenchmarkState s) {
        return s.set.toArray().length;
    }

    @Benchmark
    public int indexOfExisting(BenchmarkState s) {
        return s.set.indexOf(s.existingKey);
    }

    @Benchmark
    public int indexOfMissing(BenchmarkState s) {
        return s.set.indexOf(s.missingKey);
    }

    @Benchmark
    public int hashCode(BenchmarkState s) {
        return s.set.hashCode();
    }

    @Benchmark
    public boolean equalsSame(BenchmarkState s) {
        return s.set.equals(s.otherSet);
    }
}
