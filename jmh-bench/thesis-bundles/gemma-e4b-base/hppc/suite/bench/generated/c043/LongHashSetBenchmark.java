package bench.generated.c043;

import com.carrotsearch.hppc.LongHashSet;
import com.carrotsearch.hppc.LongContainer;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongHashSetBenchmark {

    private LongHashSet set;
    private long[] testKeys;
    private long keyToInsert;
    private long keyToRemove;
    private LongPredicate predicate;
    private LongProcedure procedure;

    private static final int INITIAL_CAPACITY = 1024;
    private static final int TEST_KEY_COUNT = 1000;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize a base set for read-only operations
        set = new LongHashSet(INITIAL_CAPACITY);

        // Generate test data
        Random random = new Random(42);
        testKeys = new long[TEST_KEY_COUNT];
        for (int i = 0; i < TEST_KEY_COUNT; i++) {
            testKeys[i] = random.nextLong();
        }

        // Setup specific keys for mutation tests
        keyToInsert = random.nextLong();
        keyToRemove = testKeys[0];

        // Setup predicates and procedures
        predicate = key -> key % 2 == 0; // Predicate: check for even numbers
        procedure = key -> {}; // Procedure: no-op
    }

    // --- Read-Only Benchmarks ---

    @Benchmark
    public void contains_Lookup(Blackhole bh) {
        // Ensure the set is populated for a meaningful lookup
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        localSet.addAll(testKeys);
        bh.consume(localSet.contains(keyToRemove));
    }

    @Benchmark
    public void size_Check(Blackhole bh) {
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        localSet.addAll(testKeys);
        bh.consume(localSet.size());
    }

    @Benchmark
    public void isEmpty_Check(Blackhole bh) {
        // Test on an empty set
        LongHashSet emptySet = new LongHashSet();
        bh.consume(emptySet.isEmpty());
    }

    @Benchmark
    public void toArray_Conversion(Blackhole bh) {
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        localSet.addAll(testKeys);
        bh.consume(localSet.toArray());
    }

    @Benchmark
    public void forEach_Procedure(Blackhole bh) {
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        localSet.addAll(testKeys);
        localSet.forEach(procedure);
        bh.consume(true);
    }

    @Benchmark
    public void forEach_Predicate_EarlyExit(Blackhole bh) {
        // Use a predicate that should stop early if possible
        LongPredicate earlyExitPredicate = key -> key % 2 != 0;
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        localSet.addAll(testKeys);
        localSet.forEach(earlyExitPredicate);
        bh.consume(true);
    }

    // --- Mutating Benchmarks ---

    @Benchmark
    public void add_SingleInsertion(Blackhole bh) {
        // Rebuild state for mutation test to prevent unbounded growth
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        localSet.addAll(testKeys);
        
        // Ensure the key is not present before adding
        long uniqueKey = Long.MAX_VALUE; 
        
        bh.consume(localSet.add(uniqueKey));
    }

    @Benchmark
    public void remove_SingleDeletion(Blackhole bh) {
        // Rebuild state for mutation test
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        localSet.addAll(testKeys);
        
        // keyToRemove is guaranteed to be in the set
        bh.consume(localSet.remove(keyToRemove));
    }

    @Benchmark
    public void addAll_BulkInsertion(Blackhole bh) {
        // Rebuild state for mutation test
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        
        // Use a subset of keys to add
        long[] subsetKeys = new long[TEST_KEY_COUNT / 2];
        for (int i = 0; i < subsetKeys.length; i++) {
            subsetKeys[i] = testKeys[i * 2];
        }
        
        bh.consume(localSet.addAll(subsetKeys));
    }

    @Benchmark
    public void removeAll_Predicate(Blackhole bh) {
        // Rebuild state for mutation test
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        localSet.addAll(testKeys);
        
        // Remove all even numbers
        bh.consume(localSet.removeAll(predicate));
    }

    @Benchmark
    public void clear_Reset(Blackhole bh) {
        // Rebuild state to ensure we are testing the clear operation on a populated set
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        localSet.addAll(testKeys);
        
        localSet.clear();
        bh.consume(localSet.isEmpty());
    }

    @Benchmark
    public void release_Cleanup(Blackhole bh) {
        // Rebuild state
        LongHashSet localSet = new LongHashSet(INITIAL_CAPACITY);
        localSet.addAll(testKeys);
        
        localSet.release();
        bh.consume(localSet.keys == null);
    }
}
