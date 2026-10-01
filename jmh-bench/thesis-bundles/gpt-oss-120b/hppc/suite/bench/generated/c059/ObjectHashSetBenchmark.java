package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectHashSet;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import java.util.List;
import java.util.ArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectHashSetBenchmark {

  private static List<String> generateStrings(int count, String prefix) {
    List<String> list = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      list.add(prefix + i);
    }
    return list;
  }

  @State(Scope.Benchmark)
  public static class AddState {
    @SuppressWarnings("unchecked")
    ObjectHashSet<String>[] pools;
    int idx;
    String newElement;

    @Setup(Level.Trial)
    public void setup() {
      int poolSize = 16;
      pools = new ObjectHashSet[poolSize];
      List<String> base = generateStrings(100, "base");
      newElement = "newElement";
      for (int i = 0; i < poolSize; i++) {
        ObjectHashSet<String> set = new ObjectHashSet<>(base.size() + 1);
        for (String s : base) {
          set.add(s);
        }
        pools[i] = set;
      }
    }

    ObjectHashSet<String> nextSet() {
      int i = idx;
      idx = (idx + 1) & (pools.length - 1);
      return pools[i];
    }
  }

  @Benchmark
  public boolean add(AddState s) {
    ObjectHashSet<String> set = s.nextSet();
    return set.add(s.newElement);
  }

  @State(Scope.Benchmark)
  public static class RemoveState {
    @SuppressWarnings("unchecked")
    ObjectHashSet<String>[] pools;
    int idx;
    String element;

    @Setup(Level.Trial)
    public void setup() {
      int poolSize = 16;
      pools = new ObjectHashSet[poolSize];
      element = "toRemove";
      List<String> base = generateStrings(100, "base");
      for (int i = 0; i < poolSize; i++) {
        ObjectHashSet<String> set = new ObjectHashSet<>(base.size() + 1);
        for (String s : base) {
          set.add(s);
        }
        set.add(element);
        pools[i] = set;
      }
    }

    ObjectHashSet<String> nextSet() {
      int i = idx;
      idx = (idx + 1) & (pools.length - 1);
      return pools[i];
    }
  }

  @Benchmark
  public boolean remove(RemoveState s) {
    ObjectHashSet<String> set = s.nextSet();
    return set.remove(s.element);
  }

  @State(Scope.Benchmark)
  public static class ContainsState {
    @SuppressWarnings("unchecked")
    ObjectHashSet<String>[] pools;
    int idx;
    String element;

    @Setup(Level.Trial)
    public void setup() {
      int poolSize = 16;
      pools = new ObjectHashSet[poolSize];
      element = "present";
      List<String> base = generateStrings(100, "base");
      for (int i = 0; i < poolSize; i++) {
        ObjectHashSet<String> set = new ObjectHashSet<>(base.size() + 1);
        for (String s : base) {
          set.add(s);
        }
        set.add(element);
        pools[i] = set;
      }
    }

    ObjectHashSet<String> nextSet() {
      int i = idx;
      idx = (idx + 1) & (pools.length - 1);
      return pools[i];
    }
  }

  @Benchmark
  public boolean contains(ContainsState s) {
    ObjectHashSet<String> set = s.nextSet();
    return set.contains(s.element);
  }

  @State(Scope.Benchmark)
  public static class ClearState {
    @SuppressWarnings("unchecked")
    ObjectHashSet<String>[] pools;
    int idx;

    @Setup(Level.Trial)
    public void setup() {
      int poolSize = 16;
      pools = new ObjectHashSet[poolSize];
      List<String> base = generateStrings(200, "base");
      for (int i = 0; i < poolSize; i++) {
        ObjectHashSet<String> set = new ObjectHashSet<>(base.size());
        for (String s : base) {
          set.add(s);
        }
        pools[i] = set;
      }
    }

    ObjectHashSet<String> nextSet() {
      int i = idx;
      idx = (idx + 1) & (pools.length - 1);
      return pools[i];
    }
  }

  @Benchmark
  public void clear(ClearState s, Blackhole bh) {
    ObjectHashSet<String> set = s.nextSet();
    set.clear();
    bh.consume(set.size());
  }

  @State(Scope.Benchmark)
  public static class EnsureCapacityState {
    @SuppressWarnings("unchecked")
    ObjectHashSet<String>[] pools;
    int idx;

    @Setup(Level.Trial)
    public void setup() {
      int poolSize = 8;
      pools = new ObjectHashSet[poolSize];
      List<String> base = generateStrings(150, "base");
      for (int i = 0; i < poolSize; i++) {
        ObjectHashSet<String> set = new ObjectHashSet<>(base.size());
        for (String s : base) {
          set.add(s);
        }
        pools[i] = set;
      }
    }

    ObjectHashSet<String> nextSet() {
      int i = idx;
      idx = (idx + 1) & (pools.length - 1);
      return pools[i];
    }
  }

  @Benchmark
  public int ensureCapacity(EnsureCapacityState s) {
    ObjectHashSet<String> set = s.nextSet();
    set.ensureCapacity(256);
    return set.size();
  }

  @State(Scope.Benchmark)
  public static class ToArrayState {
    @SuppressWarnings("unchecked")
    ObjectHashSet<String>[] pools;
    int idx;

    @Setup(Level.Trial)
    public void setup() {
      int poolSize = 8;
      pools = new ObjectHashSet[poolSize];
      List<String> base = generateStrings(120, "base");
      for (int i = 0; i < poolSize; i++) {
        ObjectHashSet<String> set = new ObjectHashSet<>(base.size());
        for (String s : base) {
          set.add(s);
        }
        pools[i] = set;
      }
    }

    ObjectHashSet<String> nextSet() {
      int i = idx;
      idx = (idx + 1) & (pools.length - 1);
      return pools[i];
    }
  }

  @Benchmark
  public int toArray(ToArrayState s) {
    ObjectHashSet<String> set = s.nextSet();
    Object[] arr = set.toArray();
    return arr.length;
  }

  @State(Scope.Benchmark)
  public static class IteratorState {
    @SuppressWarnings("unchecked")
    ObjectHashSet<String>[] pools;
    int idx;

    @Setup(Level.Trial)
    public void setup() {
      int poolSize = 8;
      pools = new ObjectHashSet[poolSize];
      List<String> base = generateStrings(80, "base");
      for (int i = 0; i < poolSize; i++) {
        ObjectHashSet<String> set = new ObjectHashSet<>(base.size());
        for (String s : base) {
          set.add(s);
        }
        pools[i] = set;
      }
    }

    ObjectHashSet<String> nextSet() {
      int i = idx;
      idx = (idx + 1) & (pools.length - 1);
      return pools[i];
    }
  }

  @Benchmark
  public void iterator(IteratorState s, Blackhole bh) {
    ObjectHashSet<String> set = s.nextSet();
    for (ObjectCursor<String> cursor : set) {
      bh.consume(cursor.value);
    }
  }
}
