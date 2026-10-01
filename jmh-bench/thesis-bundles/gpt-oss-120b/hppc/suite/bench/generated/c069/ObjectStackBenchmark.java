package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectStack;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectStackBenchmark {

  /* -------------------------------------------------------------------------
   * State for push* benchmarks (mutating a fresh empty stack each invocation)
   * ------------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class PushState {
    ObjectStack<String> stack;
    String[] elems;

    @Setup(Level.Trial)
    public void setup() {
      stack = new ObjectStack<>(128);
      elems = new String[] { "a", "b", "c", "d" };
    }
  }

  @Benchmark
  public int pushOne(PushState s) {
    s.stack.clear();
    s.stack.push(s.elems[0]);
    return s.stack.size();
  }

  @Benchmark
  public int pushTwo(PushState s) {
    s.stack.clear();
    s.stack.push(s.elems[0], s.elems[1]);
    return s.stack.size();
  }

  @Benchmark
  public int pushThree(PushState s) {
    s.stack.clear();
    s.stack.push(s.elems[0], s.elems[1], s.elems[2]);
    return s.stack.size();
  }

  @Benchmark
  public int pushFour(PushState s) {
    s.stack.clear();
    s.stack.push(s.elems[0], s.elems[1], s.elems[2], s.elems[3]);
    return s.stack.size();
  }

  @Benchmark
  public int pushArrayRange(PushState s) {
    s.stack.clear();
    s.stack.push(s.elems, 0, s.elems.length);
    return s.stack.size();
  }

  @Benchmark
  public int pushVarargs(PushState s) {
    s.stack.clear();
    s.stack.push(s.elems);
    return s.stack.size();
  }

  /* -------------------------------------------------------------------------
   * State for pushAll benchmarks (source container is pre‑filled)
   * ------------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class PushAllState {
    ObjectStack<String> source;
    ObjectStack<String> target;

    @Setup(Level.Trial)
    public void setup() {
      source = new ObjectStack<>(256);
      for (int i = 0; i < 200; i++) {
        source.push("elem-" + i);
      }
      target = new ObjectStack<>(256);
    }
  }

  @Benchmark
  public int pushAllFromContainer(PushAllState s) {
    s.target.clear();
    return s.target.pushAll(s.source);
  }

  @Benchmark
  public int pushAllFromIterable(PushAllState s) {
    s.target.clear();
    return s.target.pushAll(s.source);
  }

  /* -------------------------------------------------------------------------
   * State for pop / peek / discard benchmarks (stack is pre‑filled each call)
   * ------------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class MutatingState {
    ObjectStack<String> source;

    @Setup(Level.Trial)
    public void init() {
      source = new ObjectStack<>(256);
      for (int i = 0; i < 150; i++) {
        source.push("val-" + i);
      }
    }
  }

  @Benchmark
  public String pop(MutatingState s) {
    ObjectStack<String> stack = s.source.clone();
    return stack.pop();
  }

  @Benchmark
  public String peek(MutatingState s) {
    ObjectStack<String> stack = s.source.clone();
    return stack.peek();
  }

  @Benchmark
  public int discardOne(MutatingState s) {
    ObjectStack<String> stack = s.source.clone();
    stack.discard();
    return stack.size();
  }

  @Benchmark
  public int discardFive(MutatingState s) {
    ObjectStack<String> stack = s.source.clone();
    stack.discard(5);
    return stack.size();
  }

  /* -------------------------------------------------------------------------
   * State for discard (bulk) on a large stack without resetting each call.
   * ------------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class BulkDiscardState {
    ObjectStack<String> stack;

    @Setup(Level.Trial)
    public void setup() {
      stack = new ObjectStack<>(1024);
      for (int i = 0; i < 800; i++) {
        stack.push("bulk-" + i);
      }
    }
  }

  @Benchmark
  public int bulkDiscard(BulkDiscardState s) {
    s.stack.discard(100);
    return s.stack.size();
  }

  /* -------------------------------------------------------------------------
   * State for clone benchmark (exercise the clone method itself)
   * ------------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class CloneState {
    ObjectStack<String> original;

    @Setup(Level.Trial)
    public void setup() {
      original = new ObjectStack<>(256);
      for (int i = 0; i < 120; i++) {
        original.push("c-" + i);
      }
    }
  }

  @Benchmark
  public ObjectStack<String> cloneStack(CloneState s) {
    return s.original.clone();
  }
}
