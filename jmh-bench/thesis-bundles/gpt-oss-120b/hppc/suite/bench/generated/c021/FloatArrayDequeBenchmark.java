package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.FloatArrayDeque;
import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.predicates.FloatPredicate;
import com.carrotsearch.hppc.procedures.FloatProcedure;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatArrayDequeBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        FloatArrayDeque baseDeque;
        float[] elements;
        float sampleValue;

        @Setup(Level.Trial)
        public void setUp() {
            int size = 1024;
            elements = new float[size];
            for (int i = 0; i < size; i++) {
                elements[i] = i * 0.5f;
            }
            sampleValue = elements[0];
            baseDeque = new FloatArrayDeque(size);
            baseDeque.addLast(elements);
        }
    }

    private static class NoOpFloatProcedure implements FloatProcedure {
        @Override
        public void apply(float value) {
            // no-op
        }
    }

    private static class NoOpFloatPredicate implements FloatPredicate {
        @Override
        public boolean apply(float value) {
            return true;
        }
    }

    @Benchmark
    public int addFirst(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        d.addFirst(s.sampleValue);
        return d.size();
    }

    @Benchmark
    public int addLast(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        d.addLast(s.sampleValue);
        return d.size();
    }

    @Benchmark
    public float removeFirst(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.removeFirst();
    }

    @Benchmark
    public float removeLast(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.removeLast();
    }

    @Benchmark
    public float getFirst(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.getFirst();
    }

    @Benchmark
    public float getLast(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.getLast();
    }

    @Benchmark
    public int bufferIndexOf(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.bufferIndexOf(s.sampleValue);
    }

    @Benchmark
    public int lastBufferIndexOf(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.lastBufferIndexOf(s.sampleValue);
    }

    @Benchmark
    public int removeFirstFloat(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.removeFirst(s.sampleValue);
    }

    @Benchmark
    public int removeLastFloat(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.removeLast(s.sampleValue);
    }

    @Benchmark
    public int removeAllFloat(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.removeAll(s.sampleValue);
    }

    @Benchmark
    public int removeAtBufferIndex(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        int idx = d.head;
        d.removeAtBufferIndex(idx);
        return d.size();
    }

    @Benchmark
    public int size(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.size();
    }

    @Benchmark
    public int isEmpty(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.isEmpty() ? 1 : 0;
    }

    @Benchmark
    public int clear(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        d.clear();
        return d.size();
    }

    @Benchmark
    public int ensureCapacity(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        d.ensureCapacity(2048);
        return d.buffer.length;
    }

    @Benchmark
    public int toArray(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        float[] arr = d.toArray();
        return arr.length;
    }

    @Benchmark
    public int iterator(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        Iterator<FloatCursor> it = d.iterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }

    @Benchmark
    public int descendingIterator(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        Iterator<FloatCursor> it = d.descendingIterator();
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }

    @Benchmark
    public int forEachProcedure(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        d.forEach(new NoOpFloatProcedure());
        return d.size();
    }

    @Benchmark
    public int forEachPredicate(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        d.forEach(new NoOpFloatPredicate());
        return d.size();
    }

    @Benchmark
    public int descendingForEachProcedure(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        d.descendingForEach(new NoOpFloatProcedure());
        return d.size();
    }

    @Benchmark
    public int descendingForEachPredicate(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        d.descendingForEach(new NoOpFloatPredicate());
        return d.size();
    }

    @Benchmark
    public int removeAllPredicate(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        d.removeAll(new NoOpFloatPredicate());
        return d.size();
    }

    @Benchmark
    public int contains(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.contains(s.sampleValue) ? 1 : 0;
    }

    @Benchmark
    public int hashCode(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.hashCode();
    }

    @Benchmark
    public int equals(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return d.equals(s.baseDeque) ? 1 : 0;
    }

    @Benchmark
    public int cloneBenchmark(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        FloatArrayDeque c = d.clone();
        return c.size();
    }

    @Benchmark
    public int ramBytesAllocated(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return (int) d.ramBytesAllocated();
    }

    @Benchmark
    public int ramBytesUsed(BenchmarkState s) {
        FloatArrayDeque d = s.baseDeque.clone();
        return (int) d.ramBytesUsed();
    }

    @Benchmark
    public int staticFrom(BenchmarkState s) {
        FloatArrayDeque d = FloatArrayDeque.from(s.elements);
        return d.size();
    }
}
