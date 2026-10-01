package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.LongStack;
import com.carrotsearch.hppc.LongArrayList;
import com.carrotsearch.hppc.cursors.LongCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongStackBenchmark {

    private int poolSize;
    private int elementCount;

    private LongStack[] emptyStacks;
    private int emptyIdx;

    private LongStack[] filledStacks;
    private int filledIdx;

    private LongArrayList sourceContainer;
    private long[] varargElements;
    private long[] rangeElements;

    @Setup
    public void setup() {
        poolSize = 64;
        elementCount = 1024;

        emptyStacks = new LongStack[poolSize];
        emptyIdx = 0;

        filledStacks = new LongStack[poolSize];
        filledIdx = 0;

        // Prepare empty stacks with enough capacity to avoid resizing.
        for (int i = 0; i < poolSize; i++) {
            emptyStacks[i] = new LongStack(elementCount * 2);
        }

        // Prepare filled stacks for pop/peek/discard/clone benchmarks.
        for (int i = 0; i < poolSize; i++) {
            LongStack s = new LongStack(elementCount * 2);
            for (int j = 0; j < elementCount; j++) {
                s.push(j);
            }
            filledStacks[i] = s;
        }

        // Prepare a source container with elementCount elements.
        sourceContainer = new LongArrayList(elementCount);
        for (int i = 0; i < elementCount; i++) {
            sourceContainer.add(i);
        }

        // Prepare vararg elements.
        varargElements = new long[] {1L, 2L, 3L, 4L};

        // Prepare a range array used by push(array, start, len).
        rangeElements = new long[100];
        for (int i = 0; i < rangeElements.length; i++) {
            rangeElements[i] = i;
        }
    }

    @Benchmark
    public int pushOne() {
        LongStack s = emptyStacks[emptyIdx];
        emptyIdx = (emptyIdx + 1) & (poolSize - 1);
        s.push(0xCAFEBABECAFEL);
        return s.size();
    }

    @Benchmark
    public int pushTwo() {
        LongStack s = emptyStacks[emptyIdx];
        emptyIdx = (emptyIdx + 1) & (poolSize - 1);
        s.push(0x111111111111L, 0x222222222222L);
        return s.size();
    }

    @Benchmark
    public int pushThree() {
        LongStack s = emptyStacks[emptyIdx];
        emptyIdx = (emptyIdx + 1) & (poolSize - 1);
        s.push(0x1L, 0x2L, 0x3L);
        return s.size();
    }

    @Benchmark
    public int pushFour() {
        LongStack s = emptyStacks[emptyIdx];
        emptyIdx = (emptyIdx + 1) & (poolSize - 1);
        s.push(0xAL, 0xBL, 0xCL, 0xDL);
        return s.size();
    }

    @Benchmark
    public int pushVarargs() {
        LongStack s = emptyStacks[emptyIdx];
        emptyIdx = (emptyIdx + 1) & (poolSize - 1);
        s.push(varargElements);
        return s.size();
    }

    @Benchmark
    public int pushArrayRange() {
        LongStack s = emptyStacks[emptyIdx];
        emptyIdx = (emptyIdx + 1) & (poolSize - 1);
        s.push(rangeElements, 0, rangeElements.length);
        return s.size();
    }

    @Benchmark
    public int pushAllContainer() {
        LongStack s = emptyStacks[emptyIdx];
        emptyIdx = (emptyIdx + 1) & (poolSize - 1);
        return s.pushAll(sourceContainer);
    }

    @Benchmark
    public int pushAllIterable() {
        LongStack s = emptyStacks[emptyIdx];
        emptyIdx = (emptyIdx + 1) & (poolSize - 1);
        return s.pushAll((Iterable<? extends LongCursor>) sourceContainer);
    }

    @Benchmark
    public int discard() {
        LongStack s = filledStacks[filledIdx];
        filledIdx = (filledIdx + 1) & (poolSize - 1);
        s.discard();
        return s.size();
    }

    @Benchmark
    public long pop() {
        LongStack s = filledStacks[filledIdx];
        filledIdx = (filledIdx + 1) & (poolSize - 1);
        return s.pop();
    }

    @Benchmark
    public long peek() {
        LongStack s = filledStacks[filledIdx];
        filledIdx = (filledIdx + 1) & (poolSize - 1);
        return s.peek();
    }

    @Benchmark
    public int fromFactory() {
        LongStack s = LongStack.from(varargElements);
        return s.size();
    }

    @Benchmark
    public int cloneStack() {
        LongStack s = filledStacks[filledIdx];
        filledIdx = (filledIdx + 1) & (poolSize - 1);
        LongStack copy = s.clone();
        return copy.size();
    }
}
