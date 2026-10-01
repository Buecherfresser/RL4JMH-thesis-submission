package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import com.carrotsearch.hppc.ObjectFloatHashMap;
import com.carrotsearch.hppc.cursors.ObjectFloatCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.procedures.ObjectFloatProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectFloatHashMapBenchmark {
    private ObjectFloatHashMap<String> map;
    private ObjectFloatHashMap<String> mutableMap;
    private String[] keys;
    private String missingKey;
    private int putCounter;

    @Setup(Level.Trial)
    public void setup() {
        int size = 1024;
        map = new ObjectFloatHashMap<>(size);
        mutableMap = new ObjectFloatHashMap<>(size);
        keys = new String[size];
        for (int i = 0; i < size; i++) {
            String k = "key" + i;
            keys[i] = k;
            map.put(k, i);
            mutableMap.put(k, i);
        }
        missingKey = "missing";
        putCounter = 0;
    }

    @Benchmark
    public float getExisting() {
        return map.get(keys[0]);
    }

    @Benchmark
    public float getMissing() {
        return map.get(missingKey);
    }

    @Benchmark
    public boolean containsExisting() {
        return map.containsKey(keys[0]);
    }

    @Benchmark
    public boolean containsMissing() {
        return map.containsKey(missingKey);
    }

    @Benchmark
    public int size() {
        return map.size();
    }

    @Benchmark
    public ObjectFloatHashMap<String> cloneMap() {
        return map.clone();
    }

    @Benchmark
    public void iterate(Blackhole bh) {
        for (ObjectFloatCursor<String> c : map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        map.forEach((ObjectFloatProcedure<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void putNewKey(Blackhole bh) {
        mutableMap.clear();
        String key = "newKey" + putCounter++;
        mutableMap.put(key, 1.0f);
        bh.consume(mutableMap.size());
    }

    @Benchmark
    public float removeExisting(Blackhole bh) {
        mutableMap.clear();
        for (int i = 0; i < keys.length; i++) {
            mutableMap.put(keys[i], i);
        }
        return mutableMap.remove(keys[0]);
    }

    @Benchmark
    public void keySetIteration(Blackhole bh) {
        Iterator<ObjectCursor<String>> it = map.keys().iterator();
        while (it.hasNext()) {
            ObjectCursor<String> c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void valuesIteration(Blackhole bh) {
        Iterator<FloatCursor> it = map.values().iterator();
        while (it.hasNext()) {
            FloatCursor c = it.next();
            bh.consume(c.value);
        }
    }
}
