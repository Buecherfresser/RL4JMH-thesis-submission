package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortByteHashMap;
import com.carrotsearch.hppc.cursors.ShortByteCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.procedures.ShortByteProcedure;
import com.carrotsearch.hppc.predicates.ShortBytePredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortByteHashMapBenchmark {

    private static final int INITIAL_SIZE = 1024;

    private ShortByteHashMap map;
    private short[] keys;
    private byte[] values;
    private int getIndex;
    private int putKeyCounter;
    private Random random;

    @Setup(Level.Trial)
    public void setUp() {
        map = new ShortByteHashMap(INITIAL_SIZE);
        keys = new short[INITIAL_SIZE];
        values = new byte[INITIAL_SIZE];
        random = new Random(0x1234abcd);
        for (int i = 0; i < INITIAL_SIZE; i++) {
            short k = (short) (i + 1); // avoid zero key
            byte v = (byte) random.nextInt(256);
            map.put(k, v);
            keys[i] = k;
            values[i] = v;
        }
        getIndex = 0;
        putKeyCounter = INITIAL_SIZE + 1;
    }

    @Benchmark
    public byte get() {
        short k = keys[getIndex++];
        if (getIndex >= keys.length) getIndex = 0;
        return map.get(k);
    }

    @Benchmark
    public byte getOrDefault() {
        short k = keys[getIndex++];
        if (getIndex >= keys.length) getIndex = 0;
        return map.getOrDefault(k, (byte) -1);
    }

    @Benchmark
    public boolean containsKey() {
        short k = keys[getIndex++];
        if (getIndex >= keys.length) getIndex = 0;
        return map.containsKey(k);
    }

    @Benchmark
    public byte put(Blackhole bh) {
        short k = (short) (putKeyCounter++);
        byte v = (byte) random.nextInt(256);
        byte prev = map.put(k, v);
        bh.consume(prev);
        return prev;
    }

    @Benchmark
    public byte remove(Blackhole bh) {
        short k = keys[getIndex++];
        if (getIndex >= keys.length) getIndex = 0;
        byte prev = map.remove(k);
        bh.consume(prev);
        return prev;
    }

    @Benchmark
    public byte putOrAdd(Blackhole bh) {
        short k = keys[getIndex++];
        if (getIndex >= keys.length) getIndex = 0;
        byte inc = (byte) 1;
        byte result = map.putOrAdd(k, inc, inc);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public byte addTo(Blackhole bh) {
        short k = keys[getIndex++];
        if (getIndex >= keys.length) getIndex = 0;
        byte inc = (byte) 1;
        byte result = map.addTo(k, inc);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int putAllFromMap(Blackhole bh) {
        ShortByteHashMap src = new ShortByteHashMap(16);
        for (short i = 1; i <= 16; i++) {
            src.put(i, (byte) i);
        }
        int added = map.putAll(src);
        bh.consume(added);
        return added;
    }

    @Benchmark
    public int indexOf() {
        short k = keys[getIndex++];
        if (getIndex >= keys.length) getIndex = 0;
        return map.indexOf(k);
    }

    @Benchmark
    public byte indexGet(Blackhole bh) {
        short k = keys[getIndex++];
        if (getIndex >= keys.length) getIndex = 0;
        int idx = map.indexOf(k);
        if (map.indexExists(idx)) {
            byte v = map.indexGet(idx);
            bh.consume(v);
            return v;
        }
        return (byte) 0;
    }

    @Benchmark
    public byte indexReplace(Blackhole bh) {
        short k = keys[getIndex++];
        if (getIndex >= keys.length) getIndex = 0;
        int idx = map.indexOf(k);
        if (map.indexExists(idx)) {
            byte newVal = (byte) (values[idx] + 1);
            byte old = map.indexReplace(idx, newVal);
            bh.consume(old);
            return old;
        }
        return (byte) 0;
    }

    @Benchmark
    public void indexInsert(Blackhole bh) {
        short missingKey = (short) (putKeyCounter++);
        int idx = map.indexOf(missingKey);
        map.indexInsert(idx, missingKey, (byte) 42);
        bh.consume(missingKey);
    }

    @Benchmark
    public byte indexRemove(Blackhole bh) {
        short k = keys[getIndex++];
        if (getIndex >= keys.length) getIndex = 0;
        int idx = map.indexOf(k);
        if (map.indexExists(idx)) {
            byte removed = map.indexRemove(idx);
            bh.consume(removed);
            return removed;
        }
        return (byte) 0;
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
    public int hashCodeBenchmark() {
        return map.hashCode();
    }

    @Benchmark
    public ShortByteHashMap cloneMap() {
        return map.clone();
    }

    @Benchmark
    public void clear(Blackhole bh) {
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        map.forEach((ShortByteProcedure) (short k, byte v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        map.forEach((ShortBytePredicate) (short k, byte v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void iterateKeys(Blackhole bh) {
        for (ShortCursor c : map.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateValues(Blackhole bh) {
        for (ByteCursor c : map.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateEntries(Blackhole bh) {
        for (ShortByteCursor c : map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }
}
