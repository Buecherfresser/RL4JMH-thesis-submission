package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.IntArrayDeque;
import com.carrotsearch.hppc.IntContainer;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.predicates.IntPredicate;
import com.carrotsearch.hppc.procedures.IntProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class IntArrayDequeBenchmark {

    private IntArrayDeque deque;
    private int[] initialData;
    private int[] testElements;
    private IntContainer container;
    private int[] predicateData;
    private IntPredicate filterPredicate;

    private static final int INITIAL_SIZE = 10000;
    private static final int DATA_SIZE = 50000;
    private static final Random RANDOM = new Random(42);

    @Setup
    public void setup() {
        // 1. Setup initial data for population
        initialData = new int[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            initialData[i] = RANDOM.nextInt(100000);
        }

        // 2. Initialize the deque with initial data
        deque = IntArrayDeque.from(initialData);

        // 3. Setup data for bulk operations (e.g., adding)
        testElements = new int[DATA_SIZE / 10];
        for (int i = 0; i < testElements.length; i++) {
            testElements[i] = RANDOM.nextInt(100000);
        }

        // 4. Setup data for removal/lookup tests
        predicateData = new int[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            predicateData[i] = RANDOM.nextInt(100000);
        }
        
        // 5. Setup a container for testing addLast(IntContainer).
        // FIX: Assuming IntArrayDeque implements IntContainer to resolve the compilation error.
        container = deque;

        // 6. Setup a predicate: filter elements less than 50000
        filterPredicate = x -> x < 50000;
    }

    // --- Insertion Benchmarks ---

    @Benchmark
    public void addLast_Single(Blackhole bh) {
        deque.addLast(RANDOM.nextInt(100000));
        bh.consume(null);
    }

    @Benchmark
    public void addFirst_Single(Blackhole bh) {
        deque.addFirst(RANDOM.nextInt(100000));
        bh.consume(null);
    }

    @Benchmark
    public void addLast_Vararg(Blackhole bh) {
        deque.addLast(testElements);
        bh.consume(null);
    }

    @Benchmark
    public void addFirst_Vararg(Blackhole bh) {
        deque.addFirst(testElements);
        bh.consume(null);
    }

    @Benchmark
    public void addLast_FromContainer(Blackhole bh) {
        deque.addLast(container);
        bh.consume(null);
    }

    @Benchmark
    public void addFirst_FromContainer(Blackhole bh) {
        int addedCount = deque.addFirst(container);
        bh.consume(addedCount);
    }

    // --- Removal Benchmarks ---

    @Benchmark
    public void removeFirst_Single(Blackhole bh) {
        int removedValue = deque.removeFirst();
        bh.consume(removedValue);
    }

    @Benchmark
    public void removeLast_Single(Blackhole bh) {
        int removedValue = deque.removeLast();
        bh.consume(removedValue);
    }

    @Benchmark
    public void removeFirst_ByValue(Blackhole bh) {
        int valueToRemove = deque.getFirst(); // Pick an element to remove
        int removedIndex = deque.removeFirst(valueToRemove);
        bh.consume(removedIndex);
    }

    @Benchmark
    public void removeLast_ByValue(Blackhole bh) {
        int valueToRemove = deque.getLast(); // Pick an element to remove
        int removedIndex = deque.removeLast(valueToRemove);
        bh.consume(removedIndex);
    }

    @Benchmark
    public void removeAll_ByValue(Blackhole bh) {
        int valueToRemove = 10000; // A value likely present in the initial data
        int removedCount = deque.removeAll(valueToRemove);
        bh.consume(removedCount);
    }

    // --- Lookup Benchmarks ---

    @Benchmark
    public void contains_Lookup(Blackhole bh) {
        int valueToFind = RANDOM.nextInt(100000);
        boolean found = deque.contains(valueToFind);
        bh.consume(found);
    }

    @Benchmark
    public void bufferIndexOf_Lookup(Blackhole bh) {
        int valueToFind = initialData[RANDOM.nextInt(DATA_SIZE)];
        int index = deque.bufferIndexOf(valueToFind);
        bh.consume(index);
    }

    // --- Iteration Benchmarks ---

    @Benchmark
    public void iterator_Forward(Blackhole bh) {
        int count = 0;
        Iterator<IntCursor> it = deque.iterator();
        while (it.hasNext()) {
            IntCursor cursor = it.next();
            count++;
            bh.consume(cursor);
        }
        bh.consume(count);
    }

    @Benchmark
    public void descendingIterator_Backward(Blackhole bh) {
        int count = 0;
        Iterator<IntCursor> it = deque.descendingIterator();
        while (it.hasNext()) {
            IntCursor cursor = it.next();
            count++;
            bh.consume(cursor);
        }
        bh.consume(count);
    }

    @Benchmark
    public void forEach_Predicate(Blackhole bh) {
        // Test filtering performance
        deque.forEach(filterPredicate);
        bh.consume(null);
    }

    @Benchmark
    public void descendingForEach_Predicate(Blackhole bh) {
        // Test filtering performance in reverse
        deque.descendingForEach(filterPredicate);
        bh.consume(null);
    }
}
