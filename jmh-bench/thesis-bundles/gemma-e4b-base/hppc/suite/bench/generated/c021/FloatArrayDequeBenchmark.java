package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.FloatArrayDeque;
import com.carrotsearch.hppc.FloatContainer;
import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.predicates.FloatPredicate;
import com.carrotsearch.hppc.FloatArrayList;
import com.carrotsearch.hppc.procedures.FloatProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatArrayDequeBenchmark {

    private FloatArrayDeque deque;
    private FloatContainer initialContainer;
    private float testValue;
    private float otherValue;
    private FloatPredicate predicate;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize a deque with a fixed set of elements for consistent testing
        int initialSize = 100;
        float[] initialData = new float[initialSize];
        for (int i = 0; i < initialSize; i++) {
            initialData[i] = (float) i * 0.1f;
        }
        
        // Create the initial container
        initialContainer = FloatArrayList.from(initialData);
        
        // Create the deque instance
        deque = new FloatArrayDeque(initialSize);
        
        // Populate the deque using the container
        deque.addLast(initialContainer);

        // Setup test values
        testValue = 5.5f;
        otherValue = 99.9f;
        
        // Setup a predicate: remove all values greater than 5.0f
        predicate = new FloatPredicate() {
            @Override
            public boolean apply(float value) {
                return value > 5.0f;
            }
        };
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        // Reset the deque state before each iteration to ensure independence
        deque.clear();
        // Repopulate with the initial data
        deque.addLast(initialContainer);
    }

    @Benchmark
    public void addLastSingleElement(Blackhole bh) {
        deque.addLast(testValue);
        bh.consume(deque.size());
    }

    @Benchmark
    public void addFirstSingleElement(Blackhole bh) {
        deque.addFirst(testValue);
        bh.consume(deque.size());
    }

    @Benchmark
    public void removeFirst(Blackhole bh) {
        float result = deque.removeFirst();
        bh.consume(result);
        bh.consume(deque.size());
    }

    @Benchmark
    public void removeLast(Blackhole bh) {
        float result = deque.removeLast();
        bh.consume(result);
        bh.consume(deque.size());
    }

    @Benchmark
    public void getFirst(Blackhole bh) {
        float result = deque.getFirst();
        bh.consume(result);
    }

    @Benchmark
    public void getLast(Blackhole bh) {
        float result = deque.getLast();
        bh.consume(result);
    }

    @Benchmark
    public void containsElement(Blackhole bh) {
        boolean result = deque.contains(testValue);
        bh.consume(result);
    }

    @Benchmark
    public void bufferIndexOfElement(Blackhole bh) {
        int index = deque.bufferIndexOf(testValue);
        bh.consume(index);
    }

    @Benchmark
    public void lastBufferIndexOfElement(Blackhole bh) {
        int index = deque.lastBufferIndexOf(testValue);
        bh.consume(index);
    }

    @Benchmark
    public void removeAllByValue(Blackhole bh) {
        int removed = deque.removeAll(testValue);
        bh.consume(removed);
        bh.consume(deque.size());
    }

    @Benchmark
    public void removeAllByPredicate(Blackhole bh) {
        int removed = deque.removeAll(predicate);
        bh.consume(removed);
        bh.consume(deque.size());
    }

    @Benchmark
    public void toArrayCopy(Blackhole bh) {
        float[] target = new float[deque.size()];
        deque.toArray(target);
        bh.consume(target);
    }

    @Benchmark
    public void clearDeque(Blackhole bh) {
        deque.clear();
        bh.consume(deque.size());
    }

    @Benchmark
    public void releaseDeque(Blackhole bh) {
        deque.release();
        bh.consume(deque.size());
    }

    @Benchmark
    public void ensureCapacity(Blackhole bh) {
        // Ensure capacity for 200 elements
        deque.ensureCapacity(200);
        bh.consume(deque.size());
    }
}
