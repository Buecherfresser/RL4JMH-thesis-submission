package bench.generated.c005;

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
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.Random;
import com.carrotsearch.hppc.CharArrayDeque;
import com.carrotsearch.hppc.procedures.CharProcedure;
import com.carrotsearch.hppc.predicates.CharPredicate;
import com.carrotsearch.hppc.cursors.CharCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharArrayDequeBenchmark {

    private int dataSize = 1024;
    private CharArrayDeque deque;
    private CharArrayDeque dequeForEquals;
    private char[] data;
    private final Random rnd = new Random(0x1234abcdL);

    @Setup
    public void setup() {
        data = new char[dataSize];
        for (int i = 0; i < dataSize; i++) {
            data[i] = (char) rnd.nextInt(Character.MAX_VALUE + 1);
        }
        deque = new CharArrayDeque(dataSize);
        deque.addLast(data);
        dequeForEquals = new CharArrayDeque(dataSize);
        dequeForEquals.addLast(data);
    }

    @Benchmark
    public int benchmarkAddFirst() {
        CharArrayDeque d = deque.clone();
        d.addFirst((char) 0);
        return d.size();
    }

    @Benchmark
    public int benchmarkAddLast() {
        CharArrayDeque d = deque.clone();
        d.addLast((char) 0);
        return d.size();
    }

    @Benchmark
    public char benchmarkRemoveFirst() {
        CharArrayDeque d = deque.clone();
        return d.removeFirst();
    }

    @Benchmark
    public char benchmarkRemoveLast() {
        CharArrayDeque d = deque.clone();
        return d.removeLast();
    }

    @Benchmark
    public char benchmarkGetFirst() {
        return deque.getFirst();
    }

    @Benchmark
    public char benchmarkGetLast() {
        return deque.getLast();
    }

    @Benchmark
    public int benchmarkBufferIndexOf() {
        return deque.bufferIndexOf(data[0]);
    }

    @Benchmark
    public int benchmarkLastBufferIndexOf() {
        return deque.lastBufferIndexOf(data[data.length - 1]);
    }

    @Benchmark
    public boolean benchmarkContains() {
        return deque.contains(data[0]);
    }

    @Benchmark
    public int benchmarkSize() {
        return deque.size();
    }

    @Benchmark
    public int benchmarkClear() {
        CharArrayDeque d = deque.clone();
        d.clear();
        return d.isEmpty() ? 1 : 0;
    }

    @Benchmark
    public int benchmarkRelease() {
        CharArrayDeque d = deque.clone();
        d.release();
        return d.buffer.length;
    }

    @Benchmark
    public int benchmarkEnsureCapacity() {
        CharArrayDeque d = deque.clone();
        d.ensureCapacity(2048);
        return d.buffer.length;
    }

    @Benchmark
    public int benchmarkToArray() {
        return deque.toArray().length;
    }

    @Benchmark
    public long benchmarkRamBytesUsed() {
        return deque.ramBytesUsed();
    }

    @Benchmark
    public int benchmarkClone() {
        CharArrayDeque d = deque.clone();
        return d.size();
    }

    @Benchmark
    public boolean benchmarkEquals() {
        return deque.equals(dequeForEquals);
    }

    @Benchmark
    public int benchmarkIteratorSum() {
        int sum = 0;
        Iterator<CharCursor> it = deque.iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public int benchmarkDescendingIteratorSum() {
        int sum = 0;
        Iterator<CharCursor> it = deque.descendingIterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public int benchmarkForEachProcedure() {
        final int[] acc = new int[1];
        CharProcedure proc = new CharProcedure() {
            @Override
            public void apply(char value) {
                acc[0] += value;
            }
        };
        deque.forEach(proc);
        return acc[0];
    }

    @Benchmark
    public int benchmarkForEachPredicate() {
        final int[] acc = new int[1];
        CharPredicate pred = new CharPredicate() {
            @Override
            public boolean apply(char value) {
                acc[0] += value;
                return true;
            }
        };
        deque.forEach(pred);
        return acc[0];
    }

    @Benchmark
    public int benchmarkDescendingForEachProcedure() {
        final int[] acc = new int[1];
        CharProcedure proc = new CharProcedure() {
            @Override
            public void apply(char value) {
                acc[0] += value;
            }
        };
        deque.descendingForEach(proc);
        return acc[0];
    }

    @Benchmark
    public int benchmarkDescendingForEachPredicate() {
        final int[] acc = new int[1];
        CharPredicate pred = new CharPredicate() {
            @Override
            public boolean apply(char value) {
                acc[0] += value;
                return true;
            }
        };
        deque.descendingForEach(pred);
        return acc[0];
    }

    @Benchmark
    public int benchmarkRemoveFirstByValue() {
        CharArrayDeque d = deque.clone();
        return d.removeFirst(data[0]);
    }

    @Benchmark
    public int benchmarkRemoveLastByValue() {
        CharArrayDeque d = deque.clone();
        return d.removeLast(data[0]);
    }

    @Benchmark
    public int benchmarkRemoveAllByValue() {
        CharArrayDeque d = deque.clone();
        return d.removeAll(data[0]);
    }

    @Benchmark
    public int benchmarkRemoveAllPredicate() {
        CharArrayDeque d = deque.clone();
        CharPredicate pred = new CharPredicate() {
            @Override
            public boolean apply(char value) {
                return value == data[0];
            }
        };
        return d.removeAll(pred);
    }

    @Benchmark
    public int benchmarkHashCode() {
        return deque.hashCode();
    }
}
