package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.Random;
import com.carrotsearch.hppc.ObjectLongHashMap;
import com.carrotsearch.hppc.LongCollection;
import com.carrotsearch.hppc.procedures.ObjectLongProcedure;
import com.carrotsearch.hppc.predicates.ObjectLongPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectLongHashMapBenchmark {

  private static final int ELEMENT_COUNT = 1024;

  // ---------- Read‑only state ----------
  @State(Scope.Benchmark)
  public static class ReadState {
    ObjectLongHashMap<String> map;
    String[] keys;
    long[] values;
    String lookupKey;
    long lookupValue;

    @Setup(Level.Trial)
    public void setUp() {
      Random rnd = new Random(0);
      map = new ObjectLongHashMap<>(ELEMENT_COUNT);
      keys = new String[ELEMENT_COUNT];
      values = new long[ELEMENT_COUNT];
      for (int i = 0; i < ELEMENT_COUNT; i++) {
        String k = "key" + i;
        long v = rnd.nextLong();
        keys[i] = k;
        values[i] = v;
        map.put(k, v);
      }
      lookupKey = keys[ELEMENT_COUNT / 2];
      lookupValue = values[ELEMENT_COUNT / 2];
    }
  }

  // ---------- State for mutating put ----------
  @State(Scope.Thread)
  public static class PutState {
    ObjectLongHashMap<String> map;
    String key;
    long value;

    @Setup(Level.Invocation)
    public void setUp() {
      map = new ObjectLongHashMap<>(16);
      key = "newKey";
      value = 12345L;
    }
  }

  // ---------- State for mutating remove ----------
  @State(Scope.Thread)
  public static class RemoveState {
    ObjectLongHashMap<String> map;
    String key;

    @Setup(Level.Invocation)
    public void setUp() {
      map = new ObjectLongHashMap<>(16);
      key = "toRemove";
      map.put(key, 99L);
    }
  }

  // ---------- State for clear ----------
  @State(Scope.Thread)
  public static class ClearState {
    ObjectLongHashMap<String> map;

    @Setup(Level.Invocation)
    public void setUp() {
      map = new ObjectLongHashMap<>(ELEMENT_COUNT);
      for (int i = 0; i < ELEMENT_COUNT; i++) {
        map.put("c" + i, i);
      }
    }
  }

  // ---------- State for equals ----------
  @State(Scope.Benchmark)
  public static class EqualsState {
    ObjectLongHashMap<String> mapA;
    ObjectLongHashMap<String> mapB;

    @Setup(Level.Trial)
    public void setUp() {
      mapA = new ObjectLongHashMap<>(ELEMENT_COUNT);
      mapB = new ObjectLongHashMap<>(ELEMENT_COUNT);
      for (int i = 0; i < ELEMENT_COUNT; i++) {
        String k = "eq" + i;
        long v = i * 2L;
        mapA.put(k, v);
        mapB.put(k, v);
      }
    }
  }

  // ---------- Benchmark methods ----------
  @Benchmark
  public long benchmarkPut(PutState s) {
    return s.map.put(s.key, s.value);
  }

  @Benchmark
  public long benchmarkGet(ReadState s) {
    return s.map.get(s.lookupKey);
  }

  @Benchmark
  public long benchmarkGetOrDefault(ReadState s) {
    return s.map.getOrDefault(s.lookupKey, -1L);
  }

  @Benchmark
  public boolean benchmarkContainsKey(ReadState s) {
    return s.map.containsKey(s.lookupKey);
  }

  @Benchmark
  public int benchmarkSize(ReadState s) {
    return s.map.size();
  }

  @Benchmark
  public boolean benchmarkIsEmpty(ReadState s) {
    return s.map.isEmpty();
  }

  @Benchmark
  public void benchmarkClear(ClearState s) {
    s.map.clear();
  }

  @Benchmark
  public long benchmarkRemove(RemoveState s) {
    return s.map.remove(s.key);
  }

  @Benchmark
  public int benchmarkHashCode(ReadState s) {
    return s.map.hashCode();
  }

  @Benchmark
  public boolean benchmarkEquals(EqualsState s) {
    return s.mapA.equals(s.mapB);
  }

  @Benchmark
  public void benchmarkIterator(ReadState s, Blackhole bh) {
    Iterator<?> it = s.map.iterator();
    while (it.hasNext()) {
      bh.consume(it.next());
    }
  }

  @Benchmark
  public int benchmarkKeysSize(ReadState s) {
    return s.map.keys().size();
  }

  @Benchmark
  public int benchmarkValuesSize(ReadState s) {
    return s.map.values().size();
  }

  @Benchmark
  public boolean benchmarkValuesContains(ReadState s) {
    return s.map.values().contains(s.lookupValue);
  }

  @Benchmark
  public void benchmarkForEachProcedure(ReadState s) {
    s.map.forEach(new ObjectLongProcedure<String>() {
      @Override
      public void apply(String key, long value) {
        // no‑op
      }
    });
  }

  @Benchmark
  public void benchmarkForEachPredicate(ReadState s) {
    s.map.forEach(new ObjectLongPredicate<String>() {
      @Override
      public boolean apply(String key, long value) {
        return true; // continue iteration
      }
    });
  }

  @Benchmark
  public long benchmarkRamBytesAllocated(ReadState s) {
    return s.map.ramBytesAllocated();
  }

  @Benchmark
  public long benchmarkRamBytesUsed(ReadState s) {
    return s.map.ramBytesUsed();
  }
}
