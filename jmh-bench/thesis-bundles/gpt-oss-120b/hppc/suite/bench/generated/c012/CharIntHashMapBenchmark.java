package bench.generated.c012;

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
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.CharIntHashMap;
import com.carrotsearch.hppc.cursors.CharIntCursor;
import com.carrotsearch.hppc.procedures.CharIntProcedure;
import com.carrotsearch.hppc.procedures.CharProcedure;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.predicates.CharIntPredicate;
import com.carrotsearch.hppc.predicates.CharPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharIntHashMapBenchmark {

  private CharIntHashMap readOnlyMap;
  private CharIntHashMap[] mutableMaps;
  private int poolIndex;
  private char[] keys;
  private int[] values;
  private int size;
  private Random random;

  @Setup
  public void setUp() {
    size = 1024;
    random = new Random(0x1234abcdL);
    keys = new char[size];
    values = new int[size];
    for (int i = 0; i < size; i++) {
      keys[i] = (char) (i + 1); // avoid the special 0 key
      values[i] = i;
    }

    readOnlyMap = new CharIntHashMap(size);
    for (int i = 0; i < size; i++) {
      readOnlyMap.put(keys[i], values[i]);
    }

    int poolSize = 8;
    mutableMaps = new CharIntHashMap[poolSize];
    for (int i = 0; i < poolSize; i++) {
      mutableMaps[i] = readOnlyMap.clone();
    }
    poolIndex = 0;
  }

  private CharIntHashMap nextMutableMap() {
    CharIntHashMap map = mutableMaps[poolIndex];
    poolIndex = (poolIndex + 1) & (mutableMaps.length - 1);
    return map;
  }

  @Benchmark
  public int getExisting() {
    return readOnlyMap.get(keys[0]);
  }

  @Benchmark
  public int getNonExisting() {
    return readOnlyMap.get((char) 0);
  }

  @Benchmark
  public boolean containsExisting() {
    return readOnlyMap.containsKey(keys[size / 2]);
  }

  @Benchmark
  public int putNew() {
    CharIntHashMap map = nextMutableMap();
    char newKey = (char) (size + 1);
    return map.put(newKey, 12345);
  }

  @Benchmark
  public int putExisting() {
    CharIntHashMap map = nextMutableMap();
    char existingKey = keys[0];
    return map.put(existingKey, 999);
  }

  @Benchmark
  public int removeExisting() {
    CharIntHashMap map = nextMutableMap();
    char key = keys[0];
    return map.remove(key);
  }

  @Benchmark
  public int putOrAddExisting() {
    CharIntHashMap map = nextMutableMap();
    char key = keys[0];
    return map.putOrAdd(key, 10, 5);
  }

  @Benchmark
  public int putOrAddNew() {
    CharIntHashMap map = nextMutableMap();
    char key = (char) (size + 2);
    return map.putOrAdd(key, 10, 5);
  }

  @Benchmark
  public int addToExisting() {
    CharIntHashMap map = nextMutableMap();
    char key = keys[0];
    return map.addTo(key, 3);
  }

  @Benchmark
  public int indexOfExisting() {
    return readOnlyMap.indexOf(keys[0]);
  }

  @Benchmark
  public int indexGetExisting() {
    int idx = readOnlyMap.indexOf(keys[0]);
    return readOnlyMap.indexGet(idx);
  }

  @Benchmark
  public int indexReplaceExisting() {
    CharIntHashMap map = nextMutableMap();
    int idx = map.indexOf(keys[0]);
    return map.indexReplace(idx, 777);
  }

  @Benchmark
  public int indexInsertNew() {
    CharIntHashMap map = nextMutableMap();
    char newKey = (char) (size + 3);
    int idx = map.indexOf(newKey); // negative
    map.indexInsert(idx, newKey, 555);
    return map.size();
  }

  @Benchmark
  public int indexRemoveExisting() {
    CharIntHashMap map = nextMutableMap();
    int idx = map.indexOf(keys[0]);
    return map.indexRemove(idx);
  }

  @Benchmark
  public void clearMap(Blackhole bh) {
    CharIntHashMap map = nextMutableMap();
    map.clear();
    bh.consume(map.size());
  }

  @Benchmark
  public int sizeReadOnly() {
    return readOnlyMap.size();
  }

  @Benchmark
  public int forEachProcedure() {
    SumProcedure proc = new SumProcedure();
    readOnlyMap.forEach(proc);
    return proc.sum;
  }

  @Benchmark
  public int forEachPredicate() {
    CountPredicate pred = new CountPredicate();
    readOnlyMap.forEach(pred);
    return pred.count;
  }

  @Benchmark
  public int iterateViaIterator() {
    int sum = 0;
    for (CharIntCursor c : readOnlyMap) {
      sum += c.value;
    }
    return sum;
  }

  @Benchmark
  public int keysForEach() {
    SumCharProcedure proc = new SumCharProcedure();
    readOnlyMap.keys().forEach(proc);
    return proc.sum;
  }

  @Benchmark
  public int valuesForEach() {
    SumIntProcedure proc = new SumIntProcedure();
    readOnlyMap.values().forEach(proc);
    return proc.sum;
  }

  @Benchmark
  public int removeAllCharPredicate() {
    CharIntHashMap map = nextMutableMap();
    CharPredicate pred = new CharPredicate() {
      @Override
      public boolean apply(char key) {
        return (key & 1) == 0; // remove even keys
      }
    };
    return map.removeAll(pred);
  }

  // Helper procedure that sums int values.
  private static class SumProcedure implements CharIntProcedure {
    int sum = 0;

    @Override
    public void apply(char key, int value) {
      sum += value;
    }
  }

  // Predicate that counts how many entries are visited.
  private static class CountPredicate implements CharIntPredicate {
    int count = 0;

    @Override
    public boolean apply(char key, int value) {
      count++;
      return true; // continue iteration
    }
  }

  // Procedure for keys view.
  private static class SumCharProcedure implements CharProcedure {
    int sum = 0;

    @Override
    public void apply(char value) {
      sum += value;
    }
  }

  // Procedure for values view.
  private static class SumIntProcedure implements IntProcedure {
    int sum = 0;

    @Override
    public void apply(int value) {
      sum += value;
    }
  }
}
