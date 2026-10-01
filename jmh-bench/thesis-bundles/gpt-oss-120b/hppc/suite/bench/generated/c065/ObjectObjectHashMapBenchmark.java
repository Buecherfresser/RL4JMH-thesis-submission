package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.procedures.ObjectObjectProcedure;
import com.carrotsearch.hppc.predicates.ObjectObjectPredicate;
import java.util.Iterator;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectObjectHashMapBenchmark {

    private int size = 1024;

    private ObjectObjectHashMap<String, String> map;
    private String[] keys;
    private String[] absentKeys;
    private int index;
    private Random random;

    @Setup(Level.Trial)
    public void setUp() {
        map = new ObjectObjectHashMap<>(size);
        keys = new String[size];
        absentKeys = new String[size];
        random = new Random(0x1234ABCD);
        for (int i = 0; i < size; i++) {
            String k = "key" + i;
            String v = "value" + i;
            keys[i] = k;
            map.put(k, v);
        }
        for (int i = 0; i < size; i++) {
            absentKeys[i] = "absent" + i;
        }
        index = 0;
    }

    private int nextIndex() {
        int i = index;
        index = (index + 1) % size;
        return i;
    }

    @Benchmark
    public String benchmarkPutExisting() {
        int i = nextIndex();
        return map.put(keys[i], "newValue" + i);
    }

    @Benchmark
    public String benchmarkGetExisting() {
        int i = nextIndex();
        return map.get(keys[i]);
    }

    @Benchmark
    public String benchmarkGetOrDefaultExisting() {
        int i = nextIndex();
        return map.getOrDefault(keys[i], "default");
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        int i = nextIndex();
        return map.containsKey(keys[i]);
    }

    @Benchmark
    public String benchmarkRemoveAbsent() {
        int i = nextIndex();
        return map.remove(absentKeys[i]);
    }

    @Benchmark
    public int benchmarkSize() {
        return map.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return map.isEmpty();
    }

    @Benchmark
    public int benchmarkClear() {
        ObjectObjectHashMap<String, String> copy = map.clone();
        copy.clear();
        return copy.size();
    }

    @Benchmark
    public void benchmarkEnsureCapacity(Blackhole bh) {
        map.ensureCapacity(map.size() * 2);
        bh.consume(map.size());
    }

    @Benchmark
    public ObjectObjectProcedure<Object, Object> benchmarkForEachProcedure() {
        NoOpProcedure proc = new NoOpProcedure();
        return map.forEach(proc);
    }

    @Benchmark
    public ObjectObjectPredicate<Object, Object> benchmarkForEachPredicate() {
        AlwaysTruePredicate pred = new AlwaysTruePredicate();
        return map.forEach(pred);
    }

    @Benchmark
    public String benchmarkIterateEntries() {
        Iterator<ObjectObjectCursor<String, String>> it = map.iterator();
        if (it.hasNext()) {
            ObjectObjectCursor<String, String> c = it.next();
            return c.key;
        }
        return null;
    }

    @Benchmark
    public String benchmarkIterateKeys() {
        Iterator<ObjectCursor<String>> it = map.keys().iterator();
        if (it.hasNext()) {
            ObjectCursor<String> c = it.next();
            return c.value;
        }
        return null;
    }

    @Benchmark
    public String benchmarkIterateValues() {
        Iterator<ObjectCursor<String>> it = map.values().iterator();
        if (it.hasNext()) {
            ObjectCursor<String> c = it.next();
            return c.value;
        }
        return null;
    }

    @Benchmark
    public ObjectObjectHashMap<String, String> benchmarkClone() {
        return map.clone();
    }

    @Benchmark
    public int benchmarkHashCode() {
        return map.hashCode();
    }

    @Benchmark
    public boolean benchmarkEqualsSelf() {
        return map.equals(map);
    }

    @Benchmark
    public String benchmarkToString() {
        return map.toString();
    }

    // Helper procedure that does nothing.
    private static class NoOpProcedure implements ObjectObjectProcedure<Object, Object> {
        @Override
        public void apply(Object key, Object value) {
            // no-op
        }
    }

    // Helper predicate that always returns true.
    private static class AlwaysTruePredicate implements ObjectObjectPredicate<Object, Object> {
        @Override
        public boolean apply(Object key, Object value) {
            return true;
        }
    }
}
