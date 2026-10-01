package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortFloatHashMap;
import com.carrotsearch.hppc.cursors.ShortFloatCursor;
import com.carrotsearch.hppc.procedures.ShortFloatProcedure;
import com.carrotsearch.hppc.predicates.ShortFloatPredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortFloatHashMapBenchmark {

    private ShortFloatHashMap map;
    private short[] keys;
    private float[] values;
    private Random random;
    private int elementCount;

    @Setup(Level.Trial)
    public void setUp() {
        elementCount = 1024;
        random = new Random(0);
        keys = new short[elementCount];
        values = new float[elementCount];
        for (int i = 0; i < elementCount; i++) {
            keys[i] = (short) random.nextInt(Short.MAX_VALUE + 1);
            values[i] = random.nextFloat();
        }
        map = new ShortFloatHashMap(elementCount);
        for (int i = 0; i < elementCount; i++) {
            map.put(keys[i], values[i]);
        }
    }

    @Benchmark
    public float benchGet() {
        return map.get(keys[0]);
    }

    @Benchmark
    public float benchGetOrDefault() {
        return map.getOrDefault(keys[0], -1.0f);
    }

    @Benchmark
    public boolean benchContainsKey() {
        return map.containsKey(keys[0]);
    }

    @Benchmark
    public int benchSize() {
        return map.size();
    }

    @Benchmark
    public ShortFloatHashMap benchClone() {
        return map.clone();
    }

    @Benchmark
    public float benchPut() {
        ShortFloatHashMap m = map.clone();
        short newKey = (short) (elementCount + 1);
        return m.put(newKey, 1.23f);
    }

    @Benchmark
    public float benchPutOrAdd() {
        ShortFloatHashMap m = map.clone();
        short k = keys[0];
        return m.putOrAdd(k, 2.0f, 3.0f);
    }

    @Benchmark
    public float benchAddTo() {
        ShortFloatHashMap m = map.clone();
        short k = keys[0];
        return m.addTo(k, 4.0f);
    }

    @Benchmark
    public float benchRemove() {
        ShortFloatHashMap m = map.clone();
        short k = keys[0];
        return m.remove(k);
    }

    @Benchmark
    public void benchClear(Blackhole bh) {
        ShortFloatHashMap m = map.clone();
        m.clear();
        bh.consume(m);
    }

    @Benchmark
    public ShortFloatProcedure benchForEachProcedure() {
        return map.forEach((ShortFloatProcedure) (k, v) -> {
            // no-op
        });
    }

    @Benchmark
    public ShortFloatPredicate benchForEachPredicate() {
        return map.forEach((ShortFloatPredicate) (k, v) -> true);
    }

    @Benchmark
    public int benchIterator() {
        int sum = 0;
        for (ShortFloatCursor c : map) {
            sum += c.key;
        }
        return sum;
    }

    @Benchmark
    public boolean benchKeysContains() {
        return map.keys().contains(keys[0]);
    }

    @Benchmark
    public boolean benchValuesContains() {
        return map.values().contains(values[0]);
    }

    @Benchmark
    public int benchIndexOf() {
        return map.indexOf(keys[0]);
    }

    @Benchmark
    public float benchIndexGet() {
        int idx = map.indexOf(keys[0]);
        return map.indexGet(idx);
    }

    @Benchmark
    public float benchIndexReplace() {
        ShortFloatHashMap m = map.clone();
        int idx = m.indexOf(keys[0]);
        return m.indexReplace(idx, 9.9f);
    }

    @Benchmark
    public float benchIndexInsert() {
        ShortFloatHashMap m = map.clone();
        short newKey = (short) (elementCount + 2);
        int idx = m.indexOf(newKey); // will be negative
        m.indexInsert(idx, newKey, 7.7f);
        return m.get(newKey);
    }

    @Benchmark
    public float benchIndexRemove() {
        ShortFloatHashMap m = map.clone();
        int idx = m.indexOf(keys[0]);
        return m.indexRemove(idx);
    }
}
