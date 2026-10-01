package bench.generated.c073;

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
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortCharHashMap;
import com.carrotsearch.hppc.cursors.ShortCharCursor;
import com.carrotsearch.hppc.procedures.ShortCharProcedure;
import com.carrotsearch.hppc.predicates.ShortCharPredicate;
import com.carrotsearch.hppc.ShortCharAssociativeContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortCharHashMapBenchmark {

    private static final int SIZE = 1024;
    private static final int POOL_SIZE = 16;

    // Data for read‑only benchmarks
    private short[] keys;
    private char[] values;
    private ShortCharHashMap filledMap;

    // Pools for mutating benchmarks
    private ShortCharHashMap[] emptyMaps;      // for putNew
    private ShortCharHashMap[] singleEntryMaps; // for removeExisting / removeMissing
    private ShortCharHashMap[] clearMaps;      // for clear benchmark

    // Indexes to rotate through data/pools
    private int getIdx;
    private int containsIdx;
    private int putIdx;
    private int removeIdx;
    private int clearIdx;
    private int putOrAddIdx;
    private int addToIdx;

    // Small source map for putAll benchmark
    private ShortCharHashMap sourceMap;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(0x1234ABCD);
        keys = new short[SIZE];
        values = new char[SIZE];
        filledMap = new ShortCharHashMap(SIZE);
        for (int i = 0; i < SIZE; i++) {
            short k = (short) (rnd.nextInt(Short.MAX_VALUE - 1) + 1); // avoid zero key
            char v = (char) rnd.nextInt(Character.MAX_VALUE);
            keys[i] = k;
            values[i] = v;
            filledMap.put(k, v);
        }

        // empty maps pool
        emptyMaps = new ShortCharHashMap[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            emptyMaps[i] = new ShortCharHashMap(SIZE);
        }

        // single‑entry maps pool
        singleEntryMaps = new ShortCharHashMap[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            ShortCharHashMap m = new ShortCharHashMap(SIZE);
            short k = (short) (i + 1);
            char v = (char) (i + 1);
            m.put(k, v);
            singleEntryMaps[i] = m;
        }

        // clear maps pool (pre‑filled)
        clearMaps = new ShortCharHashMap[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            ShortCharHashMap m = new ShortCharHashMap(SIZE);
            for (int j = 0; j < SIZE; j++) {
                m.put(keys[j], values[j]);
            }
            clearMaps[i] = m;
        }

        // source map for putAll
        sourceMap = new ShortCharHashMap(8);
        for (int i = 0; i < 8; i++) {
            sourceMap.put((short) (i + 1000), (char) (i + 'a'));
        }
    }

    @Benchmark
    public char getExisting() {
        short k = keys[getIdx];
        getIdx = (getIdx + 1) & (SIZE - 1);
        return filledMap.get(k);
    }

    @Benchmark
    public char getMissing() {
        short k = (short) (SIZE + 1);
        return filledMap.get(k);
    }

    @Benchmark
    public boolean containsKeyExisting() {
        short k = keys[containsIdx];
        containsIdx = (containsIdx + 1) & (SIZE - 1);
        return filledMap.containsKey(k);
    }

    @Benchmark
    public boolean containsKeyMissing() {
        short k = (short) (SIZE + 2);
        return filledMap.containsKey(k);
    }

    @Benchmark
    public char putNew() {
        ShortCharHashMap map = emptyMaps[putIdx];
        short k = (short) (putIdx + 1);
        char v = (char) (putIdx + 1);
        putIdx = (putIdx + 1) % POOL_SIZE;
        return map.put(k, v);
    }

    @Benchmark
    public char removeExisting() {
        ShortCharHashMap map = singleEntryMaps[removeIdx];
        short k = (short) (removeIdx + 1);
        removeIdx = (removeIdx + 1) % POOL_SIZE;
        return map.remove(k);
    }

    @Benchmark
    public char removeMissing() {
        ShortCharHashMap map = singleEntryMaps[removeIdx];
        short k = (short) (SIZE + 10);
        removeIdx = (removeIdx + 1) % POOL_SIZE;
        return map.remove(k);
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
    public void clear(Blackhole bh) {
        ShortCharHashMap map = clearMaps[clearIdx];
        clearIdx = (clearIdx + 1) % POOL_SIZE;
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public int iterate(Blackhole bh) {
        int sum = 0;
        for (ShortCharCursor c : filledMap) {
            sum += c.value;
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        final int[] sum = new int[1];
        ShortCharProcedure proc = (short k, char v) -> sum[0] += v;
        filledMap.forEach(proc);
        bh.consume(sum[0]);
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        final int[] sum = new int[1];
        ShortCharPredicate pred = (short k, char v) -> {
            sum[0] += v;
            return true;
        };
        filledMap.forEach(pred);
        bh.consume(sum[0]);
    }

    @Benchmark
    public int keysSize() {
        return filledMap.keys().size();
    }

    @Benchmark
    public int valuesSize() {
        return filledMap.values().size();
    }

    @Benchmark
    public char putOrAddExisting() {
        short k = keys[putOrAddIdx];
        putOrAddIdx = (putOrAddIdx + 1) & (SIZE - 1);
        return filledMap.putOrAdd(k, (char) 0, (char) 1);
    }

    @Benchmark
    public char putOrAddMissing() {
        short k = (short) (SIZE + 3);
        return filledMap.putOrAdd(k, (char) 5, (char) 1);
    }

    @Benchmark
    public char addToExisting() {
        short k = keys[addToIdx];
        addToIdx = (addToIdx + 1) & (SIZE - 1);
        return filledMap.addTo(k, (char) 1);
    }

    @Benchmark
    public char addToMissing() {
        short k = (short) (SIZE + 4);
        return filledMap.addTo(k, (char) 1);
    }

    @Benchmark
    public int putAllFromIterable(Blackhole bh) {
        ShortCharHashMap target = new ShortCharHashMap(8);
        target.putAll((Iterable<? extends ShortCharCursor>) sourceMap);
        bh.consume(target.size());
        return target.size();
    }

    @Benchmark
    public int removeAllByPredicate(Blackhole bh) {
        ShortCharHashMap map = new ShortCharHashMap(SIZE);
        for (int i = 0; i < SIZE; i++) {
            map.put(keys[i], values[i]);
        }
        ShortCharPredicate pred = (short k, char v) -> v % 2 == 0;
        int removed = map.removeAll(pred);
        bh.consume(removed);
        return removed;
    }
}
