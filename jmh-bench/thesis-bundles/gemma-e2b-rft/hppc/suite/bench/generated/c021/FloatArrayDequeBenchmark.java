package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.FloatArrayDeque;
import com.carrotsearch.hppc.FloatContainer;
import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.predicates.FloatPredicate;
import com.carrotsearch.hppc.procedures.FloatProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FloatArrayDequeBenchmark {

    private FloatArrayDeque deque;
    private float[] initialData;
    private int initialSize;
    private Random random;

    // Constants for setup
    private static final int INITIAL_SIZE = 10000;
    private static final int LARGE_SIZE = 50000;
    private static final float[] TEST_DATA_SMALL = new float[INITIAL_SIZE];
    private static final float[] TEST_DATA_LARGE = new float[LARGE_SIZE];

    @Setup
    public void setup() {
        random = new Random(42);
        
        // Setup initial data for the deque
        for (int i = 0; i < INITIAL_SIZE; i++) {
            TEST_DATA_SMALL[i] = random.nextFloat();
        }
        initialData = TEST_DATA_SMALL;
        initialSize = INITIAL_SIZE;

        // Initialize the deque with data
        deque = FloatArrayDeque.from(initialData);
    }

    // --- Insertion Benchmarks ---

    @Benchmark
    public void addFirst_Single(Blackhole bh) {
        deque.addFirst(random.nextFloat());
        bh.consume(null);
    }

    @Benchmark
    public void addFirst_Varargs(Blackhole bh) {
        float[] elements = new float[10];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = random.nextFloat();
        }
        deque.addFirst(elements);
        bh.consume(null);
    }

    @Benchmark
    public void addLast_Single(Blackhole bh) {
        deque.addLast(random.nextFloat());
        bh.consume(null);
    }

    @Benchmark
    public void addLast_Varargs(Blackhole bh) {
        float[] elements = new float[10];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = random.nextFloat();
        }
        deque.addLast(elements);
        bh.consume(null);
    }

    @Benchmark
    public void addLast_FromArray(Blackhole bh) {
        // Test adding elements from an array directly
        deque.addLast(TEST_DATA_SMALL);
        bh.consume(null);
    }

    // --- Access Benchmarks ---

    @Benchmark
    public float getFirst(Blackhole bh) {
        float result = deque.getFirst();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float getLast(Blackhole bh) {
        float result = deque.getLast();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean contains(Blackhole bh) {
        float valueToFind = TEST_DATA_SMALL[random.nextInt(INITIAL_SIZE)];
        boolean result = deque.contains(valueToFind);
        bh.consume(result);
        return result;
    }

    // --- Removal Benchmarks ---

    @Benchmark
    public float removeFirst(Blackhole bh) {
        float result = deque.removeFirst();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int removeFirst_ByValue(Blackhole bh) {
        float valueToRemove = TEST_DATA_SMALL[random.nextInt(INITIAL_SIZE)];
        int index = deque.removeFirst(valueToRemove);
        bh.consume(index);
        return index;
    }

    @Benchmark
    public float removeLast(Blackhole bh) {
        float result = deque.removeLast();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int removeLast_ByValue(Blackhole bh) {
        float valueToRemove = TEST_DATA_SMALL[random.nextInt(INITIAL_SIZE)];
        int index = deque.removeLast(valueToRemove);
        bh.consume(index);
        return index;
    }

    @Benchmark
    public int removeAll_ByValue(Blackhole bh) {
        float valueToRemove = TEST_DATA_SMALL[random.nextInt(INITIAL_SIZE)];
        int removedCount = deque.removeAll(valueToRemove);
        bh.consume(removedCount);
        return removedCount;
    }

    // --- Iteration Benchmarks ---

    @Benchmark
    public void iterator_FullTraversal(Blackhole bh) {
        java.util.Iterator<FloatCursor> it = deque.iterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void descendingForEach_AllElements(Blackhole bh) {
        FloatProcedure procedure = f -> { /* do nothing */ };
        deque.descendingForEach(procedure);
        bh.consume(null);
    }

    @Benchmark
    public void descendingForEach_PredicateFilter(Blackhole bh) {
        // Predicate: keep elements greater than 0.5
        FloatPredicate predicate = f -> f > 0.5f;
        deque.descendingForEach(predicate);
        bh.consume(null);
    }

    @Benchmark
    public void forEach_PredicateFilter(Blackhole bh) {
        // Predicate: keep elements greater than 0.5
        FloatPredicate predicate = f -> f > 0.5f;
        deque.forEach(predicate);
        bh.consume(null);
    }

    @Benchmark
    public void forEach_Procedure(Blackhole bh) {
        // Procedure: double the value (conceptually)
        FloatProcedure procedure = f -> { /* do nothing */ };
        deque.forEach(procedure);
        bh.consume(null);
    }

    @Benchmark
    public void descendingForEach_Procedure(Blackhole bh) {
        // Procedure: double the value (conceptually)
        FloatProcedure procedure = f -> { /* do nothing */ };
        deque.descendingForEach(procedure);
        bh.consume(null);
    }

    // --- Utility Benchmarks ---

    @Benchmark
    public int size(Blackhole bh) {
        int size = deque.size();
        bh.consume(size);
        return size;
    }

    @Benchmark
    public void clear(Blackhole bh) {
        deque.clear();
        bh.consume(null);
    }

    @Benchmark
    public void release(Blackhole bh) {
        deque.release();
        bh.consume(null);
    }

    @Benchmark
    public float[] toArray(Blackhole bh) {
        float[] result = deque.toArray(new float[deque.size()]);
        bh.consume(result);
        return result;
    }
}
