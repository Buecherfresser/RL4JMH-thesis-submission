package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.ObjectFloatIdentityHashMap;
import com.carrotsearch.hppc.cursors.ObjectFloatCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectFloatIdentityHashMapBenchmark {

  /** State with a pre‑filled map used for read‑only operations. */
  @State(Scope.Benchmark)
  public static class ReadOnlyState {
    ObjectFloatIdentityHashMap<Object> map;
    Object[] keys;
    float[] values;

    @Setup(Level.Trial)
    public void setUp() {
      int size = 1024;
      keys = new Object[size];
      values = new float[size];
      Random rnd = new Random(12345L);
      for (int i = 0; i < size; i++) {
        keys[i] = new Object();
        values[i] = rnd.nextFloat();
      }
      map = new ObjectFloatIdentityHashMap<>(size);
      for (int i = 0; i < size; i++) {
        map.put(keys[i], values[i]);
      }
    }
  }

  /** State with a pre‑filled map used for mutating operations. */
  @State(Scope.Benchmark)
  public static class MutatingState {
    ObjectFloatIdentityHashMap<Object> map;
    Object[] keys;
    float[] values;

    @Setup(Level.Trial)
    public void setUp() {
      int size = 1024;
      keys = new Object[size];
      values = new float[size];
      Random rnd = new Random(54321L);
      for (int i = 0; i < size; i++) {
        keys[i] = new Object();
        values[i] = rnd.nextFloat();
      }
      map = new ObjectFloatIdentityHashMap<>(size);
      for (int i = 0; i < size; i++) {
        map.put(keys[i], values[i]);
      }
    }
  }

  /** State holding a second map for putAll benchmarks. */
  @State(Scope.Benchmark)
  public static class OtherState {
    ObjectFloatIdentityHashMap<Object> otherMap;

    @Setup(Level.Trial)
    public void setUp() {
      int size = 256;
      otherMap = new ObjectFloatIdentityHashMap<>(size);
      Random rnd = new Random(999L);
      for (int i = 0; i < size; i++) {
        Object k = new Object();
        float v = rnd.nextFloat();
        otherMap.put(k, v);
      }
    }
  }

  // -------------------------------------------------------------------------
  // Read‑only benchmarks
  // -------------------------------------------------------------------------

  @Benchmark
  public float getExisting(ReadOnlyState s) {
    return s.map.get(s.keys[0]);
  }

  @Benchmark
  public float getOrDefaultExisting(ReadOnlyState s) {
    return s.map.getOrDefault(s.keys[0], -1.0f);
  }

  @Benchmark
  public boolean containsKeyExisting(ReadOnlyState s) {
    return s.map.containsKey(s.keys[0]);
  }

  @Benchmark
  public int size(ReadOnlyState s) {
    return s.map.size();
  }

  @Benchmark
  public int indexOfExisting(ReadOnlyState s) {
    return s.map.indexOf(s.keys[0]);
  }

  @Benchmark
  public boolean indexExistsExisting(ReadOnlyState s) {
    int idx = s.map.indexOf(s.keys[0]);
    return s.map.indexExists(idx);
  }

  @Benchmark
  public float indexGetExisting(ReadOnlyState s) {
    int idx = s.map.indexOf(s.keys[0]);
    return s.map.indexGet(idx);
  }

  @Benchmark
  public void iterate(ReadOnlyState s, Blackhole bh) {
    for (ObjectFloatCursor<Object> c : s.map) {
      bh.consume(c.key);
      bh.consume(c.value);
    }
  }

  // -------------------------------------------------------------------------
  // Mutating benchmarks
  // -------------------------------------------------------------------------

  @Benchmark
  public float putNewKey(MutatingState s) {
    Object newKey = new Object();
    return s.map.put(newKey, 1.23f);
  }

  @Benchmark
  public float putOrAddExisting(MutatingState s) {
    return s.map.putOrAdd(s.keys[0], 1.0f, 2.0f);
  }

  @Benchmark
  public float addToExisting(MutatingState s) {
    return s.map.addTo(s.keys[0], 0.5f);
  }

  @Benchmark
  public float removeExisting(MutatingState s) {
    return s.map.remove(s.keys[0]);
  }

  @Benchmark
  public void clearMap(MutatingState s, Blackhole bh) {
    s.map.clear();
    bh.consume(s.map.size());
  }

  @Benchmark
  public int putAllFromOther(MutatingState s, OtherState o) {
    s.map.putAll(o.otherMap);
    return s.map.size();
  }

  @Benchmark
  public void indexInsert(MutatingState s, Blackhole bh) {
    int idx = s.map.indexOf(s.keys[0]);
    s.map.indexInsert(idx, new Object(), 3.14f);
    bh.consume(s.map.size());
  }

  @Benchmark
  public float indexReplace(MutatingState s) {
    int idx = s.map.indexOf(s.keys[0]);
    return s.map.indexReplace(idx, 2.71f);
  }
}
