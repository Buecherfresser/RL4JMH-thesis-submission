package bench.generated.c086;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.SortedIterationCharIntHashMap;
import com.carrotsearch.hppc.CharIntHashMap;
import com.carrotsearch.hppc.cursors.CharIntCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.CharIntProcedure;
import com.carrotsearch.hppc.predicates.CharIntPredicate;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.comparators.CharIntComparator;
import com.carrotsearch.hppc.CharCollection;
import com.carrotsearch.hppc.IntContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharIntHashMapBenchmark {

  private CharIntHashMap delegate;
  private SortedIterationCharIntHashMap mapByKey;
  private SortedIterationCharIntHashMap mapByKeyValue;
  private CharComparator keyComparator;
  private CharIntComparator keyValueComparator;
  private CharIntProcedure proc;
  private CharIntPredicate pred;
  private int sampleKey;
  private int sampleIndex;

  @Setup(Level.Trial)
  public void setup() {
    Random rnd = new Random(12345L);
    delegate = new CharIntHashMap();
    int size = 1024;
    for (int i = 0; i < size; i++) {
      char k = (char) rnd.nextInt(Character.MAX_VALUE + 1);
      delegate.put(k, i);
    }
    sampleKey = 0;
    for (char k : delegate.keys) {
      if (k != 0) {
        sampleKey = k;
        break;
      }
    }
    sampleIndex = 0;

    keyComparator = (a, b) -> Character.compare(a, b);
    keyValueComparator = (ka, va, kb, vb) -> {
      int cmp = Character.compare(ka, kb);
      if (cmp != 0) return cmp;
      return Integer.compare(va, vb);
    };

    mapByKey = new SortedIterationCharIntHashMap(delegate, keyComparator);
    mapByKeyValue = new SortedIterationCharIntHashMap(delegate, keyValueComparator);

    proc = (k, v) -> {
      // no‑op
    };
    pred = (k, v) -> true;
  }

  @Benchmark
  public int benchmarkSize() {
    return mapByKey.size();
  }

  @Benchmark
  public boolean benchmarkIsEmpty() {
    return mapByKey.isEmpty();
  }

  @Benchmark
  public boolean benchmarkContainsKey() {
    return mapByKey.containsKey((char) sampleKey);
  }

  @Benchmark
  public int benchmarkGet() {
    return mapByKey.get((char) sampleKey);
  }

  @Benchmark
  public int benchmarkGetOrDefault() {
    return mapByKey.getOrDefault((char) sampleKey, -1);
  }

  @Benchmark
  public void benchmarkIterator(Blackhole bh) {
    for (CharIntCursor c : mapByKey) {
      bh.consume(c.key);
      bh.consume(c.value);
    }
  }

  @Benchmark
  public CharIntProcedure benchmarkForEachProcedure() {
    return mapByKey.forEach(proc);
  }

  @Benchmark
  public CharIntPredicate benchmarkForEachPredicate() {
    return mapByKey.forEach(pred);
  }

  @Benchmark
  public void benchmarkKeysIteration(Blackhole bh) {
    CharCollection keys = mapByKey.keys();
    for (CharCursor c : keys) {
      bh.consume(c.value);
    }
  }

  @Benchmark
  public void benchmarkValuesIteration(Blackhole bh) {
    IntContainer values = mapByKey.values();
    for (IntCursor c : values) {
      bh.consume(c.value);
    }
  }

  @Benchmark
  public int benchmarkIndexOf() {
    return mapByKey.indexOf((char) sampleKey);
  }

  @Benchmark
  public boolean benchmarkIndexExists() {
    return mapByKey.indexExists(sampleIndex);
  }

  @Benchmark
  public int benchmarkIndexGet() {
    return mapByKey.indexGet(sampleIndex);
  }

  @Benchmark
  public String benchmarkVisualizeKeyDistribution() {
    return mapByKey.visualizeKeyDistribution(10);
  }

  @Benchmark
  public int benchmarkSizeByKeyValue() {
    return mapByKeyValue.size();
  }

  @Benchmark
  public void benchmarkIteratorByKeyValue(Blackhole bh) {
    for (CharIntCursor c : mapByKeyValue) {
      bh.consume(c.key);
      bh.consume(c.value);
    }
  }

  @Benchmark
  public CharIntProcedure benchmarkForEachProcedureByKeyValue() {
    return mapByKeyValue.forEach(proc);
  }

  @Benchmark
  public CharIntPredicate benchmarkForEachPredicateByKeyValue() {
    return mapByKeyValue.forEach(pred);
  }
}
