package bench.generated.c023;

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
import java.util.Random;
import com.carrotsearch.hppc.FloatStack;
import com.carrotsearch.hppc.FloatArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatStackBenchmark {

  /* ---------------------------------------------------------------------- */
  /* State for empty stack used by push benchmarks                           */
  /* ---------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class EmptyStackState {
    FloatStack stack;

    @Setup(Level.Trial)
    public void trialSetup() {
      stack = new FloatStack();
    }

    @Setup(Level.Invocation)
    public void invocationReset() {
      stack.clear();
    }
  }

  /* ---------------------------------------------------------------------- */
  /* State for a pre‑filled stack used by pop/peek/discard/clone benchmarks   */
  /* ---------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class FilledStackState {
    static final int SIZE = 1024;
    FloatStack stack;
    float[] data;

    @Setup(Level.Trial)
    public void trialSetup() {
      data = new float[SIZE];
      Random rnd = new Random(12345L);
      for (int i = 0; i < SIZE; i++) {
        data[i] = rnd.nextFloat();
      }
      stack = new FloatStack(SIZE);
    }

    @Setup(Level.Invocation)
    public void invocationSetup() {
      stack.clear();
      stack.push(data, 0, data.length);
    }
  }

  /* ---------------------------------------------------------------------- */
  /* State for push(float[] ...) benchmark                                    */
  /* ---------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class PushArrayState {
    FloatStack stack;
    float[] elements;

    @Setup(Level.Trial)
    public void trialSetup() {
      stack = new FloatStack();
      elements = new float[8];
      Random rnd = new Random(54321L);
      for (int i = 0; i < elements.length; i++) {
        elements[i] = rnd.nextFloat();
      }
    }

    @Setup(Level.Invocation)
    public void invocationReset() {
      stack.clear();
    }
  }

  /* ---------------------------------------------------------------------- */
  /* State for push(varargs) benchmark                                        */
  /* ---------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class PushVarargsState {
    FloatStack stack;
    float[] varargs;

    @Setup(Level.Trial)
    public void trialSetup() {
      stack = new FloatStack();
      varargs = new float[] {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
    }

    @Setup(Level.Invocation)
    public void invocationReset() {
      stack.clear();
    }
  }

  /* ---------------------------------------------------------------------- */
  /* State for pushAll(container) and pushAll(iterable) benchmarks           */
  /* ---------------------------------------------------------------------- */
  @State(Scope.Benchmark)
  public static class PushAllState {
    FloatStack stack;
    FloatArrayList container;

    @Setup(Level.Trial)
    public void trialSetup() {
      stack = new FloatStack();
      container = new FloatArrayList();
      Random rnd = new Random(98765L);
      for (int i = 0; i < 256; i++) {
        container.add(rnd.nextFloat());
      }
    }

    @Setup(Level.Invocation)
    public void invocationReset() {
      stack.clear();
    }
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: push one element                                            */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int pushOne(EmptyStackState s) {
    s.stack.push(1.23f);
    return s.stack.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: push two elements                                           */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int pushTwo(EmptyStackState s) {
    s.stack.push(1.23f, 4.56f);
    return s.stack.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: push three elements                                         */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int pushThree(EmptyStackState s) {
    s.stack.push(1.23f, 4.56f, 7.89f);
    return s.stack.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: push four elements                                          */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int pushFour(EmptyStackState s) {
    s.stack.push(1.23f, 4.56f, 7.89f, 0.12f);
    return s.stack.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: push(float[] elements, int start, int len)                 */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int pushArray(PushArrayState s) {
    s.stack.push(s.elements, 0, s.elements.length);
    return s.stack.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: push(varargs)                                              */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int pushVarargs(PushVarargsState s) {
    s.stack.push(s.varargs);
    return s.stack.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: pushAll(FloatContainer)                                    */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int pushAllContainer(PushAllState s) {
    s.stack.pushAll(s.container);
    return s.stack.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: pushAll(Iterable<? extends FloatCursor>)                    */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int pushAllIterable(PushAllState s) {
    s.stack.pushAll(s.container);
    return s.stack.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: pop (remove top element)                                   */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public float pop(FilledStackState s) {
    return s.stack.pop();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: peek (read top element)                                    */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public float peek(FilledStackState s) {
    return s.stack.peek();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: discard() – remove one element from top                     */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int discardOne(FilledStackState s) {
    s.stack.discard();
    return s.stack.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: discard(int) – remove several elements from top             */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int discardMultiple(FilledStackState s) {
    s.stack.discard(3);
    return s.stack.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: clone – duplicate the stack                                 */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int cloneStack(FilledStackState s) {
    FloatStack cloned = s.stack.clone();
    return cloned.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: static factory FloatStack.from(float...)                    */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int fromFactory(PushVarargsState s) {
    FloatStack created = FloatStack.from(s.varargs);
    return created.size();
  }

  /* ---------------------------------------------------------------------- */
  /* Benchmark: clear – reset the stack to empty                            */
  /* ---------------------------------------------------------------------- */
  @Benchmark
  public int clear(FilledStackState s) {
    s.stack.clear();
    return s.stack.size();
  }
}
