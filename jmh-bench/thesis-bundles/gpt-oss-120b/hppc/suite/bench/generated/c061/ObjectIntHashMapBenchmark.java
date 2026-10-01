package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.procedures.ObjectIntProcedure;
import com.carrotsearch.hppc.predicates.ObjectIntPredicate;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.IntCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectIntHashMapBenchmark {

    private int size = 1024;
    private ObjectIntHashMap<String> map;
    private String[] keys;
    private int[] values;
    private int index = 0;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        keys = new String[size];
        values = new int[size];
        for (int i = 0; i < size; i++) {
            keys[i] = "key-" + i;
            values[i] = rnd.nextInt();
        }
        map = new ObjectIntHashMap<>(size);
        for (int i = 0; i < size; i++) {
            map.put(keys[i], values[i]);
        }
    }

    private int nextIndex() {
        int i = index;
        index = (index + 1) % size;
        return i;
    }

    @Benchmark
    public int putExisting() {
        int i = nextIndex();
        return map.put(keys[i], values[i] + 1);
    }

    @Benchmark
    public int getExisting() {
        int i = nextIndex();
        return map.get(keys[i]);
    }

    @Benchmark
    public int getOrDefaultExisting() {
        int i = nextIndex();
        return map.getOrDefault(keys[i], -1);
    }

    @Benchmark
    public int containsKey() {
        int i = nextIndex();
        return map.containsKey(keys[i]) ? 1 : 0;
    }

    @Benchmark
    public int removeNonExisting() {
        int i = nextIndex();
        return map.remove("nonexistent-" + i);
    }

    @Benchmark
    public int putOrAddExisting() {
        int i = nextIndex();
        return map.putOrAdd(keys[i], 1, 2);
    }

    @Benchmark
    public int addToExisting() {
        int i = nextIndex();
        return map.addTo(keys[i], 3);
    }

    @Benchmark
    public int forEachProcedure() {
        map.forEach(new NoOpProcedure());
        return map.size();
    }

    @Benchmark
    public int forEachPredicate() {
        map.forEach(new NoOpPredicate());
        return map.size();
    }

    @Benchmark
    public int iterateKeys(Blackhole bh) {
        for (ObjectCursor<String> c : map.keys()) {
            bh.consume(c.value);
        }
        return map.size();
    }

    @Benchmark
    public int iterateValues(Blackhole bh) {
        for (IntCursor c : map.values()) {
            bh.consume(c.value);
        }
        return map.size();
    }

    @Benchmark
    public int cloneMap() {
        ObjectIntHashMap<String> cloned = map.clone();
        return cloned.size();
    }

    @Benchmark
    public int clearMap() {
        ObjectIntHashMap<String> copy = map.clone();
        copy.clear();
        return copy.size();
    }

    @Benchmark
    public int size() {
        return map.size();
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return map.hashCode();
    }

    @Benchmark
    public int equalsBenchmark() {
        return map.equals(map.clone()) ? 1 : 0;
    }

    private static class NoOpProcedure implements ObjectIntProcedure<String> {
        @Override
        public void apply(String key, int value) {
            // no-op
        }
    }

    private static class NoOpPredicate implements ObjectIntPredicate<String> {
        @Override
        public boolean apply(String key, int value) {
            return true;
        }
    }
}
