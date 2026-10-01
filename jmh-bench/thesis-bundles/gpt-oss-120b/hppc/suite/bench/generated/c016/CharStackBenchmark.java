package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.CharStack;
import com.carrotsearch.hppc.CharArrayList;
import com.carrotsearch.hppc.CharContainer;
import com.carrotsearch.hppc.cursors.CharCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharStackBenchmark {

  private static final int PRESET_SIZE = 1024;
  private static final int DISCARD_COUNT = 10;
  private static final int PUSH_ARRAY_LEN = 256;

  private char[] pushArray;
  private CharArrayList pushAllContainer;
  private CharStack prefilledStack;

  @Setup(Level.Trial)
  public void setup() {
    // Prepare a reusable array for push(... ) benchmarks.
    pushArray = new char[PUSH_ARRAY_LEN];
    for (int i = 0; i < PUSH_ARRAY_LEN; i++) {
      pushArray[i] = (char) (i & 0xFFFF);
    }

    // Container used for pushAll(..) benchmarks.
    pushAllContainer = new CharArrayList(PUSH_ARRAY_LEN);
    for (char c : pushArray) {
      pushAllContainer.add(c);
    }

    // Prefilled stack used by read‑only and mutation‑reset benchmarks.
    prefilledStack = new CharStack(PRESET_SIZE);
    for (int i = 0; i < PRESET_SIZE; i++) {
      prefilledStack.push(pushArray[i % PUSH_ARRAY_LEN]);
    }
  }

  // --------------------------------------------------------------------------
  // Push variants (mutating). Each creates a fresh stack to isolate the cost.
  // --------------------------------------------------------------------------

  @Benchmark
  public int benchmarkPushSingle() {
    CharStack s = new CharStack();
    s.push((char) 1);
    return s.size();
  }

  @Benchmark
  public int benchmarkPushTwo() {
    CharStack s = new CharStack();
    s.push((char) 1, (char) 2);
    return s.size();
  }

  @Benchmark
  public int benchmarkPushThree() {
    CharStack s = new CharStack();
    s.push((char) 1, (char) 2, (char) 3);
    return s.size();
  }

  @Benchmark
  public int benchmarkPushFour() {
    CharStack s = new CharStack();
    s.push((char) 1, (char) 2, (char) 3, (char) 4);
    return s.size();
  }

  @Benchmark
  public int benchmarkPushArray() {
    CharStack s = new CharStack();
    s.push(pushArray, 0, pushArray.length);
    return s.size();
  }

  @Benchmark
  public int benchmarkPushVarargs() {
    CharStack s = new CharStack();
    s.push(pushArray);
    return s.size();
  }

  // --------------------------------------------------------------------------
  // pushAll variants (mutating)
  // --------------------------------------------------------------------------

  @Benchmark
  public int benchmarkPushAllContainer() {
    CharStack s = new CharStack();
    return s.pushAll((CharContainer) pushAllContainer);
  }

  @Benchmark
  public int benchmarkPushAllIterable() {
    CharStack s = new CharStack();
    return s.pushAll((Iterable<? extends CharCursor>) pushAllContainer);
  }

  // --------------------------------------------------------------------------
  // Discard variants (mutating)
  // --------------------------------------------------------------------------

  @Benchmark
  public int benchmarkDiscardCount() {
    CharStack s = prefilledStack.clone();
    s.discard(DISCARD_COUNT);
    return s.size();
  }

  @Benchmark
  public int benchmarkDiscardOne() {
    CharStack s = prefilledStack.clone();
    s.discard();
    return s.size();
  }

  // --------------------------------------------------------------------------
  // Pop and peek (mutating vs read‑only)
  // --------------------------------------------------------------------------

  @Benchmark
  public char benchmarkPop() {
    CharStack s = prefilledStack.clone();
    return s.pop();
  }

  @Benchmark
  public char benchmarkPeek() {
    return prefilledStack.peek();
  }

  // --------------------------------------------------------------------------
  // Other public methods
  // --------------------------------------------------------------------------

  @Benchmark
  public int benchmarkSize() {
    return prefilledStack.size();
  }

  @Benchmark
  public int benchmarkCloneSize() {
    CharStack cloned = prefilledStack.clone();
    return cloned.size();
  }

  @Benchmark
  public int benchmarkClear() {
    CharStack s = prefilledStack.clone();
    s.clear();
    return s.size();
  }
}
