package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.DoubleStack;
import com.carrotsearch.hppc.DoubleContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleStackBenchmark {

  /* -------------------------------------------------------------------------
   * State for push‑related benchmarks.
   * ----------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class PushState {
    DoubleStack stack;
    double[] elems;
    DoubleStack sourceContainer;

    @Setup(Level.Trial)
    public void setUp() {
      elems = new double[] {1.0, 2.0, 3.0, 4.0};
      stack = new DoubleStack(16);
      sourceContainer = new DoubleStack(elems.length);
      sourceContainer.push(elems);
    }

    @Setup(Level.Invocation)
    public void reset() {
      stack.clear();
    }
  }

  /* -------------------------------------------------------------------------
   * State for pop / peek / discard / clone benchmarks.
   * ----------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class PopState {
    DoubleStack stack;
    double[] elems;

    @Setup(Level.Trial)
    public void setUp() {
      elems = new double[] {10.0, 20.0, 30.0, 40.0, 50.0};
      stack = new DoubleStack(elems.length);
      stack.push(elems);
    }

    @Setup(Level.Invocation)
    public void reset() {
      stack.clear();
      stack.push(elems);
    }
  }

  /* -------------------------------------------------------------------------
   * State for static factory benchmark.
   * ----------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class FromState {
    double[] elems;

    @Setup(Level.Trial)
    public void setUp() {
      elems = new double[] {5.0, 6.0, 7.0, 8.0};
    }
  }

  /* -------------------------------------------------------------------------
   * Push benchmarks (return stack size to avoid dead‑code elimination).
   * ----------------------------------------------------------------------- */
  @Benchmark
  public int pushOne(PushState s) {
    s.stack.push(s.elems[0]);
    return s.stack.size();
  }

  @Benchmark
  public int pushTwo(PushState s) {
    s.stack.push(s.elems[0], s.elems[1]);
    return s.stack.size();
  }

  @Benchmark
  public int pushThree(PushState s) {
    s.stack.push(s.elems[0], s.elems[1], s.elems[2]);
    return s.stack.size();
  }

  @Benchmark
  public int pushFour(PushState s) {
    s.stack.push(s.elems[0], s.elems[1], s.elems[2], s.elems[3]);
    return s.stack.size();
  }

  @Benchmark
  public int pushArray(PushState s) {
    s.stack.push(s.elems, 0, s.elems.length);
    return s.stack.size();
  }

  @Benchmark
  public int pushVarargs(PushState s) {
    s.stack.push(s.elems);
    return s.stack.size();
  }

  @Benchmark
  public int pushAllContainer(PushState s) {
    return s.stack.pushAll((DoubleContainer) s.sourceContainer);
  }

  @Benchmark
  public int pushAllIterable(PushState s) {
    return s.stack.pushAll((Iterable<? extends com.carrotsearch.hppc.cursors.DoubleCursor>) s.sourceContainer);
  }

  /* -------------------------------------------------------------------------
   * Pop / Peek benchmarks.
   * ----------------------------------------------------------------------- */
  @Benchmark
  public double pop(PopState s) {
    return s.stack.pop();
  }

  @Benchmark
  public double peek(PopState s) {
    return s.stack.peek();
  }

  /* -------------------------------------------------------------------------
   * Discard benchmarks (return resulting size).
   * ----------------------------------------------------------------------- */
  @Benchmark
  public int discardOne(PopState s) {
    s.stack.discard();
    return s.stack.size();
  }

  @Benchmark
  public int discardCount(PopState s) {
    s.stack.discard(2);
    return s.stack.size();
  }

  /* -------------------------------------------------------------------------
   * Clone benchmark (returns the cloned stack).
   * ----------------------------------------------------------------------- */
  @Benchmark
  public DoubleStack cloneStack(PopState s) {
    return s.stack.clone();
  }

  /* -------------------------------------------------------------------------
   * Static factory benchmark.
   * ----------------------------------------------------------------------- */
  @Benchmark
  public DoubleStack fromVarargs(FromState s) {
    return DoubleStack.from(s.elems);
  }
}
