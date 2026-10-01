package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import com.carrotsearch.hppc.IntHashSet;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.predicates.IntPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntHashSetBenchmark {

    private int elementCount;
    private IntHashSet filledSet;
    private int existingKey;
    private int missingKey;

    @Setup(Level.Trial)
    public void init() {
        elementCount = 1024;
        filledSet = new IntHashSet(elementCount * 2);
        for (int i = 1; i <= elementCount; i++) {
            filledSet.add(i);
        }
        existingKey = elementCount / 2;          // guaranteed present
        missingKey = elementCount + 1000;        // guaranteed absent
    }

    // -------------------------------------------------------------------------
    // Read‑only operations
    // -------------------------------------------------------------------------

    @Benchmark
    public boolean benchmarkContainsExisting() {
        return filledSet.contains(existingKey);
    }

    @Benchmark
    public boolean benchmarkContainsMissing() {
        return filledSet.contains(missingKey);
    }

    @Benchmark
    public int benchmarkSize() {
        return filledSet.size();
    }

    @Benchmark
    public int[] benchmarkToArray() {
        return filledSet.toArray();
    }

    @Benchmark
    public int benchmarkHashCode() {
        return filledSet.hashCode();
    }

    @Benchmark
    public boolean benchmarkEqualsSelf() {
        return filledSet.equals(filledSet);
    }

    @Benchmark
    public long benchmarkRamBytesAllocated() {
        return filledSet.ramBytesAllocated();
    }

    @Benchmark
    public long benchmarkRamBytesUsed() {
        return filledSet.ramBytesUsed();
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return filledSet.visualizeKeyDistribution(64);
    }

    @Benchmark
    public int benchmarkIndexOfExisting() {
        return filledSet.indexOf(existingKey);
    }

    @Benchmark
    public boolean benchmarkIndexExistsPositive() {
        int idx = filledSet.indexOf(existingKey);
        return filledSet.indexExists(idx);
    }

    @Benchmark
    public int benchmarkIndexGet() {
        int idx = filledSet.indexOf(existingKey);
        return filledSet.indexGet(idx);
    }

    @Benchmark
    public void benchmarkIterate(Blackhole bh) {
        Iterator<IntCursor> it = filledSet.iterator();
        while (it.hasNext()) {
            bh.consume(it.next().value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        filledSet.forEach(new IntProcedure() {
            @Override
            public void apply(int value) {
                bh.consume(value);
            }
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        filledSet.forEach(new IntPredicate() {
            @Override
            public boolean apply(int value) {
                bh.consume(value);
                return true;
            }
        });
    }

    // -------------------------------------------------------------------------
    // Mutating operations (operate on a fresh clone to avoid cross‑benchmark side effects)
    // -------------------------------------------------------------------------

    @Benchmark
    public boolean benchmarkAddNew() {
        IntHashSet set = new IntHashSet();
        return set.add(missingKey);
    }

    @Benchmark
    public boolean benchmarkAddExisting() {
        IntHashSet set = filledSet.clone();
        return set.add(existingKey);
    }

    @Benchmark
    public boolean benchmarkRemoveExisting() {
        IntHashSet set = filledSet.clone();
        return set.remove(existingKey);
    }

    @Benchmark
    public int benchmarkRemoveMissing() {
        IntHashSet set = filledSet.clone();
        return set.removeAll(missingKey);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        IntHashSet set = filledSet.clone();
        set.clear();
        bh.consume(set.size());
    }

    @Benchmark
    public IntHashSet benchmarkClone() {
        return filledSet.clone();
    }

    @Benchmark
    public IntHashSet benchmarkRelease() {
        IntHashSet clone = filledSet.clone();
        clone.release();
        return clone;
    }

    @Benchmark
    public void benchmarkEnsureCapacity(Blackhole bh) {
        IntHashSet set = new IntHashSet();
        set.ensureCapacity(elementCount * 4);
        bh.consume(set.size());
    }
}
