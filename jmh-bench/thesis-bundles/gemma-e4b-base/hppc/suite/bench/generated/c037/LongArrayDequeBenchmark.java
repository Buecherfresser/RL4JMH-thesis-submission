package bench.generated.c037;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.LongArrayDeque;
import com.carrotsearch.hppc.LongContainer;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;
import java.util.Iterator;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongArrayDequeBenchmark {

    private static final int INITIAL_CAPACITY = 100;
    private static final int ADDITION_COUNT = 10;

    // Pool of deques to ensure state isolation for mutating operations
    private LongArrayDeque[] dequePool;
    private int poolIndex = 0;

    // Input data for bulk operations
    private long[] inputElements;
    private LongContainer inputContainer;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize pool of deques
        dequePool = new LongArrayDeque[10];
        for (int i = 0; i < 10; i++) {
            dequePool[i] = new LongArrayDeque(INITIAL_CAPACITY);
        }

        // Initialize input data
        inputElements = new long[INITIAL_CAPACITY];
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            inputElements[i] = i * 2;
        }
        
        // Create a LongContainer equivalent for bulk operations
        inputContainer = LongArrayDeque.from(inputElements);
    }

    private LongArrayDeque getDeque() {
        LongArrayDeque deque = dequePool[poolIndex];
        poolIndex = (poolIndex + 1) % dequePool.length;
        // Ensure the deque is clean before use if it was mutated in the previous run
        deque.clear(); 
        return deque;
    }

    // --- Access and Utility Benchmarks ---

    @Benchmark
    public long testSize() {
        LongArrayDeque deque = getDeque();
        // Populate with some data
        for (int i = 0; i < 50; i++) {
            deque.addLast(i);
        }
        return deque.size();
    }

    @Benchmark
    public boolean testIsEmpty() {
        LongArrayDeque deque = getDeque();
        // Ensure it's empty
        deque.clear();
        return deque.isEmpty();
    }

    @Benchmark
    public long testGetFirst() {
        LongArrayDeque deque = getDeque();
        deque.addLast(100L);
        deque.addLast(200L);
        return deque.getFirst();
    }

    @Benchmark
    public long testGetLast() {
        LongArrayDeque deque = getDeque();
        deque.addLast(100L);
        deque.addLast(200L);
        return deque.getLast();
    }

    @Benchmark
    public boolean testContains() {
        LongArrayDeque deque = getDeque();
        deque.addLast(5L);
        deque.addLast(15L);
        return deque.contains(15L);
    }

    @Benchmark
    public int testBufferIndexOf() {
        LongArrayDeque deque = getDeque();
        deque.addLast(1L);
        deque.addLast(5L);
        deque.addLast(10L);
        return deque.bufferIndexOf(5L);
    }

    @Benchmark
    public int testLastBufferIndexOf() {
        LongArrayDeque deque = getDeque();
        deque.addLast(1L);
        deque.addLast(5L);
        deque.addLast(10L);
        return deque.lastBufferIndexOf(10L);
    }

    // --- Insertion Benchmarks ---

    @Benchmark
    public void testAddLastSingle(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        deque.addLast(1L);
        bh.consume(deque.size());
    }

    @Benchmark
    public void testAddFirstSingle(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        deque.addFirst(1L);
        bh.consume(deque.size());
    }

    @Benchmark
    public void testAddLastBulk(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        deque.addLast(inputContainer);
        bh.consume(deque.size());
    }

    @Benchmark
    public void testAddFirstBulk(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        deque.addFirst(inputContainer);
        bh.consume(deque.size());
    }

    // --- Removal Benchmarks ---

    @Benchmark
    public long testRemoveFirst() {
        LongArrayDeque deque = getDeque();
        deque.addLast(1L);
        deque.addLast(2L);
        return deque.removeFirst();
    }

    @Benchmark
    public long testRemoveLast() {
        LongArrayDeque deque = getDeque();
        deque.addLast(1L);
        deque.addLast(2L);
        return deque.removeLast();
    }

    @Benchmark
    public int testRemoveFirstByValue() {
        LongArrayDeque deque = getDeque();
        deque.addLast(1L);
        deque.addLast(5L);
        deque.addLast(10L);
        return deque.removeFirst(5L);
    }

    @Benchmark
    public int testRemoveLastByValue() {
        LongArrayDeque deque = getDeque();
        deque.addLast(1L);
        deque.addLast(5L);
        deque.addLast(10L);
        return deque.removeLast(5L);
    }

    @Benchmark
    public int testRemoveAllByValue() {
        LongArrayDeque deque = getDeque();
        deque.addLast(1L);
        deque.addLast(5L);
        deque.addLast(10L);
        deque.addLast(5L);
        return deque.removeAll(5L);
    }

    @Benchmark
    public int testRemoveAllByPredicate() {
        LongArrayDeque deque = getDeque();
        deque.addLast(1L);
        deque.addLast(5L);
        deque.addLast(10L);
        deque.addLast(5L);
        
        // Predicate: remove elements less than 7
        LongPredicate p = (l) -> l >= 7;
        return deque.removeAll(p);
    }

    // --- Iteration Benchmarks ---

    @Benchmark
    public void testIteratorForward(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        Iterator<LongCursor> it = deque.iterator();
        long sum = 0;
        while (it.hasNext()) {
            sum += it.next().value;
        }
        bh.consume(sum);
    }

    @Benchmark
    public void testIteratorDescending(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        Iterator<LongCursor> it = deque.descendingIterator();
        long sum = 0;
        while (it.hasNext()) {
            sum += it.next().value;
        }
        bh.consume(sum);
    }

    @Benchmark
    public void testForEachProcedure(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        
        LongProcedure p = (l) -> {}; // No-op procedure
        p.apply(0L); // Initialize procedure state
        deque.forEach(p);
        bh.consume(deque.size());
    }

    @Benchmark
    public void testDescendingForEachProcedure(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        
        LongProcedure p = (l) -> {}; // No-op procedure
        p.apply(0L); // Initialize procedure state
        deque.descendingForEach(p);
        bh.consume(deque.size());
    }

    @Benchmark
    public void testForEachPredicate(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        
        // Predicate: check if element is even
        LongPredicate p = (l) -> l % 2 == 0;
        p.apply(0L); // Initialize predicate state
        deque.forEach(p);
        bh.consume(deque.size());
    }

    @Benchmark
    public void testDescendingForEachPredicate(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        
        // Predicate: check if element is even
        LongPredicate p = (l) -> l % 2 == 0;
        p.apply(0L); // Initialize predicate state
        deque.descendingForEach(p);
        bh.consume(deque.size());
    }

    // --- State Management Benchmarks ---

    @Benchmark
    public void testClear(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        // Populate
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        deque.clear();
        // Verify state change
        bh.consume(deque.size());
    }

    @Benchmark
    public void testRelease(Blackhole bh) {
        LongArrayDeque deque = getDeque();
        // Populate
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        deque.release();
        // Verify state change
        bh.consume(deque.size());
    }

    @Benchmark
    public long[] testToArray() {
        LongArrayDeque deque = getDeque();
        // Populate
        for (int i = 0; i < 100; i++) {
            deque.addLast(i);
        }
        long[] target = new long[100];
        return deque.toArray(target);
    }
}
