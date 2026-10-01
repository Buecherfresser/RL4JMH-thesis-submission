package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.IntFloatHashMap;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntFloatHashMapBenchmark {

    private IntFloatHashMap map;
    private int existingKey;
    private int missingKey;
    private int existingIndex;
    private int missingIndex;
    private float replacementValue;

    @Setup(Level.Trial)
    public void setup() {
        int n = 10000;
        map = new IntFloatHashMap(n);
        Random rnd = new Random(12345);

        for (int i = 0; i < n; i++) {
            map.put(i, rnd.nextFloat());
        }

        existingKey = n / 2;
        missingKey = n + 1;
        existingIndex = map.indexOf(existingKey);
        missingIndex = map.indexOf(missingKey);
        replacementValue = 42.0f;
    }

    @Benchmark
    public float get() {
        return map.get(existingKey);
    }

    @Benchmark
    public float getMissing() {
        return map.get(missingKey);
    }

    @Benchmark
    public float getOrDefault() {
        return map.getOrDefault(existingKey, 0f);
    }

    @Benchmark
    public boolean containsKey() {
        return map.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsMissingKey() {
        return map.containsKey(missingKey);
    }

    @Benchmark
    public int indexOf() {
        return map.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExists() {
        return map.indexExists(existingIndex);
    }

    @Benchmark
    public boolean indexExistsMissing() {
        return map.indexExists(missingIndex);
    }

    @Benchmark
    public float indexGet() {
        return map.indexGet(existingIndex);
    }

    @Benchmark
    public float put() {
        return map.put(existingKey, replacementValue);
    }

    @Benchmark
    public float indexReplace() {
        return map.indexReplace(existingIndex, replacementValue);
    }
}
