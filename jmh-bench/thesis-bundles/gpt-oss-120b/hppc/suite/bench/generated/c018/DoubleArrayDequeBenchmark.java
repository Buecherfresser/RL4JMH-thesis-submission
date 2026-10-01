package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Iterator;
import com.carrotsearch.hppc.DoubleArrayDeque;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.predicates.DoublePredicate;
import com.carrotsearch.hppc.procedures.DoubleProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleArrayDequeBenchmark {

  /** State with a fully populated deque, reused for read‑only operations. */
  @State(Scope.Benchmark)
  public static class FilledState {
    DoubleArrayDeque deque;
    double[] data;

    @Setup(Level.Trial)
    public void setUp() {
      int size = 1024;
      data = new double[size];
      Random rnd = new Random(12345L);
      for (int i = 0; i < size; i++) {
        data[i] = rnd.nextDouble();
      }
      deque = new DoubleArrayDeque(size);
      for (double v : data) {
        deque.addLast(v);
      }
    }
  }

  /** State that creates a fresh deque for each invocation, used for mutating operations. */
  @State(Scope.Benchmark)
  public static class FreshState {
    DoubleArrayDeque deque;
    double[] data;

    @Setup(Level.Invocation)
    public void setUp() {
      int size = 1024;
      data = new double[size];
      Random rnd = new Random(12345L);
      for (int i = 0; i < size; i++) {
        data[i] = rnd.nextDouble();
      }
      deque = new DoubleArrayDeque(size);
      for (double v : data) {
        deque.addLast(v);
      }
    }
  }

  @Benchmark
  public double addFirst(FreshState s) {
    s.deque.addFirst(s.data[0]);
    return s.deque.getFirst();
  }

  @Benchmark
  public double addLast(FreshState s) {
    s.deque.addLast(s.data[0]);
    return s.deque.getLast();
  }

  @Benchmark
  public double removeFirst(FreshState s) {
    return s.deque.removeFirst();
  }

  @Benchmark
  public double removeLast(FreshState s) {
    return s.deque.removeLast();
  }

  @Benchmark
  public double getFirst(FilledState s) {
    return s.deque.getFirst();
  }

  @Benchmark
  public double getLast(FilledState s) {
    return s.deque.getLast();
  }

  @Benchmark
  public int size(FilledState s) {
    return s.deque.size();
  }

  @Benchmark
  public boolean contains(FilledState s) {
    return s.deque.contains(s.data[0]);
  }

  @Benchmark
  public void iterate(FilledState s, Blackhole bh) {
    Iterator<DoubleCursor> it = s.deque.iterator();
    while (it.hasNext()) {
      bh.consume(it.next().value);
    }
  }

  @Benchmark
  public void descendingIterate(FilledState s, Blackhole bh) {
    Iterator<DoubleCursor> it = s.deque.descendingIterator();
    while (it.hasNext()) {
      bh.consume(it.next().value);
    }
  }

  @Benchmark
  public void forEachProcedure(FilledState s, Blackhole bh) {
    DoubleProcedure proc = new DoubleProcedure() {
      @Override
      public void apply(double value) {
        bh.consume(value);
      }
    };
    s.deque.forEach(proc);
  }

  @Benchmark
  public void forEachPredicate(FilledState s, Blackhole bh) {
    DoublePredicate pred = new DoublePredicate() {
      @Override
      public boolean apply(double value) {
        bh.consume(value);
        return true;
      }
    };
    s.deque.forEach(pred);
  }

  @Benchmark
  public int removeFirstElement(FreshState s) {
    return s.deque.removeFirst(s.data[0]);
  }

  @Benchmark
  public int removeLastElement(FreshState s) {
    return s.deque.removeLast(s.data[0]);
  }

  @Benchmark
  public int bufferIndexOf(FilledState s) {
    return s.deque.bufferIndexOf(s.data[0]);
  }

  @Benchmark
  public int lastBufferIndexOf(FilledState s) {
    return s.deque.lastBufferIndexOf(s.data[0]);
  }

  @Benchmark
  public boolean clear(FreshState s) {
    s.deque.clear();
    return s.deque.isEmpty();
  }

  @Benchmark
  public int cloneDeque(FilledState s) {
    DoubleArrayDeque cloned = s.deque.clone();
    return cloned.size();
  }

  @Benchmark
  public int toArray(FilledState s) {
    double[] arr = s.deque.toArray();
    return arr.length;
  }

  @Benchmark
  public long ramBytesUsed(FilledState s) {
    return s.deque.ramBytesUsed();
  }

  @Benchmark
  public long ramBytesAllocated(FilledState s) {
    return s.deque.ramBytesAllocated();
  }

  @Benchmark
  public int hashCode(FilledState s) {
    return s.deque.hashCode();
  }

  @Benchmark
  public boolean equalsSelf(FilledState s) {
    return s.deque.equals(s.deque);
  }
}
