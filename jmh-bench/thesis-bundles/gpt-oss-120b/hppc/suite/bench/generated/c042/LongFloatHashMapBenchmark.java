package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.LongFloatHashMap;
import com.carrotsearch.hppc.cursors.LongFloatCursor;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongFloatHashMapBenchmark {

    private LongFloatHashMap map;
    private long[] keys;
    private float[] values;
    private long existingKey;
    private long nonExistingKey;
    private int existingIndex;
    private List<LongFloatCursor> cursorList;

    @Setup(Level.Trial)
    public void setup() {
        int size = 1024;
        map = new LongFloatHashMap(size);
        keys = new long[size];
        values = new float[size];
        Random rnd = new Random(12345L);
        for (int i = 0; i < size; i++) {
            long k = rnd.nextLong();
            if (k == 0L) {
                k = 1L;
            }
            float v = rnd.nextFloat();
            keys[i] = k;
            values[i] = v;
            map.put(k, v);
        }
        existingKey = keys[0];
        nonExistingKey = Long.MAX_VALUE; // guaranteed not to be in the map
        existingIndex = map.indexOf(existingKey);
        cursorList = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            LongFloatCursor c = new LongFloatCursor();
            c.key = keys[i];
            c.value = values[i];
            cursorList.add(c);
        }
    }

    @Benchmark
    public float benchmarkPutExisting() {
        return map.put(existingKey, 1.23f);
    }

    @Benchmark
    public float benchmarkGetExisting() {
        return map.get(existingKey);
    }

    @Benchmark
    public float benchmarkGetOrDefaultExisting() {
        return map.getOrDefault(existingKey, -1.0f);
    }

    @Benchmark
    public boolean benchmarkContainsKeyExisting() {
        return map.containsKey(existingKey);
    }

    @Benchmark
    public float benchmarkRemoveNonExisting() {
        return map.remove(nonExistingKey);
    }

    @Benchmark
    public float benchmarkPutOrAddExisting() {
        return map.putOrAdd(existingKey, 0.0f, 1.0f);
    }

    @Benchmark
    public float benchmarkAddToExisting() {
        return map.addTo(existingKey, 1.0f);
    }

    @Benchmark
    public int benchmarkPutAllIterable() {
        LongFloatHashMap copy = map.clone();
        return copy.putAll(cursorList);
    }

    @Benchmark
    public int benchmarkIndexOfExisting() {
        return map.indexOf(existingKey);
    }

    @Benchmark
    public float benchmarkIndexGet() {
        return map.indexGet(existingIndex);
    }

    @Benchmark
    public float benchmarkIndexReplace() {
        return map.indexReplace(existingIndex, 3.0f);
    }
}
