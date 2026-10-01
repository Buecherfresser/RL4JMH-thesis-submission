package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ByteStack;
import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.cursors.ByteCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteStackBenchmark {

    private ByteStack mutableStack;
    private ByteStack prefilledStack;
    private ByteArrayList sourceList;
    private Iterable<? extends ByteCursor> sourceIterable;
    private byte[] smallArray;
    private byte[] fourArray;

    @Setup(Level.Trial)
    public void setUp() {
        int capacity = 1024;

        mutableStack = new ByteStack(capacity);
        prefilledStack = new ByteStack(capacity);
        for (int i = 0; i < capacity; i++) {
            prefilledStack.push((byte) i);
        }

        sourceList = new ByteArrayList(capacity);
        for (int i = 0; i < capacity; i++) {
            sourceList.add((byte) i);
        }
        sourceIterable = sourceList;

        smallArray = new byte[] {1, 2, 3, 4, 5};
        fourArray = new byte[] {10, 20, 30, 40};
    }

    @Benchmark
    public int pushOne() {
        mutableStack.clear();
        mutableStack.push((byte) 0x1);
        return mutableStack.size();
    }

    @Benchmark
    public int pushTwo() {
        mutableStack.clear();
        mutableStack.push((byte) 0x1, (byte) 0x2);
        return mutableStack.size();
    }

    @Benchmark
    public int pushThree() {
        mutableStack.clear();
        mutableStack.push((byte) 0x1, (byte) 0x2, (byte) 0x3);
        return mutableStack.size();
    }

    @Benchmark
    public int pushFour() {
        mutableStack.clear();
        mutableStack.push((byte) 0x1, (byte) 0x2, (byte) 0x3, (byte) 0x4);
        return mutableStack.size();
    }

    @Benchmark
    public int pushArrayRange() {
        mutableStack.clear();
        mutableStack.push(smallArray, 0, smallArray.length);
        return mutableStack.size();
    }

    @Benchmark
    public int pushVarargs() {
        mutableStack.clear();
        mutableStack.push(smallArray);
        return mutableStack.size();
    }

    @Benchmark
    public int pushAllContainer() {
        mutableStack.clear();
        mutableStack.pushAll(sourceList);
        return mutableStack.size();
    }

    @Benchmark
    public int pushAllIterable() {
        mutableStack.clear();
        mutableStack.pushAll(sourceIterable);
        return mutableStack.size();
    }

    @Benchmark
    public byte pop() {
        ByteStack copy = prefilledStack.clone();
        return copy.pop();
    }

    @Benchmark
    public byte peek() {
        ByteStack copy = prefilledStack.clone();
        return copy.peek();
    }

    @Benchmark
    public int discardTen() {
        ByteStack copy = prefilledStack.clone();
        copy.discard(10);
        return copy.size();
    }

    @Benchmark
    public int discardOne() {
        ByteStack copy = prefilledStack.clone();
        copy.discard();
        return copy.size();
    }

    @Benchmark
    public ByteStack cloneStack() {
        return prefilledStack.clone();
    }

    @Benchmark
    public ByteStack fromVarargs() {
        return ByteStack.from(smallArray);
    }
}
