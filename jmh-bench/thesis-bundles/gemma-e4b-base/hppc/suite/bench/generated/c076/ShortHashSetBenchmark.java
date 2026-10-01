package bench.generated.c076;

import com.carrotsearch.hppc.ShortHashSet;
import com.carrotsearch.hppc.ShortContainer;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.predicates.ShortPredicate;
import com.carrotsearch.hppc.procedures.ShortProcedure;
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
public class ShortHashSetBenchmark {

    private ShortHashSet set;
    private short[] testKeys;
    private ShortHashSet container; // FIX: Changed type from ShortContainer to ShortHashSet to allow calling add(short)
    private ShortPredicate predicate;
    private ShortProcedure procedure;

    private static final int INITIAL_CAPACITY = 1000;
    private static final int TEST_KEY_COUNT = 10000;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(42);
        testKeys = new short[TEST_KEY_COUNT];
        for (int i = 0; i < TEST_KEY_COUNT; i++) {
            // Generate random shorts, avoiding 0 as it's a special marker
            testKeys[i] = (short) (random.nextInt(32767) + 1);
        }

        // 1. Setup a large, populated set for read operations
        set = new ShortHashSet(INITIAL_CAPACITY);
        for (short key : testKeys) {
            set.add(key);
        }

        // 2. Setup a container for bulk operations
        container = new ShortHashSet();
        for (short key : testKeys) {
            container.add(key);
        }

        // 3. Setup predicates and procedures
        // Predicate: Check if key is even
        predicate = new ShortPredicate() {
            @Override
            public boolean apply(short key) {
                return (key % 2) == 0;
            }
        };

        // Procedure: Increment key by 1
        procedure = new ShortProcedure() {
            @Override
            public void apply(short key) {
                // Note: Since we are benchmarking traversal, the side effect is fine.
                // We don't need to return the modified value.
            }
        };
    }

    // --- Read Operations Benchmarks ---

    @Benchmark
    public void contains_Lookup(Blackhole bh) {
        // Test lookup on a large, populated set
        short key = testKeys[INITIAL_CAPACITY / 2];
        boolean result = set.contains(key);
        bh.consume(result);
    }

    @Benchmark
    public void indexOf_FindIndex(Blackhole bh) {
        // Test finding the index of a key
        short key = testKeys[INITIAL_CAPACITY / 2];
        int index = set.indexOf(key);
        bh.consume(index);
    }

    @Benchmark
    public void indexExists_CheckIndex(Blackhole bh) {
        // Test checking if a specific index exists (using a known valid index)
        int index = INITIAL_CAPACITY / 2;
        boolean exists = set.indexExists(index);
        bh.consume(exists);
    }

    @Benchmark
    public void indexGet_RetrieveValue(Blackhole bh) {
        // Test retrieving the value at a known index
        int index = INITIAL_CAPACITY / 2;
        short value = set.indexGet(index);
        bh.consume(value);
    }

    // --- Mutation Operations Benchmarks (Requires fresh state or careful handling) ---

    @Benchmark
    public void add_SingleInsertion(Blackhole bh) {
        // Use a fresh set instance for mutation tests to prevent state accumulation
        ShortHashSet localSet = new ShortHashSet(1);
        short key = testKeys[0];
        boolean added = localSet.add(key);
        bh.consume(added);
    }

    @Benchmark
    public void remove_SingleDeletion(Blackhole bh) {
        // Use a fresh set instance for mutation tests
        ShortHashSet localSet = new ShortHashSet(1);
        localSet.add(testKeys[0]);
        short key = testKeys[0];
        boolean removed = localSet.remove(key);
        bh.consume(removed);
    }

    @Benchmark
    public void addAll_BulkInsertion(Blackhole bh) {
        // Use a fresh set instance for mutation tests
        ShortHashSet localSet = new ShortHashSet(TEST_KEY_COUNT);
        int count = localSet.addAll(testKeys);
        bh.consume(count);
    }

    @Benchmark
    public void removeAll_BySingleKey(Blackhole bh) {
        // Use a fresh set instance for mutation tests
        ShortHashSet localSet = new ShortHashSet(TEST_KEY_COUNT);
        for (short key : testKeys) {
            localSet.add(key);
        }
        short keyToRemove = testKeys[0];
        int count = localSet.removeAll(keyToRemove);
        bh.consume(count);
    }

    @Benchmark
    public void removeAll_ByContainer(Blackhole bh) {
        // Use a fresh set instance for mutation tests
        ShortHashSet localSet = new ShortHashSet(TEST_KEY_COUNT);
        for (short key : testKeys) {
            localSet.add(key);
        }
        ShortHashSet otherSet = new ShortHashSet(TEST_KEY_COUNT);
        for (short key : testKeys) {
            otherSet.add(key);
        }
        int count = localSet.removeAll(otherSet);
        bh.consume(count);
    }

    @Benchmark
    public void removeAll_ByPredicate(Blackhole bh) {
        // Use a fresh set instance for mutation tests
        ShortHashSet localSet = new ShortHashSet(TEST_KEY_COUNT);
        for (short key : testKeys) {
            localSet.add(key);
        }
        // Use the pre-configured predicate (checks for even numbers)
        int count = localSet.removeAll(predicate);
        bh.consume(count);
    }

    // --- Iteration Benchmarks ---

    @Benchmark
    public void forEach_ProcedureTraversal(Blackhole bh) {
        // Use the large, populated set from setup
        set.forEach(procedure);
        bh.consume(true); // Consume result to prevent dead code elimination
    }

    @Benchmark
    public void forEach_PredicateTraversal(Blackhole bh) {
        // Use the large, populated set from setup
        set.forEach(predicate);
        bh.consume(true);
    }

    @Benchmark
    public void toArray_Conversion(Blackhole bh) {
        // Use the large, populated set from setup
        short[] array = set.toArray();
        bh.consume(array);
    }

    // --- Index Manipulation Benchmarks ---

    @Benchmark
    public void indexInsert_InsertNonExisting(Blackhole bh) {
        // Use a fresh set instance for mutation tests
        ShortHashSet localSet = new ShortHashSet(1);
        localSet.add(testKeys[0]); // Ensure index 0 exists
        
        // Find a non-existing index (e.g., one far outside the current bounds)
        int nonExistingIndex = 100000; 
        short newKey = testKeys[1];
        
        localSet.indexInsert(nonExistingIndex, newKey);
        bh.consume(true);
    }

    @Benchmark
    public void indexRemove_RemoveExisting(Blackhole bh) {
        // Use a fresh set instance for mutation tests
        ShortHashSet localSet = new ShortHashSet(1);
        localSet.add(testKeys[0]);
        
        // Find the index of the existing key
        int existingIndex = localSet.indexOf(testKeys[0]);
        
        localSet.indexRemove(existingIndex);
        bh.consume(true);
    }

    @Benchmark
    public void indexReplace_ReplaceKey(Blackhole bh) {
        // Use a fresh set instance for mutation tests
        ShortHashSet localSet = new ShortHashSet(1);
        short originalKey = testKeys[0];
        localSet.add(originalKey);
        
        // Find the index
        int existingIndex = localSet.indexOf(originalKey);
        short replacementKey = testKeys[1];
        
        short previousKey = localSet.indexReplace(existingIndex, replacementKey);
        bh.consume(previousKey);
    }
}
