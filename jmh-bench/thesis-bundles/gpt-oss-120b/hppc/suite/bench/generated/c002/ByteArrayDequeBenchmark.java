package bench.generated.c002;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import com.carrotsearch.hppc.ByteArrayDeque;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.procedures.ByteProcedure;
import com.carrotsearch.hppc.predicates.BytePredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteArrayDequeBenchmark {

    private ByteArrayDeque baseDeque;
    private final byte[] varargs3 = new byte[] {1, 2, 3};
    private final byte[] varargs5 = new byte[] {1, 2, 3, 4, 5};

    @Setup(Level.Trial)
    public void setUp() {
        int size = 1024;
        baseDeque = new ByteArrayDeque(size);
        for (int i = 0; i < size; i++) {
            baseDeque.addLast((byte) (i & 0xFF));
        }
    }

    @Benchmark
    public int addFirstSingle() {
        ByteArrayDeque dq = baseDeque.clone();
        dq.addFirst((byte) 42);
        return dq.size();
    }

    @Benchmark
    public int addFirstVarargs() {
        ByteArrayDeque dq = baseDeque.clone();
        dq.addFirst(varargs3);
        return dq.size();
    }

    @Benchmark
    public int addLastSingle() {
        ByteArrayDeque dq = baseDeque.clone();
        dq.addLast((byte) 99);
        return dq.size();
    }

    @Benchmark
    public int addLastVarargs() {
        ByteArrayDeque dq = baseDeque.clone();
        dq.addLast(varargs5);
        return dq.size();
    }

    @Benchmark
    public byte removeFirst() {
        ByteArrayDeque dq = baseDeque.clone();
        return dq.removeFirst();
    }

    @Benchmark
    public byte removeLast() {
        ByteArrayDeque dq = baseDeque.clone();
        return dq.removeLast();
    }

    @Benchmark
    public byte getFirst() {
        ByteArrayDeque dq = baseDeque.clone();
        return dq.getFirst();
    }

    @Benchmark
    public byte getLast() {
        ByteArrayDeque dq = baseDeque.clone();
        return dq.getLast();
    }

    @Benchmark
    public int removeFirstByValue() {
        ByteArrayDeque dq = baseDeque.clone();
        return dq.removeFirst((byte) 0);
    }

    @Benchmark
    public int removeLastByValue() {
        ByteArrayDeque dq = baseDeque.clone();
        return dq.removeLast((byte) 0);
    }

    @Benchmark
    public int bufferIndexOf() {
        ByteArrayDeque dq = baseDeque.clone();
        return dq.bufferIndexOf((byte) 0);
    }

    @Benchmark
    public int lastBufferIndexOf() {
        ByteArrayDeque dq = baseDeque.clone();
        return dq.lastBufferIndexOf((byte) 0);
    }

    @Benchmark
    public int removeAllValue() {
        ByteArrayDeque dq = baseDeque.clone();
        return dq.removeAll((byte) 0);
    }

    @Benchmark
    public int removeAllPredicate(Blackhole bh) {
        ByteArrayDeque dq = baseDeque.clone();
        BytePredicate pred = value -> {
            bh.consume(value);
            return (value & 1) == 0;
        };
        return dq.removeAll(pred);
    }

    @Benchmark
    public int forEachProcedure(Blackhole bh) {
        ByteArrayDeque dq = baseDeque.clone();
        ByteProcedure proc = bh::consume;
        dq.forEach(proc);
        return dq.size();
    }

    @Benchmark
    public int forEachPredicate(Blackhole bh) {
        ByteArrayDeque dq = baseDeque.clone();
        BytePredicate pred = value -> {
            bh.consume(value);
            return true;
        };
        dq.forEach(pred);
        return dq.size();
    }

    @Benchmark
    public int descendingForEachProcedure(Blackhole bh) {
        ByteArrayDeque dq = baseDeque.clone();
        ByteProcedure proc = bh::consume;
        dq.descendingForEach(proc);
        return dq.size();
    }

    @Benchmark
    public int descendingForEachPredicate(Blackhole bh) {
        ByteArrayDeque dq = baseDeque.clone();
        BytePredicate pred = value -> {
            bh.consume(value);
            return true;
        };
        dq.descendingForEach(pred);
        return dq.size();
    }

    @Benchmark
    public byte iteratorNext() {
        ByteArrayDeque dq = baseDeque.clone();
        Iterator<ByteCursor> it = dq.iterator();
        return it.hasNext() ? it.next().value : (byte) -1;
    }

    @Benchmark
    public byte descendingIteratorNext() {
        ByteArrayDeque dq = baseDeque.clone();
        Iterator<ByteCursor> it = dq.descendingIterator();
        return it.hasNext() ? it.next().value : (byte) -1;
    }

    @Benchmark
    public int size() {
        return baseDeque.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return baseDeque.isEmpty();
    }

    @Benchmark
    public int clear() {
        ByteArrayDeque dq = baseDeque.clone();
        dq.clear();
        return dq.size();
    }

    @Benchmark
    public int release() {
        ByteArrayDeque dq = baseDeque.clone();
        dq.release();
        return dq.buffer.length;
    }

    @Benchmark
    public int ensureCapacity() {
        ByteArrayDeque dq = baseDeque.clone();
        dq.ensureCapacity(2048);
        return dq.buffer.length;
    }

    @Benchmark
    public int toArrayLength() {
        ByteArrayDeque dq = baseDeque.clone();
        byte[] arr = dq.toArray();
        return arr.length;
    }

    @Benchmark
    public long ramBytesUsed() {
        return baseDeque.ramBytesUsed();
    }

    @Benchmark
    public long ramBytesAllocated() {
        return baseDeque.ramBytesAllocated();
    }

    @Benchmark
    public boolean equalsSelf() {
        return baseDeque.equals(baseDeque);
    }

    @Benchmark
    public boolean equalsClone() {
        ByteArrayDeque clone = baseDeque.clone();
        return baseDeque.equals(clone);
    }

    @Benchmark
    public int hashCodeValue() {
        return baseDeque.hashCode();
    }

    @Benchmark
    public int cloneSize() {
        ByteArrayDeque clone = baseDeque.clone();
        return clone.size();
    }

    @Benchmark
    public int fromStatic() {
        ByteArrayDeque dq = ByteArrayDeque.from(varargs5);
        return dq.size();
    }
}
