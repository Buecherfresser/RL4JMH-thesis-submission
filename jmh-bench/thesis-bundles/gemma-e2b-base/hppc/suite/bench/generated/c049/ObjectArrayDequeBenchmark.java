package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ObjectArrayDeque;
import com.carrotsearch.hppc.ObjectContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectArrayDequeBenchmark {

    private ObjectArrayDeque<Integer> deque;
    private List<Integer> initialElements;
    private Random random;
    private final int INITIAL_SIZE = 10000;
    private final int MAX_VALUE = 100000;

    @Setup
    public void setup() {
        random = new Random(42);
        initialElements = new ArrayList<>(INITIAL_SIZE);
        for (int i = 0; i < INITIAL_SIZE; i++) {
            initialElements.add(random.nextInt(MAX_VALUE));
        }
        
        // Initialize the deque with elements from the setup list
        deque = ObjectArrayDeque.from(initialElements.toArray(new Integer[0]));
    }

    @Benchmark
    public void addFirstSingle(Blackhole bh) {
        int value = random.nextInt(MAX_VALUE);
        deque.addFirst(value);
        bh.consume(value);
    }

    @Benchmark
    public void addLastSingle(Blackhole bh) {
        int value = random.nextInt(MAX_VALUE);
        deque.addLast(value);
        bh.consume(value);
    }

    @Benchmark
    public void removeFirstSingle(Blackhole bh) {
        Integer result = deque.removeFirst();
        bh.consume(result);
    }

    @Benchmark
    public void removeLastSingle(Blackhole bh) {
        Integer result = deque.removeLast();
        bh.consume(result);
    }

    @Benchmark
    public void getFirst(Blackhole bh) {
        Integer result = deque.getFirst();
        bh.consume(result);
    }

    @Benchmark
    public void getLast(Blackhole bh) {
        Integer result = deque.getLast();
        bh.consume(result);
    }

    @Benchmark
    public void contains(Blackhole bh) {
        int valueToCheck = random.nextInt(MAX_VALUE);
        boolean result = deque.contains(valueToCheck);
        bh.consume(result);
    }

    @Benchmark
    public void removeAllSingle(Blackhole bh) {
        int valueToRemove = random.nextInt(MAX_VALUE);
        int removedCount = deque.removeAll(valueToRemove);
        bh.consume(removedCount);
    }

    @Benchmark
    public void addFirstBulk(Blackhole bh) {
        int count = 100;
        for (int i = 0; i < count; i++) {
            deque.addFirst(random.nextInt(MAX_VALUE));
        }
        bh.consume(count);
    }

    @Benchmark
    public void addLastBulk(Blackhole bh) {
        int count = 100;
        for (int i = 0; i < count; i++) {
            deque.addLast(random.nextInt(MAX_VALUE));
        }
        bh.consume(count);
    }

    @Benchmark
    public void clone(Blackhole bh) {
        ObjectArrayDeque<Integer> cloned = deque.clone();
        bh.consume(cloned);
    }
}
