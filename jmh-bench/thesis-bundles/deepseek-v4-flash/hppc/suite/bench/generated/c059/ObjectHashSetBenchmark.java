package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectHashSet;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.carrotsearch.hppc.predicates.ObjectPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectHashSetBenchmark {

    private static final int POOL_SIZE = 1024;
    private static final int SET_SIZE = 1000;

    // Pools for mutating benchmarks
    private ObjectHashSet<String>[] addPool;
    private ObjectHashSet<String>[] containsPool;
    private ObjectHashSet<String>[] removePool;
    private ObjectHashSet<String>[] addAllPool;
    private ObjectHashSet<String>[] clearPool;
    private ObjectHashSet<String>[] indexOfPool;
    private ObjectHashSet<String>[] forEachPool;
    private ObjectHashSet<String>[] toArrayPool;
    private ObjectHashSet<String>[] clonePool;
    private ObjectHashSet<String>[] equalsPool;
    private ObjectHashSet<String>[] hashCodePool;
    private ObjectHashSet<String>[] isEmptyPool;
    private ObjectHashSet<String>[] sizePool;
    private ObjectHashSet<String>[] iteratorPool;

    // Indices for cycling through pools
    private int addIndex;
    private int containsIndex;
    private int removeIndex;
    private int addAllIndex;
    private int clearIndex;
    private int indexOfIndex;
    private int forEachIndex;
    private int toArrayIndex;
    private int cloneIndex;
    private int equalsIndex;
    private int hashCodeIndex;
    private int isEmptyIndex;
    private int sizeIndex;
    private int iteratorIndex;

    // Read-only set for non-mutating benchmarks
    private ObjectHashSet<String> readOnlySet;

    @SuppressWarnings("unchecked")
    @Setup
    public void setup() {
        addPool = createPool();
        containsPool = createPool();
        removePool = createPool();
        addAllPool = createPool();
        clearPool = createPool();
        indexOfPool = createPool();
        forEachPool = createPool();
        toArrayPool = createPool();
        clonePool = createPool();
        equalsPool = createPool();
        hashCodePool = createPool();
        isEmptyPool = createPool();
        sizePool = createPool();
        iteratorPool = createPool();

        // Create a read-only set for non-mutating benchmarks
        readOnlySet = createSet(SET_SIZE);
    }

    private ObjectHashSet<String>[] createPool() {
        ObjectHashSet<String>[] pool = new ObjectHashSet[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            pool[i] = createSet(SET_SIZE);
        }
        return pool;
    }

    private ObjectHashSet<String> createSet(int size) {
        ObjectHashSet<String> set = new ObjectHashSet<>(size);
        for (int i = 0; i < size; i++) {
            set.add("key" + i);
        }
        return set;
    }

    // Helper to get next set from a pool
    private ObjectHashSet<String> nextSet(ObjectHashSet<String>[] pool, java.util.concurrent.atomic.AtomicInteger index) {
        return pool[index.getAndIncrement() & (POOL_SIZE - 1)];
    }

    // Benchmark methods

    @Benchmark
    public boolean add() {
        ObjectHashSet<String> set = addPool[addIndex++ & (POOL_SIZE - 1)];
        return set.add("newKey" + addIndex);
    }

    @Benchmark
    public boolean contains() {
        ObjectHashSet<String> set = containsPool[containsIndex++ & (POOL_SIZE - 1)];
        return set.contains("key0");
    }

    @Benchmark
    public boolean remove() {
        ObjectHashSet<String> set = removePool[removeIndex++ & (POOL_SIZE - 1)];
        return set.remove("key0");
    }

    @Benchmark
    public int addAll() {
        ObjectHashSet<String> set = addAllPool[addAllIndex++ & (POOL_SIZE - 1)];
        return set.addAll("newKey1", "newKey2", "newKey3");
    }

    @Benchmark
    public void clear() {
        ObjectHashSet<String> set = clearPool[clearIndex++ & (POOL_SIZE - 1)];
        set.clear();
    }

    @Benchmark
    public int indexOf() {
        ObjectHashSet<String> set = indexOfPool[indexOfIndex++ & (POOL_SIZE - 1)];
        return set.indexOf("key0");
    }

    @Benchmark
    public void forEach(Blackhole bh) {
        ObjectHashSet<String> set = forEachPool[forEachIndex++ & (POOL_SIZE - 1)];
        set.forEach((ObjectProcedure<String>) bh::consume);
    }

    @Benchmark
    public Object[] toArray() {
        ObjectHashSet<String> set = toArrayPool[toArrayIndex++ & (POOL_SIZE - 1)];
        return set.toArray();
    }

    @Benchmark
    public ObjectHashSet<String> clone() {
        ObjectHashSet<String> set = clonePool[cloneIndex++ & (POOL_SIZE - 1)];
        return set.clone();
    }

    @Benchmark
    public boolean equals() {
        ObjectHashSet<String> set = equalsPool[equalsIndex++ & (POOL_SIZE - 1)];
        return set.equals(readOnlySet);
    }

    @Benchmark
    public int hashCode() {
        ObjectHashSet<String> set = hashCodePool[hashCodeIndex++ & (POOL_SIZE - 1)];
        return set.hashCode();
    }

    @Benchmark
    public boolean isEmpty() {
        ObjectHashSet<String> set = isEmptyPool[isEmptyIndex++ & (POOL_SIZE - 1)];
        return set.isEmpty();
    }

    @Benchmark
    public int size() {
        ObjectHashSet<String> set = sizePool[sizeIndex++ & (POOL_SIZE - 1)];
        return set.size();
    }

    @Benchmark
    public void iterator(Blackhole bh) {
        ObjectHashSet<String> set = iteratorPool[iteratorIndex++ & (POOL_SIZE - 1)];
        for (ObjectCursor<String> cursor : set) {
            bh.consume(cursor.value);
        }
    }
}
