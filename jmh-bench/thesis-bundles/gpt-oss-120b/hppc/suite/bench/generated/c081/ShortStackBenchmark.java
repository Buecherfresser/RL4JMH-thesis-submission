package bench.generated.c081;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortStack;
import com.carrotsearch.hppc.ShortArrayList;
import com.carrotsearch.hppc.ShortContainer;
import com.carrotsearch.hppc.cursors.ShortCursor;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortStackBenchmark {

  /* ---------- State for push (single element) ---------- */
  @State(Scope.Benchmark)
  public static class PushOneState {
    ShortStack stack = new ShortStack(1024);
    short value = 1;
  }

  @Benchmark
  public void pushOne(PushOneState s, Blackhole bh) {
    s.stack.push(s.value);
    bh.consume(s.stack);
  }

  /* ---------- State for push (two elements) ---------- */
  @State(Scope.Benchmark)
  public static class PushTwoState {
    ShortStack stack = new ShortStack(1024);
    short v1 = 1, v2 = 2;
  }

  @Benchmark
  public void pushTwo(PushTwoState s, Blackhole bh) {
    s.stack.push(s.v1, s.v2);
    bh.consume(s.stack);
  }

  /* ---------- State for push (three elements) ---------- */
  @State(Scope.Benchmark)
  public static class PushThreeState {
    ShortStack stack = new ShortStack(1024);
    short v1 = 1, v2 = 2, v3 = 3;
  }

  @Benchmark
  public void pushThree(PushThreeState s, Blackhole bh) {
    s.stack.push(s.v1, s.v2, s.v3);
    bh.consume(s.stack);
  }

  /* ---------- State for push (four elements) ---------- */
  @State(Scope.Benchmark)
  public static class PushFourState {
    ShortStack stack = new ShortStack(1024);
    short v1 = 1, v2 = 2, v3 = 3, v4 = 4;
  }

  @Benchmark
  public void pushFour(PushFourState s, Blackhole bh) {
    s.stack.push(s.v1, s.v2, s.v3, s.v4);
    bh.consume(s.stack);
  }

  /* ---------- State for push from array ---------- */
  @State(Scope.Benchmark)
  public static class PushArrayState {
    ShortStack stack = new ShortStack(2048);
    short[] data = new short[128];
    {
      Random rnd = new Random(0);
      for (int i = 0; i < data.length; i++) {
        data[i] = (short) rnd.nextInt(Short.MAX_VALUE + 1);
      }
    }
    int start = 0;
    int len = data.length;
  }

  @Benchmark
  public void pushArray(PushArrayState s, Blackhole bh) {
    s.stack.push(s.data, s.start, s.len);
    bh.consume(s.stack);
  }

  /* ---------- State for push varargs ---------- */
  @State(Scope.Benchmark)
  public static class PushVarargsState {
    ShortStack stack = new ShortStack(2048);
    short[] elements = new short[64];
    {
      Random rnd = new Random(1);
      for (int i = 0; i < elements.length; i++) {
        elements[i] = (short) rnd.nextInt(Short.MAX_VALUE + 1);
      }
    }
  }

  @Benchmark
  public void pushVarargs(PushVarargsState s, Blackhole bh) {
    s.stack.push(s.elements);
    bh.consume(s.stack);
  }

  /* ---------- State for pushAll(ShortContainer) ---------- */
  @State(Scope.Benchmark)
  public static class PushAllContainerState {
    ShortStack stack = new ShortStack(4096);
    ShortArrayList container = new ShortArrayList(256);
    {
      Random rnd = new Random(2);
      for (int i = 0; i < 256; i++) {
        container.add((short) rnd.nextInt(Short.MAX_VALUE + 1));
      }
    }
  }

  @Benchmark
  public int pushAllContainer(PushAllContainerState s) {
    return s.stack.pushAll((ShortContainer) s.container);
  }

  /* ---------- State for pushAll(Iterable) ---------- */
  @State(Scope.Benchmark)
  public static class PushAllIterableState {
    ShortStack stack = new ShortStack(4096);
    ShortArrayList iterable = new ShortArrayList(256);
    {
      Random rnd = new Random(3);
      for (int i = 0; i < 256; i++) {
        iterable.add((short) rnd.nextInt(Short.MAX_VALUE + 1));
      }
    }
  }

  @Benchmark
  public int pushAllIterable(PushAllIterableState s) {
    return s.stack.pushAll((Iterable<? extends ShortCursor>) s.iterable);
  }

  /* ---------- State for discard(int) ---------- */
  @State(Scope.Benchmark)
  public static class DiscardIntState {
    ShortStack stack = new ShortStack(1024);
    {
      for (int i = 0; i < 512; i++) {
        stack.push((short) i);
      }
    }
    int count = 10;
  }

  @Benchmark
  public void discardInt(DiscardIntState s, Blackhole bh) {
    s.stack.discard(s.count);
    bh.consume(s.stack);
  }

  /* ---------- State for discard() ---------- */
  @State(Scope.Benchmark)
  public static class DiscardOneState {
    ShortStack stack = new ShortStack(1024);
    {
      for (int i = 0; i < 512; i++) {
        stack.push((short) i);
      }
    }
  }

  @Benchmark
  public void discardOne(DiscardOneState s, Blackhole bh) {
    s.stack.discard();
    bh.consume(s.stack);
  }

  /* ---------- State for pop() ---------- */
  @State(Scope.Benchmark)
  public static class PopState {
    ShortStack stack = new ShortStack(1024);
    {
      for (int i = 0; i < 512; i++) {
        stack.push((short) i);
      }
    }
  }

  @Benchmark
  public short pop(PopState s) {
    return s.stack.pop();
  }

  /* ---------- State for peek() ---------- */
  @State(Scope.Benchmark)
  public static class PeekState {
    ShortStack stack = new ShortStack(1024);
    {
      for (int i = 0; i < 512; i++) {
        stack.push((short) i);
      }
    }
  }

  @Benchmark
  public short peek(PeekState s) {
    return s.stack.peek();
  }

  /* ---------- State for clone() ---------- */
  @State(Scope.Benchmark)
  public static class CloneState {
    ShortStack stack = new ShortStack(1024);
    {
      for (int i = 0; i < 256; i++) {
        stack.push((short) i);
      }
    }
  }

  @Benchmark
  public ShortStack cloneStack(CloneState s) {
    return s.stack.clone();
  }

  /* ---------- State for static from(short...) ---------- */
  @State(Scope.Benchmark)
  public static class FromFactoryState {
    short[] elements = new short[128];
    {
      Random rnd = new Random(4);
      for (int i = 0; i < elements.length; i++) {
        elements[i] = (short) rnd.nextInt(Short.MAX_VALUE + 1);
      }
    }
  }

  @Benchmark
  public ShortStack fromFactory(FromFactoryState s) {
    return ShortStack.from(s.elements);
  }

  /* ---------- State for size() (read‑only) ---------- */
  @State(Scope.Benchmark)
  public static class SizeState {
    ShortStack stack = new ShortStack(1024);
    {
      for (int i = 0; i < 500; i++) {
        stack.push((short) i);
      }
    }
  }

  @Benchmark
  public int size(SizeState s) {
    return s.stack.size();
  }
}
