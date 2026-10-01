package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectDoubleHashMap;
import com.carrotsearch.hppc.cursors.ObjectDoubleCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.procedures.ObjectDoubleProcedure;
import com.carrotsearch.hppc.predicates.ObjectDoublePredicate;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectDoubleHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class FilledState {
        ObjectDoubleHashMap<String> map;
        String[] keys;
        double[] values;
        final int size = 1024;

        @Setup(Level.Trial)
        public void setUp() {
            map = new ObjectDoubleHashMap<>(size);
            keys = new String[size];
            values = new double[size];
            for (int i = 0; i < size; i++) {
                keys[i] = "k" + i;
                values[i] = i * 1.0;
                map.put(keys[i], values[i]);
            }
        }
    }

    @State(Scope.Benchmark)
    public static class FreshState {
        ObjectDoubleHashMap<String> map;
        final String key = "newKey";
        final double value = 123.45;
        final String existingKey = "existing";
        final double existingValue = 99.9;

        @Setup(Level.Invocation)
        public void setUp() {
            map = new ObjectDoubleHashMap<>(16);
            map.put(existingKey, existingValue);
        }
    }

    @Benchmark
    public double benchPut(FreshState s) {
        return s.map.put(s.key, s.value);
    }

    @Benchmark
    public double benchGet(FilledState s) {
        return s.map.get(s.keys[0]);
    }

    @Benchmark
    public double benchGetOrDefault(FilledState s) {
        return s.map.getOrDefault(s.keys[0], -1.0);
    }

    @Benchmark
    public boolean benchContainsKey(FilledState s) {
        return s.map.containsKey(s.keys[0]);
    }

    @Benchmark
    public double benchRemove(FreshState s) {
        return s.map.remove(s.existingKey);
    }

    @Benchmark
    public double benchPutOrAdd(FreshState s) {
        return s.map.putOrAdd(s.existingKey, 5.0, 2.0);
    }

    @Benchmark
    public double benchAddTo(FreshState s) {
        return s.map.addTo(s.existingKey, 3.0);
    }

    @Benchmark
    public int benchIndexOf(FilledState s) {
        return s.map.indexOf(s.keys[0]);
    }

    @Benchmark
    public double benchIndexGet(FilledState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexGet(idx);
    }

    @Benchmark
    public double benchIndexReplace(FilledState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexReplace(idx, 777.7);
    }

    @Benchmark
    public void benchIndexInsert(FreshState s, Blackhole bh) {
        int idx = s.map.indexOf(s.key);
        s.map.indexInsert(idx, s.key, s.value);
        bh.consume(s.map);
    }

    @Benchmark
    public double benchIndexRemove(FilledState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexRemove(idx);
    }

    @Benchmark
    public void benchClear(FilledState s, Blackhole bh) {
        s.map.clear();
        bh.consume(s.map);
    }

    @Benchmark
    public int benchSize(FilledState s) {
        return s.map.size();
    }

    @Benchmark
    public boolean benchIsEmpty(FilledState s) {
        return s.map.isEmpty();
    }

    @Benchmark
    public ObjectDoubleProcedure<String> benchForEachProcedure(FilledState s) {
        ObjectDoubleProcedure<String> proc = (k, v) -> { };
        return s.map.forEach(proc);
    }

    @Benchmark
    public ObjectDoublePredicate<String> benchForEachPredicate(FilledState s) {
        ObjectDoublePredicate<String> pred = (k, v) -> true;
        return s.map.forEach(pred);
    }

    @Benchmark
    public boolean benchKeysContains(FilledState s) {
        return s.map.keys().contains(s.keys[0]);
    }

    @Benchmark
    public boolean benchValuesContains(FilledState s) {
        return s.map.values().contains(s.values[0]);
    }

    @Benchmark
    public ObjectDoubleCursor<String> benchIteratorNext(FilledState s) {
        Iterator<ObjectDoubleCursor<String>> it = s.map.iterator();
        return it.next();
    }

    @Benchmark
    public ObjectDoubleHashMap<String> benchClone(FilledState s) {
        return s.map.clone();
    }

    @Benchmark
    public String benchToString(FilledState s) {
        return s.map.toString();
    }

    @Benchmark
    public int benchHashCode(FilledState s) {
        return s.map.hashCode();
    }

    @Benchmark
    public boolean benchEquals(FilledState s) {
        return s.map.equals(s.map.clone());
    }

    @Benchmark
    public void benchEnsureCapacity(FilledState s, Blackhole bh) {
        s.map.ensureCapacity(2048);
        bh.consume(s.map);
    }

    @Benchmark
    public long benchRamBytesUsed(FilledState s) {
        return s.map.ramBytesUsed();
    }

    @Benchmark
    public long benchRamBytesAllocated(FilledState s) {
        return s.map.ramBytesAllocated();
    }

    @Benchmark
    public String benchVisualizeKeyDistribution(FilledState s) {
        return s.map.visualizeKeyDistribution(10);
    }

    @Benchmark
    public int benchFromFactory(FilledState s) {
        ObjectDoubleHashMap<String> m = ObjectDoubleHashMap.from(s.keys, s.values);
        return m.size();
    }
}
