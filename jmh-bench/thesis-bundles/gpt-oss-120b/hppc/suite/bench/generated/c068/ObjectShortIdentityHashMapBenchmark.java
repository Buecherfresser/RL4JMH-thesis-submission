package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectShortIdentityHashMap;
import com.carrotsearch.hppc.cursors.ObjectShortCursor;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectShortIdentityHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        int size;
        ObjectShortIdentityHashMap<Object> map;
        Object[] keys;
        short[] values;
        int index = 0;

        @Setup(Level.Trial)
        public void setUp() {
            size = 4096;
            map = new ObjectShortIdentityHashMap<>(size);
            keys = new Object[size];
            values = new short[size];
            Random rnd = new Random(0x1234);
            for (int i = 0; i < size; i++) {
                Object k = new Object();
                short v = (short) rnd.nextInt(Short.MAX_VALUE + 1);
                keys[i] = k;
                values[i] = v;
                map.put(k, v);
            }
        }

        Object nextKey() {
            Object k = keys[index];
            index = (index + 1) & (size - 1);
            return k;
        }
    }

    @State(Scope.Benchmark)
    public static class ClearState {
        int size;
        ObjectShortIdentityHashMap<Object> map;

        @Setup(Level.Invocation)
        public void setUp() {
            size = 4096;
            map = new ObjectShortIdentityHashMap<>(size);
            for (int i = 0; i < size; i++) {
                map.put(new Object(), (short) i);
            }
        }
    }

    @Benchmark
    public short benchmarkPutNew(BenchmarkState s) {
        return s.map.put(new Object(), (short) 1);
    }

    @Benchmark
    public short benchmarkGetExisting(BenchmarkState s) {
        Object key = s.nextKey();
        return s.map.get(key);
    }

    @Benchmark
    public boolean benchmarkContainsKey(BenchmarkState s) {
        Object key = s.nextKey();
        return s.map.containsKey(key);
    }

    @Benchmark
    public short benchmarkPutOrAdd(BenchmarkState s) {
        Object key = s.nextKey();
        return s.map.putOrAdd(key, (short) 1, (short) 1);
    }

    @Benchmark
    public short benchmarkAddTo(BenchmarkState s) {
        Object key = s.nextKey();
        return s.map.addTo(key, (short) 1);
    }

    @Benchmark
    public int benchmarkIteration(BenchmarkState s) {
        int sum = 0;
        for (ObjectShortCursor c : s.map) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public int benchmarkKeysSize(BenchmarkState s) {
        return s.map.keys().size();
    }

    @Benchmark
    public int benchmarkValuesSize(BenchmarkState s) {
        return s.map.values().size();
    }

    @Benchmark
    public int benchmarkSize(BenchmarkState s) {
        return s.map.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty(BenchmarkState s) {
        return s.map.isEmpty();
    }

    @Benchmark
    public long benchmarkRamBytesUsed(BenchmarkState s) {
        return s.map.ramBytesUsed();
    }

    @Benchmark
    public void benchmarkClear(ClearState s, Blackhole bh) {
        s.map.clear();
        bh.consume(s.map);
    }
}
