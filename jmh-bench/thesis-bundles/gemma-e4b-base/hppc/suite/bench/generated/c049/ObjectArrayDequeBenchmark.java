package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectArrayDeque;
import com.carrotsearch.hppc.ObjectContainer;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.predicates.ObjectPredicate;
import com.carrotsearch.hppc.procedures.ObjectProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectArrayDequeBenchmark {

    private ObjectArrayDeque<String> deque;
    private ObjectArrayDeque<String> populatedDeque;
    private String testElement;
    private ObjectArrayDeque<String> testContainer;
    private ObjectPredicate<String> predicate;
    private ObjectProcedure<String> procedure;

    private static final int INITIAL_SIZE = 100;

    @Setup(Level.Trial)
    public void setup() {
        // Setup for operations requiring a fresh, empty deque
        deque = new ObjectArrayDeque<>();

        // Setup for operations requiring a populated deque
        populatedDeque = new ObjectArrayDeque<>(INITIAL_SIZE);
        testElement = "test_element";

        // Populate the container and deque
        testContainer = new ObjectArrayDeque<>(INITIAL_SIZE);
        for (int i = 0; i < INITIAL_SIZE; i++) {
            String element = "element_" + i;
            testContainer.addLast(element);
            populatedDeque.addLast(element);
        }

        // Setup predicates and procedures
        predicate = (ObjectPredicate<String>) (s) -> s.contains("element_");
        procedure = (ObjectProcedure<String>) (s) -> {};
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Reset the populated deque state for each invocation to prevent unbounded growth
        populatedDeque.clear();
        for (int i = 0; i < INITIAL_SIZE; i++) {
            populatedDeque.addLast("element_" + i);
        }
    }

    // --- Add Operations ---

    @Benchmark
    public void addLast_singleElement(Blackhole bh) {
        deque.addLast(testElement);
        bh.consume(deque.size());
    }

    @Benchmark
    public void addLast_varargs(Blackhole bh) {
        deque.addLast(testElement, "other_element");
        bh.consume(deque.size());
    }

    @Benchmark
    public void addLast_fromContainer(Blackhole bh) {
        deque.addLast(testContainer);
        bh.consume(deque.size());
    }

    @Benchmark
    public void addFirst_singleElement(Blackhole bh) {
        deque.addFirst(testElement);
        bh.consume(deque.size());
    }

    @Benchmark
    public void addFirst_varargs(Blackhole bh) {
        deque.addFirst(testElement, "other_element");
        bh.consume(deque.size());
    }

    @Benchmark
    public void addFirst_fromContainer(Blackhole bh) {
        deque.addFirst(testContainer);
        bh.consume(deque.size());
    }

    // --- Remove/Access Operations ---

    @Benchmark
    public void removeFirst(Blackhole bh) {
        String result = populatedDeque.removeFirst();
        bh.consume(result);
    }

    @Benchmark
    public void removeLast(Blackhole bh) {
        String result = populatedDeque.removeLast();
        bh.consume(result);
    }

    @Benchmark
    public void getFirst(Blackhole bh) {
        String result = populatedDeque.getFirst();
        bh.consume(result);
    }

    @Benchmark
    public void getLast(Blackhole bh) {
        String result = populatedDeque.getLast();
        bh.consume(result);
    }

    @Benchmark
    public void removeFirst_byElement(Blackhole bh) {
        int index = populatedDeque.removeFirst(testElement);
        bh.consume(index);
    }

    @Benchmark
    public void removeLast_byElement(Blackhole bh) {
        int index = populatedDeque.removeLast(testElement);
        bh.consume(index);
    }

    @Benchmark
    public void contains(Blackhole bh) {
        boolean result = populatedDeque.contains(testElement);
        bh.consume(result);
    }

    // --- Indexing/Search Operations ---

    @Benchmark
    public void bufferIndexOf(Blackhole bh) {
        int index = populatedDeque.bufferIndexOf(testElement);
        bh.consume(index);
    }

    @Benchmark
    public void lastBufferIndexOf(Blackhole bh) {
        int index = populatedDeque.lastBufferIndexOf(testElement);
        bh.consume(index);
    }

    // --- Modification Operations ---

    @Benchmark
    public void removeAll_byElement(Blackhole bh) {
        int removed = populatedDeque.removeAll(testElement);
        bh.consume(removed);
    }

    @Benchmark
    public void removeAll_byPredicate(Blackhole bh) {
        int removed = populatedDeque.removeAll(predicate);
        bh.consume(removed);
    }

    @Benchmark
    public void clear(Blackhole bh) {
        populatedDeque.clear();
        bh.consume(populatedDeque.size());
    }

    @Benchmark
    public void release(Blackhole bh) {
        populatedDeque.release();
        bh.consume(populatedDeque.buffer);
    }

    // --- Iteration Operations ---

    @Benchmark
    public void iterator_forward(Blackhole bh) {
        Iterator<ObjectCursor<String>> it = populatedDeque.iterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void iterator_descending(Blackhole bh) {
        Iterator<ObjectCursor<String>> it = populatedDeque.descendingIterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void forEach_forward(Blackhole bh) {
        populatedDeque.forEach(procedure);
        bh.consume(true);
    }

    @Benchmark
    public void descendingForEach_backward(Blackhole bh) {
        populatedDeque.descendingForEach(procedure);
        bh.consume(true);
    }

    @Benchmark
    public void forEach_predicate_forward(Blackhole bh) {
        populatedDeque.forEach(predicate);
        bh.consume(true);
    }

    @Benchmark
    public void descendingForEach_predicate_backward(Blackhole bh) {
        populatedDeque.descendingForEach(predicate);
        bh.consume(true);
    }

    // --- Utility Operations ---

    @Benchmark
    public void toArray(Blackhole bh) {
        String[] array = populatedDeque.toArray(new String[populatedDeque.size()]);
        bh.consume(array);
    }

    @Benchmark
    public void size(Blackhole bh) {
        int size = populatedDeque.size();
        bh.consume(size);
    }

    @Benchmark
    public void isEmpty(Blackhole bh) {
        boolean empty = populatedDeque.isEmpty();
        bh.consume(empty);
    }
}
