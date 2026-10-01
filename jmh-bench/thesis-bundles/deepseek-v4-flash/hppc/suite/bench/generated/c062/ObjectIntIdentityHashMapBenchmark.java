package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import com.carrotsearch.hppc.ObjectIntIdentityHashMap;
import com.carrotsearch.hppc.cursors.ObjectIntCursor;
import com.carrotsearch.hppc.procedures.ObjectIntProcedure;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectIntIdentityHashMapBenchmark {

    private static final int SIZE = 10_000;

    private ObjectIntIdentityHashMap<String> map;
    private String[] keys;
    private String missKey;
    private String existingKey;
    private int indexForExistingKey;
    private int[] valuesForFrom;

    @Setup(Level.Trial)
    public void setUp() {
        map = new ObjectIntIdentityHashMap<>(SIZE);
        keys = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            keys[i] = "key" + i;
            map.put(keys[i], i);
        }
        existingKey = keys[0];
        missKey = "nonexistent";
        indexForExistingKey = map.indexOf(existingKey);
        valuesForFrom = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            valuesForFrom[i] = i;
        }
    }

    @Benchmark
    public int putOverwrite() {
        return map.put(existingKey, 12345);
    }

    @Benchmark
    public int getHit() {
        return map.get(existingKey);
    }

    @Benchmark
    public int getMiss() {
        return map.get(missKey);
    }

    @Benchmark
    public boolean containsKeyHit() {
        return map.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsKeyMiss() {
        return map.containsKey(missKey);
    }

    @Benchmark
    public int getOrDefaultHit() {
        return map.getOrDefault(existingKey, -1);
    }

    @Benchmark
    public int getOrDefaultMiss() {
        return map.getOrDefault(missKey, -1);
    }

    @Benchmark
    public int indexOfHit() {
        return map.indexOf(existingKey);
    }

    @Benchmark
    public int indexOfMiss() {
        return map.indexOf(missKey);
    }

    @Benchmark
    public int indexGet() {
        return map.indexGet(indexForExistingKey);
    }

    @Benchmark
    public int putOrAddExisting() {
        return map.putOrAdd(existingKey, 999, 1);
    }

    @Benchmark
    public int addToExisting() {
        return map.addTo(existingKey, 1);
    }

    @Benchmark
    public int forEach() {
        int[] sum = {0};
        map.forEach((ObjectIntProcedure<String>) (key, value) -> sum[0] += value);
        return sum[0];
    }

    @Benchmark
    public int iteration() {
        int sum = 0;
        for (ObjectIntCursor<String> cursor : map) {
            sum += cursor.value;
        }
        return sum;
    }

    @Benchmark
    public int size() {
        return map.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Benchmark
    public long ramBytesUsed() {
        return map.ramBytesUsed();
    }

    @Benchmark
    public ObjectIntIdentityHashMap<String> from() {
        return ObjectIntIdentityHashMap.from(keys, valuesForFrom);
    }
}
