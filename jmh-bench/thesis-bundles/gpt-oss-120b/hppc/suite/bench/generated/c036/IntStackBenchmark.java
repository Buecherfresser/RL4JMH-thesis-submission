package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntStack;
import com.carrotsearch.hppc.IntContainer;
import com.carrotsearch.hppc.cursors.IntCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntStackBenchmark {

    private static final int ELEMENT_COUNT = 1024;
    private static final int DISCARD_COUNT = 10;
    private static final int[] VARARG_ELEMENTS = new int[] {1, 2, 3, 4, 5};

    private IntStack baselineStack;
    private IntStack sourceContainer;
    private int[] sourceArray;

    @Setup(Level.Trial)
    public void setUp() {
        baselineStack = new IntStack(ELEMENT_COUNT);
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            baselineStack.push(i);
        }

        sourceContainer = new IntStack(ELEMENT_COUNT);
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            sourceContainer.push(i);
        }

        sourceArray = new int[ELEMENT_COUNT];
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            sourceArray[i] = i;
        }
    }

    @Benchmark
    public void benchmarkPushOne(Blackhole bh) {
        IntStack stack = new IntStack();
        stack.push(42);
        bh.consume(stack.size());
    }

    @Benchmark
    public void benchmarkPushTwo(Blackhole bh) {
        IntStack stack = new IntStack();
        stack.push(1, 2);
        bh.consume(stack.size());
    }

    @Benchmark
    public void benchmarkPushThree(Blackhole bh) {
        IntStack stack = new IntStack();
        stack.push(1, 2, 3);
        bh.consume(stack.size());
    }

    @Benchmark
    public void benchmarkPushFour(Blackhole bh) {
        IntStack stack = new IntStack();
        stack.push(1, 2, 3, 4);
        bh.consume(stack.size());
    }

    @Benchmark
    public void benchmarkPushVarargs(Blackhole bh) {
        IntStack stack = new IntStack();
        stack.push(VARARG_ELEMENTS);
        bh.consume(stack.size());
    }

    @Benchmark
    public void benchmarkPushArraySlice(Blackhole bh) {
        IntStack stack = new IntStack();
        stack.push(sourceArray, 0, sourceArray.length);
        bh.consume(stack.size());
    }

    @Benchmark
    public void benchmarkPushAllContainer(Blackhole bh) {
        IntStack stack = new IntStack();
        stack.pushAll((IntContainer) sourceContainer);
        bh.consume(stack.size());
    }

    @Benchmark
    public void benchmarkPushAllIterable(Blackhole bh) {
        IntStack stack = new IntStack();
        stack.pushAll((Iterable<? extends IntCursor>) sourceContainer);
        bh.consume(stack.size());
    }

    @Benchmark
    public int benchmarkPop() {
        IntStack stack = baselineStack.clone();
        return stack.pop();
    }

    @Benchmark
    public int benchmarkPeek() {
        IntStack stack = baselineStack.clone();
        return stack.peek();
    }

    @Benchmark
    public void benchmarkDiscardOne(Blackhole bh) {
        IntStack stack = baselineStack.clone();
        stack.discard();
        bh.consume(stack.size());
    }

    @Benchmark
    public void benchmarkDiscardCount(Blackhole bh) {
        IntStack stack = baselineStack.clone();
        stack.discard(DISCARD_COUNT);
        bh.consume(stack.size());
    }

    @Benchmark
    public IntStack benchmarkClone() {
        return baselineStack.clone();
    }

    @Benchmark
    public IntStack benchmarkFromStatic() {
        return IntStack.from(VARARG_ELEMENTS);
    }
}
