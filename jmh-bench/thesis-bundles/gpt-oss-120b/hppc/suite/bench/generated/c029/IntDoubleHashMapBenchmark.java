package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntDoubleHashMap;
import com.carrotsearch.hppc.cursors.IntDoubleCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.procedures.IntDoubleProcedure;
import com.carrotsearch.hppc.predicates.IntDoublePredicate;
import com.carrotsearch.hppc.predicates.IntPredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntDoubleHashMapBenchmark {

  /** State for read‑only operations. */
  @State(Scope.Benchmark)
  public static class ReadOnlyState {
    IntDoubleHashMap map;
    int existingKey;
    int missingKey;

    @Setup(Level.Trial)
    public void setUp() {
      int size = 1024;
      map = new IntDoubleHashMap(size);
      Random rnd = new Random(0);
      for (int i = 0; i < size; i++) {
        int key = rnd.nextInt();
        double value = rnd.nextDouble();
        map.put(key, value);
        if (i == size / 2) {
          existingKey = key;
        }
      }
      // a key guaranteed not to be present
      missingKey = Integer.MAX_VALUE;
    }
  }

  /** State for mutating operations – a fresh clone per invocation. */
  @State(Scope.Benchmark)
  public static class MutatingState {
    IntDoubleHashMap baseMap;
    IntDoubleHashMap map; // clone used in the benchmark
    int existingKey;
    int putKey;

    @Setup(Level.Trial)
    public void setUpTrial() {
      int size = 1024;
      baseMap = new IntDoubleHashMap(size);
      Random rnd = new Random(1);
      for (int i = 0; i < size; i++) {
        int key = rnd.nextInt();
        double value = rnd.nextDouble();
        baseMap.put(key, value);
        if (i == size / 2) {
          existingKey = key;
        }
      }
      putKey = Integer.MAX_VALUE; // not present in the base map
    }

    @Setup(Level.Invocation)
    public void setUpInvocation() {
      map = baseMap.clone();
    }
  }

  // -------------------------------------------------------------------------
  // Read‑only benchmarks
  // -------------------------------------------------------------------------

  @Benchmark
  public double benchGet(ReadOnlyState s) {
    return s.map.get(s.existingKey);
  }

  @Benchmark
  public double benchGetOrDefault(ReadOnlyState s) {
    return s.map.getOrDefault(s.missingKey, -1.0);
  }

  @Benchmark
  public boolean benchContainsKey(ReadOnlyState s) {
    return s.map.containsKey(s.existingKey);
  }

  @Benchmark
  public int benchSize(ReadOnlyState s) {
    return s.map.size();
  }

  @Benchmark
  public boolean benchIsEmpty(ReadOnlyState s) {
    return s.map.isEmpty();
  }

  @Benchmark
  public int benchIterateEntries(ReadOnlyState s, Blackhole bh) {
    int count = 0;
    for (IntDoubleCursor c : s.map) {
      bh.consume(c.key);
      bh.consume(c.value);
      count++;
    }
    return count;
  }

  @Benchmark
  public double benchForEachProcedure(ReadOnlyState s) {
    final double[] sum = new double[1];
    s.map.forEach((IntDoubleProcedure) (k, v) -> sum[0] += v);
    return sum[0];
  }

  @Benchmark
  public double benchForEachPredicate(ReadOnlyState s) {
    final double[] sum = new double[1];
    s.map.forEach((IntDoublePredicate) (k, v) -> {
      sum[0] += v;
      return true;
    });
    return sum[0];
  }

  @Benchmark
  public int benchKeysIteration(ReadOnlyState s) {
    int cnt = 0;
    for (IntCursor c : s.map.keys()) {
      cnt++;
    }
    return cnt;
  }

  @Benchmark
  public int benchValuesIteration(ReadOnlyState s, Blackhole bh) {
    int cnt = 0;
    for (DoubleCursor c : s.map.values()) {
      bh.consume(c.value);
      cnt++;
    }
    return cnt;
  }

  // -------------------------------------------------------------------------
  // Mutating benchmarks
  // -------------------------------------------------------------------------

  @Benchmark
  public double benchPut(MutatingState s) {
    return s.map.put(s.putKey, 3.1415);
  }

  @Benchmark
  public double benchRemove(MutatingState s) {
    return s.map.remove(s.existingKey);
  }

  @Benchmark
  public void benchClear(MutatingState s, Blackhole bh) {
    s.map.clear();
    bh.consume(s.map.size());
  }

  @Benchmark
  public double benchPutOrAdd(MutatingState s) {
    return s.map.putOrAdd(s.existingKey, 2.0, 5.0);
  }

  @Benchmark
  public double benchAddTo(MutatingState s) {
    return s.map.addTo(s.existingKey, 1.0);
  }

  @Benchmark
  public int benchRemoveAllPredicate(MutatingState s) {
    // Remove entries with value less than 0.5
    return s.map.removeAll((IntDoublePredicate) (k, v) -> v < 0.5);
  }

  @Benchmark
  public int benchRemoveAllIntPredicate(MutatingState s) {
    // Remove entries with odd keys
    return s.map.removeAll((IntPredicate) k -> (k & 1) != 0);
  }
}
