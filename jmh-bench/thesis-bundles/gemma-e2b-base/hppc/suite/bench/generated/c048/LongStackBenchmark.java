package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.carrotsearch.hppc.LongStack;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.LongContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongStackBenchmark {

    private LongStack stack;
    private long[] pushData1;
    private long[] pushData2;
    private long[] pushData3;
    private long[] pushData4;
    private long[] discardData;
    private long[] pushAllData;
    private List<LongCursor> cursorList;

    @Setup
    public void setup() {
        // Initialize stack with a reasonable starting capacity
        stack = new LongStack(10000);

        // Setup fixed data payloads
        pushData1 = new long[100];
        Arrays.fill(pushData1, 1L);

        pushData2 = new long[100];
        Arrays.fill(pushData2, 2L);

        pushData3 = new long[100];
        Arrays.fill(pushData3, 3L);

        pushData4 = new long[100];
        Arrays.fill(pushData4, 4L);

        discardData = new long[5000];
        for (int i = 0; i < 5000; i++) {
            discardData[i] = (long) i;
        }

        // Setup data for pushAll simulation
        pushAllData = new long[5000];
        for (int i = 0; i < 5000; i++) {
            pushAllData[i] = (long) i;
        }

        // Pre-build iterable for pushAllFromIterable, assuming LongCursor has a no-arg constructor
        cursorList = new ArrayList<>();
        for (long val : pushAllData) {
            cursorList.add(new LongCursor());
        }
    }

    @Benchmark
    public void pushSingleElement(Blackhole bh) {
        stack.push(1000L);
        bh.consume(stack.peek());
    }

    @Benchmark
    public void pushTwoElements(Blackhole bh) {
        stack.push(1000L, 2000L);
        bh.consume(stack.peek());
    }

    @Benchmark
    public void pushThreeElements(Blackhole bh) {
        stack.push(1000L, 2000L, 3000L);
        bh.consume(stack.peek());
    }

    @Benchmark
    public void pushFourElements(Blackhole bh) {
        stack.push(1000L, 2000L, 3000L, 4000L);
        bh.consume(stack.peek());
    }

    @Benchmark
    public void pushArray(Blackhole bh) {
        // Test push(long[] elements, int start, int len)
        stack.push(pushData1, 0, pushData1.length);
        bh.consume(stack.peek());
    }

    @Benchmark
    public void pushVarargs(Blackhole bh) {
        // Test push(long... elements)
        stack.push(1L, 2L, 3L, 4L);
        bh.consume(stack.peek());
    }

    @Benchmark
    public void pop(Blackhole bh) {
        long popped = stack.pop();
        bh.consume(popped);
    }

    @Benchmark
    public void peek(Blackhole bh) {
        long peeked = stack.peek();
        bh.consume(peeked);
    }

    @Benchmark
    public void discardSingle(Blackhole bh) {
        stack.discard();
        bh.consume(stack.peek());
    }

    @Benchmark
    public void discardMultiple(Blackhole bh) {
        stack.discard(100);
        bh.consume(stack.peek());
    }

    @Benchmark
    public void pushAllFromIterable(Blackhole bh) {
        // Test pushAll(Iterable<? extends LongCursor>)
        int count = stack.pushAll(cursorList);
        bh.consume(count);
    }
}
