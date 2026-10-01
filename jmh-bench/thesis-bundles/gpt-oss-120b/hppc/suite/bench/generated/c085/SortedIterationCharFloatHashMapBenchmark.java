package bench.generated.c085;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationCharFloatHashMap;
import com.carrotsearch.hppc.CharFloatHashMap;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.comparators.CharFloatComparator;
import com.carrotsearch.hppc.procedures.CharFloatProcedure;
import com.carrotsearch.hppc.predicates.CharFloatPredicate;
import com.carrotsearch.hppc.cursors.CharFloatCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.CharCollection;
import com.carrotsearch.hppc.FloatContainer;
import java.util.Iterator;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharFloatHashMapBenchmark {

  private CharFloatHashMap delegate;
  private SortedIterationCharFloatHashMap sortedByKey;
  private SortedIterationCharFloatHashMap sortedByKeyValue;
  private CharComparator keyComparator;
  private CharFloatComparator keyValueComparator;
  private CharFloatProcedure dummyProcedure;
  private CharFloatPredicate alwaysTruePredicate;
  private char existingKey;
  private char missingKey;
  private float defaultValue;

  @Setup(Level.Trial)
  public void setup() {
    int mapSize = 1024;
    delegate = new CharFloatHashMap(mapSize);
    Random rnd = new Random(12345L);
    for (int i = 0; i < mapSize; i++) {
      char key = (char) rnd.nextInt(Character.MAX_VALUE + 1);
      float value = rnd.nextFloat();
      delegate.put(key, value);
      if (i == 0) {
        existingKey = key;
      }
    }
    missingKey = (char) (existingKey + 1);
    defaultValue = -1.0f;

    keyComparator = (a, b) -> Character.compare(a, b);
    keyValueComparator = (ka, va, kb, vb) -> {
      int cmp = Character.compare(ka, kb);
      if (cmp != 0) return cmp;
      return Float.compare(va, vb);
    };

    sortedByKey = new SortedIterationCharFloatHashMap(delegate, keyComparator);
    sortedByKeyValue = new SortedIterationCharFloatHashMap(delegate, keyValueComparator);

    dummyProcedure = (k, v) -> {
      // no-op
    };
    alwaysTruePredicate = (k, v) -> true;
  }

  @Benchmark
  public int benchmarkSize() {
    return sortedByKey.size();
  }

  @Benchmark
  public boolean benchmarkIsEmpty() {
    return sortedByKey.isEmpty();
  }

  @Benchmark
  public boolean benchmarkContainsKey() {
    return sortedByKey.containsKey(existingKey);
  }

  @Benchmark
  public float benchmarkGet() {
    return sortedByKey.get(existingKey);
  }

  @Benchmark
  public float benchmarkGetOrDefault() {
    return sortedByKey.getOrDefault(missingKey, defaultValue);
  }

  @Benchmark
  public void benchmarkIterator(Blackhole bh) {
    Iterator<CharFloatCursor> it = sortedByKey.iterator();
    if (it.hasNext()) {
      bh.consume(it.next());
    }
  }

  @Benchmark
  public CharCollection benchmarkKeys() {
    return sortedByKey.keys();
  }

  @Benchmark
  public FloatContainer benchmarkValues() {
    return sortedByKey.values();
  }

  @Benchmark
  public void benchmarkKeysIterator(Blackhole bh) {
    Iterator<CharCursor> it = sortedByKey.keys().iterator();
    if (it.hasNext()) {
      bh.consume(it.next());
    }
  }

  @Benchmark
  public void benchmarkValuesIterator(Blackhole bh) {
    Iterator<FloatCursor> it = sortedByKey.values().iterator();
    if (it.hasNext()) {
      bh.consume(it.next());
    }
  }

  @Benchmark
  public CharFloatProcedure benchmarkForEachProcedure() {
    return sortedByKey.forEach(dummyProcedure);
  }

  @Benchmark
  public CharFloatPredicate benchmarkForEachPredicate() {
    return sortedByKey.forEach(alwaysTruePredicate);
  }

  @Benchmark
  public int benchmarkIndexOf() {
    return sortedByKey.indexOf(existingKey);
  }

  @Benchmark
  public boolean benchmarkIndexExists() {
    int idx = sortedByKey.indexOf(existingKey);
    return sortedByKey.indexExists(idx);
  }

  @Benchmark
  public float benchmarkIndexGet() {
    int idx = sortedByKey.indexOf(existingKey);
    return sortedByKey.indexGet(idx);
  }

  @Benchmark
  public String benchmarkVisualizeKeyDistribution() {
    return sortedByKey.visualizeKeyDistribution(10);
  }

  @Benchmark
  public SortedIterationCharFloatHashMap benchmarkCreateByKey() {
    return new SortedIterationCharFloatHashMap(delegate, keyComparator);
  }

  @Benchmark
  public SortedIterationCharFloatHashMap benchmarkCreateByKeyValue() {
    return new SortedIterationCharFloatHashMap(delegate, keyValueComparator);
  }
}
