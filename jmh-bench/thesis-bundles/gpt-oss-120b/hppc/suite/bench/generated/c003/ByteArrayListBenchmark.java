package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Iterator;
import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.predicates.BytePredicate;
import com.carrotsearch.hppc.procedures.ByteProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteArrayListBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ByteArrayList base;
        byte sampleByte;
        byte[] sampleArray;
        BytePredicate predicate;
        ByteProcedure procedure;
        ByteArrayList other;

        @Setup(Level.Trial)
        public void setup() {
            Random rnd = new Random(0);
            int size = 1024;
            base = new ByteArrayList(size);
            sampleArray = new byte[size];
            for (int i = 0; i < size; i++) {
                byte b = (byte) rnd.nextInt(256);
                base.add(b);
                sampleArray[i] = b;
            }
            sampleByte = (byte) rnd.nextInt(256);
            predicate = new BytePredicate() {
                @Override
                public boolean apply(byte value) {
                    return value == sampleByte;
                }
            };
            procedure = new ByteProcedure() {
                @Override
                public void apply(byte value) {
                    // no-op
                }
            };
            other = base.clone();
        }
    }

    @Benchmark
    public ByteArrayList addByte(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        list.add(s.sampleByte);
        return list;
    }

    @Benchmark
    public ByteArrayList addTwoBytes(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        list.add(s.sampleByte, s.sampleByte);
        return list;
    }

    @Benchmark
    public ByteArrayList addArraySlice(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        list.add(s.sampleArray, 0, s.sampleArray.length);
        return list;
    }

    @Benchmark
    public ByteArrayList addVarargs(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        list.add(s.sampleByte, s.sampleByte, s.sampleByte);
        return list;
    }

    @Benchmark
    public int addAllContainer(BenchmarkState s) {
        ByteArrayList list = new ByteArrayList(s.base.size());
        return list.addAll(s.base);
    }

    @Benchmark
    public int addAllIterable(BenchmarkState s) {
        ByteArrayList list = new ByteArrayList(s.base.size());
        return list.addAll(s.base);
    }

    @Benchmark
    public ByteArrayList insert(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        int idx = list.size() / 2;
        list.insert(idx, s.sampleByte);
        return list;
    }

    @Benchmark
    public byte get(BenchmarkState s) {
        int idx = s.base.size() / 2;
        return s.base.get(idx);
    }

    @Benchmark
    public byte set(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        int idx = list.size() / 2;
        return list.set(idx, s.sampleByte);
    }

    @Benchmark
    public byte removeAt(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        int idx = list.size() / 2;
        return list.removeAt(idx);
    }

    @Benchmark
    public byte removeLast(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        return list.removeLast();
    }

    @Benchmark
    public void removeRange(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        int from = list.size() / 4;
        int to = from + (list.size() / 4);
        list.removeRange(from, to);
    }

    @Benchmark
    public boolean removeElement(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        return list.removeElement(s.sampleByte);
    }

    @Benchmark
    public int removeFirstByte(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        return list.removeFirst(s.sampleByte);
    }

    @Benchmark
    public int removeLastByte(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        return list.removeLast(s.sampleByte);
    }

    @Benchmark
    public int removeAllValue(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        return list.removeAll(s.sampleByte);
    }

    @Benchmark
    public boolean contains(BenchmarkState s) {
        return s.base.contains(s.sampleByte);
    }

    @Benchmark
    public int indexOf(BenchmarkState s) {
        return s.base.indexOf(s.sampleByte);
    }

    @Benchmark
    public int lastIndexOf(BenchmarkState s) {
        return s.base.lastIndexOf(s.sampleByte);
    }

    @Benchmark
    public boolean isEmpty(BenchmarkState s) {
        return s.base.isEmpty();
    }

    @Benchmark
    public void ensureCapacity(BenchmarkState s, Blackhole bh) {
        ByteArrayList list = s.base.clone();
        list.ensureCapacity(list.size() + 100);
        bh.consume(list.buffer.length);
    }

    @Benchmark
    public ByteArrayList resize(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        list.resize(list.size() / 2);
        return list;
    }

    @Benchmark
    public ByteArrayList trimToSize(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        list.trimToSize();
        return list;
    }

    @Benchmark
    public void clear(BenchmarkState s, Blackhole bh) {
        ByteArrayList list = s.base.clone();
        list.clear();
        bh.consume(list.elementsCount);
    }

    @Benchmark
    public void release(BenchmarkState s, Blackhole bh) {
        ByteArrayList list = s.base.clone();
        list.release();
        bh.consume(list.buffer);
    }

    @Benchmark
    public byte[] toArray(BenchmarkState s) {
        return s.base.toArray();
    }

    @Benchmark
    public ByteArrayList sort(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        list.sort();
        return list;
    }

    @Benchmark
    public ByteArrayList reverse(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        list.reverse();
        return list;
    }

    @Benchmark
    public ByteArrayList cloneList(BenchmarkState s) {
        return s.base.clone();
    }

    @Benchmark
    public int hashCode(BenchmarkState s) {
        return s.base.hashCode();
    }

    @Benchmark
    public boolean equals(BenchmarkState s) {
        return s.base.equals(s.other);
    }

    @Benchmark
    public long ramBytesAllocated(BenchmarkState s) {
        return s.base.ramBytesAllocated();
    }

    @Benchmark
    public long ramBytesUsed(BenchmarkState s) {
        return s.base.ramBytesUsed();
    }

    @Benchmark
    public void iterator(BenchmarkState s, Blackhole bh) {
        Iterator<ByteCursor> it = s.base.iterator();
        if (it.hasNext()) {
            ByteCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public ByteProcedure forEachProcedure(BenchmarkState s) {
        return s.base.forEach(s.procedure);
    }

    @Benchmark
    public ByteProcedure forEachProcedureSlice(BenchmarkState s) {
        int from = s.base.size() / 4;
        int to = from + (s.base.size() / 2);
        return s.base.forEach(s.procedure, from, to);
    }

    @Benchmark
    public int removeAllPredicate(BenchmarkState s) {
        ByteArrayList list = s.base.clone();
        return list.removeAll(s.predicate);
    }

    @Benchmark
    public BytePredicate forEachPredicate(BenchmarkState s) {
        return s.base.forEach(s.predicate);
    }

    @Benchmark
    public BytePredicate forEachPredicateSlice(BenchmarkState s) {
        int from = s.base.size() / 4;
        int to = from + (s.base.size() / 2);
        return s.base.forEach(s.predicate, from, to);
    }
}
