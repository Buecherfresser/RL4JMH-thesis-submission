package bench.generated.c062;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import com.carrotsearch.hppc.ObjectIntIdentityHashMap;
import com.carrotsearch.hppc.cursors.ObjectIntCursor;
import com.carrotsearch.hppc.procedures.ObjectIntProcedure;
import java.util.concurrent.TimeUnit;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectIntIdentityHashMapBenchmark {

    private static final int SIZE = 1024;

    private ObjectIntIdentityHashMap<Object> filledMap;
    private ObjectIntIdentityHashMap<Object> emptyMap;
    private ObjectIntIdentityHashMap<Object> smallMap;
    private Object[] keys;
    private int[] values;
    private Random random;

    @Setup
    public void setup() {
        random = new Random(0);
        keys = new Object[SIZE];
        values = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            keys[i] = new Object();
            values[i] = i;
        }

        filledMap = new ObjectIntIdentityHashMap<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            filledMap.put(keys[i], values[i]);
        }

        emptyMap = new ObjectIntIdentityHashMap<>(SIZE);

        // small map for putAll benchmark
        smallMap = new ObjectIntIdentityHashMap<>(16);
        for (int i = 0; i < 16; i++) {
            smallMap.put(new Object(), i);
        }
    }

    @Benchmark
    public int get() {
        int idx = random.nextInt(SIZE);
        return filledMap.get(keys[idx]);
    }

    @Benchmark
    public int getOrDefault() {
        int idx = random.nextInt(SIZE);
        return filledMap.getOrDefault(keys[idx], -1);
    }

    @Benchmark
    public boolean containsKey() {
        int idx = random.nextInt(SIZE);
        return filledMap.containsKey(keys[idx]);
    }

    @Benchmark
    public int putExisting() {
        int idx = random.nextInt(SIZE);
        return filledMap.put(keys[idx], values[idx] + 1);
    }

    @Benchmark
    public int putOrAddExisting() {
        int idx = random.nextInt(SIZE);
        return filledMap.putOrAdd(keys[idx], 1, 1);
    }

    @Benchmark
    public int addToExisting() {
        int idx = random.nextInt(SIZE);
        return filledMap.addTo(keys[idx], 1);
    }

    @Benchmark
    public int removeExisting() {
        int idx = random.nextInt(SIZE);
        return filledMap.remove(keys[idx]);
    }

    @Benchmark
    public int clear() {
        emptyMap.clear();
        return emptyMap.size();
    }

    @Benchmark
    public int size() {
        return filledMap.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return filledMap.isEmpty();
    }

    @Benchmark
    public int ramBytesUsed() {
        return (int) filledMap.ramBytesUsed();
    }

    @Benchmark
    public int iterationSum() {
        int sum = 0;
        for (ObjectIntCursor<Object> c : filledMap) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public int forEachSum() {
        final int[] sum = new int[1];
        filledMap.forEach(new ObjectIntProcedure<Object>() {
            @Override
            public void apply(Object key, int value) {
                sum[0] += value;
            }
        });
        return sum[0];
    }

    @Benchmark
    public int putAll() {
        ObjectIntIdentityHashMap<Object> target = new ObjectIntIdentityHashMap<>(SIZE);
        target.putAll(smallMap);
        return target.size();
    }
}
