package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.DoubleStack;
import com.carrotsearch.hppc.cursors.DoubleCursor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleStackBenchmark {

    private DoubleStack stack;
    private double[] inputData;
    private DoubleStack sourceContainer;
    private Iterable<? extends DoubleCursor> sourceIterable;

    private static final int INITIAL_CAPACITY = 1000;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the stack
        stack = new DoubleStack(INITIAL_CAPACITY);

        // Initialize input data for batch operations
        inputData = new double[INITIAL_CAPACITY];
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            inputData[i] = i * 0.1;
        }

        // Initialize source container for pushAll(DoubleContainer)
        // DoubleStack extends DoubleArrayList, which implements DoubleContainer
        sourceContainer = new DoubleStack(INITIAL_CAPACITY);
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            sourceContainer.push(i * 2.0);
        }

        // Initialize source iterable for pushAll(Iterable)
        // FIX: DoubleCursor requires no arguments based on compilation errors
        List<DoubleCursor> cursorList = Arrays.asList(
                new DoubleCursor(), new DoubleCursor(), new DoubleCursor()
        );
        sourceIterable = cursorList;
    }

    // --- Push Operations ---

    @Benchmark
    public void push_single(Blackhole bh) {
        // Ensure stack is empty before measuring single push
        stack.clear();
        stack.push(1.0);
        bh.consume(stack.size());
    }

    @Benchmark
    public void push_double(Blackhole bh) {
        stack.clear();
        stack.push(1.0, 2.0);
        bh.consume(stack.size());
    }

    @Benchmark
    public void push_triple(Blackhole bh) {
        stack.clear();
        stack.push(1.0, 2.0, 3.0);
        bh.consume(stack.size());
    }

    @Benchmark
    public void push_quadruple(Blackhole bh) {
        stack.clear();
        stack.push(1.0, 2.0, 3.0, 4.0);
        bh.consume(stack.size());
    }

    @Benchmark
    public void push_batch(Blackhole bh) {
        stack.clear();
        // Push a representative batch size
        int len = 100;
        stack.push(inputData, 0, len);
        bh.consume(stack.size());
    }

    @Benchmark
    public void push_vararg(Blackhole bh) {
        stack.clear();
        // Push a representative vararg size
        double[] elements = new double[50];
        for (int i = 0; i < 50; i++) {
            elements[i] = i * 0.5;
        }
        stack.push(elements);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushAll_container(Blackhole bh) {
        // Ensure stack is empty before measuring pushAll
        stack.clear();
        int count = stack.pushAll(sourceContainer);
        bh.consume(count);
    }

    @Benchmark
    public void pushAll_iterable(Blackhole bh) {
        // Ensure stack is empty before measuring pushAll
        stack.clear();
        int count = stack.pushAll(sourceIterable);
        bh.consume(count);
    }

    // --- Discard Operations ---

    @Benchmark
    public void discard_single(Blackhole bh) {
        // Pre-fill stack to ensure discard is possible
        stack.clear();
        for (int i = 0; i < 100; i++) {
            stack.push(1.0);
        }
        stack.discard();
        bh.consume(stack.size());
    }

    @Benchmark
    public void discard_batch(Blackhole bh) {
        // Pre-fill stack
        stack.clear();
        for (int i = 0; i < 1000; i++) {
            stack.push(1.0);
        }
        // Discard a representative batch
        stack.discard(50);
        bh.consume(stack.size());
    }

    // --- Pop and Peek Operations ---

    @Benchmark
    public void pop(Blackhole bh) {
        // Pre-fill stack to ensure pop is possible
        stack.clear();
        for (int i = 0; i < 100; i++) {
            stack.push(1.0);
        }
        double result = stack.pop();
        bh.consume(result);
    }

    @Benchmark
    public void peek(Blackhole bh) {
        // Pre-fill stack to ensure peek is possible
        stack.clear();
        for (int i = 0; i < 100; i++) {
            stack.push(1.0);
        }
        double result = stack.peek();
        bh.consume(result);
    }
}
