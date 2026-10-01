package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;
import com.carrotsearch.hppc.procedures.IntObjectProcedure;
import com.carrotsearch.hppc.predicates.IntObjectPredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntObjectHashMapBenchmark {

    private IntObjectHashMap<String> map;
    private int[] keys;
    private String[] values;
    private Random random;

    @Setup(Level.Trial)
    public void setUp() {
        int size = 1024;
        map = new IntObjectHashMap<>(size);
        keys = new int[size];
        values = new String[size];
        random = new Random(0x1234);
        for (int i = 0; i < size; i++) {
            int key = i + 1; // avoid zero key (special case)
            String value = "v" + key;
            keys[i] = key;
            values[i] = value;
            map.put(key, value);
        }
    }

    @State(Scope.Thread)
    public static class PutState {
        IntObjectHashMap<String> map;
        int key;
        String value;

        @Setup(Level.Invocation)
        public void setUp() {
            map = new IntObjectHashMap<>(16);
            key = 42;
            value = "new";
        }
    }

    @Benchmark
    public String benchmarkPut(PutState s) {
        return s.map.put(s.key, s.value);
    }

    @Benchmark
    public String benchmarkGet() {
        int idx = random.nextInt(keys.length);
        int key = keys[idx];
        return map.get(key);
    }

    @Benchmark
    public String benchmarkGetOrDefault() {
        int idx = random.nextInt(keys.length);
        int key = keys[idx];
        return map.getOrDefault(key, "default");
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        int idx = random.nextInt(keys.length);
        int key = keys[idx];
        return map.containsKey(key);
    }

    @Benchmark
    public String benchmarkRemove() {
        IntObjectHashMap<String> m = new IntObjectHashMap<>(1);
        m.put(1, "one");
        return m.remove(1);
    }

    @Benchmark
    public int benchmarkSize() {
        return map.size();
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        IntObjectHashMap<String> m = new IntObjectHashMap<>(1024);
        for (int i = 0; i < keys.length; i++) {
            m.put(keys[i], values[i]);
        }
        m.clear();
        bh.consume(m);
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        map.forEach((IntObjectProcedure<String>) (k, v) -> bh.consume(k));
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        map.forEach((IntObjectPredicate<String>) (k, v) -> {
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        for (IntObjectCursor<String> c : map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int benchmarkIndexOf() {
        int idx = random.nextInt(keys.length);
        int key = keys[idx];
        return map.indexOf(key);
    }

    @Benchmark
    public String benchmarkIndexGet() {
        int idx = random.nextInt(keys.length);
        int key = keys[idx];
        int index = map.indexOf(key);
        if (index >= 0) {
            return map.indexGet(index);
        }
        return null;
    }

    @Benchmark
    public String benchmarkIndexReplace() {
        int idx = random.nextInt(keys.length);
        int key = keys[idx];
        int index = map.indexOf(key);
        if (index >= 0) {
            return map.indexReplace(index, "replaced");
        }
        return null;
    }

    @Benchmark
    public void benchmarkKeysSize(Blackhole bh) {
        bh.consume(map.keys().size());
    }

    @Benchmark
    public boolean benchmarkValuesContains(Blackhole bh) {
        int idx = random.nextInt(values.length);
        String val = values[idx];
        return map.values().contains(val);
    }

    @Benchmark
    public void benchmarkPutAll(Blackhole bh) {
        IntObjectHashMap<String> target = new IntObjectHashMap<>(16);
        for (int i = 0; i < keys.length; i++) {
            target.put(keys[i], values[i]);
        }
        IntObjectHashMap<String> source = new IntObjectHashMap<>(2);
        source.put(1001, "a");
        source.put(1002, "b");
        target.putAll(source);
        bh.consume(target);
    }

    @Benchmark
    public void benchmarkRemoveAllByPredicate(Blackhole bh) {
        IntObjectHashMap<String> m = new IntObjectHashMap<>(1024);
        for (int i = 0; i < keys.length; i++) {
            m.put(keys[i], values[i]);
        }
        m.removeAll((IntObjectPredicate<String>) (k, v) -> (k & 1) == 0);
        bh.consume(m);
    }
}
