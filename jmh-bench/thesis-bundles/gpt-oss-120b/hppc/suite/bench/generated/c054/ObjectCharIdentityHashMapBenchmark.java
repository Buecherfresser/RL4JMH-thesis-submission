package bench.generated.c054;

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
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.ObjectCharIdentityHashMap;
import com.carrotsearch.hppc.cursors.ObjectCharCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectCharIdentityHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class FilledState {
        static final int SIZE = 1024;
        Object[] keys = new Object[SIZE];
        char[] values = new char[SIZE];
        ObjectCharIdentityHashMap<Object> map = new ObjectCharIdentityHashMap<>(SIZE);
        int index = 0;
        Random rnd = new Random(12345L);

        @Setup(Level.Trial)
        public void setUp() {
            for (int i = 0; i < SIZE; i++) {
                keys[i] = new Object();
                values[i] = (char) rnd.nextInt(Character.MAX_VALUE + 1);
                map.put(keys[i], values[i]);
            }
        }

        Object nextKey() {
            int i = index;
            index = (index + 1) & (SIZE - 1);
            return keys[i];
        }
    }

    @Benchmark
    public char benchmarkGet(FilledState s) {
        Object key = s.nextKey();
        return s.map.get(key);
    }

    @Benchmark
    public boolean benchmarkContainsKey(FilledState s) {
        Object key = s.nextKey();
        return s.map.containsKey(key);
    }

    @Benchmark
    public char benchmarkPutOverwrite(FilledState s) {
        Object key = s.nextKey();
        return s.map.put(key, (char) 0);
    }

    @Benchmark
    public char benchmarkPutOrAdd(FilledState s) {
        Object key = s.nextKey();
        return s.map.putOrAdd(key, (char) 1, (char) 1);
    }

    @Benchmark
    public char benchmarkAddTo(FilledState s) {
        Object key = s.nextKey();
        return s.map.addTo(key, (char) 1);
    }

    @Benchmark
    public int benchmarkSize(FilledState s) {
        return s.map.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty(FilledState s) {
        return s.map.isEmpty();
    }

    @Benchmark
    public long benchmarkRamBytesUsed(FilledState s) {
        return s.map.ramBytesUsed();
    }

    @Benchmark
    public Object[] benchmarkKeys(FilledState s) {
        return s.map.keys().toArray();
    }

    @Benchmark
    public char[] benchmarkValues(FilledState s) {
        return s.map.values().toArray();
    }

    @Benchmark
    public char benchmarkIndexGet(FilledState s) {
        Object key = s.nextKey();
        int idx = s.map.indexOf(key);
        return s.map.indexGet(idx);
    }

    @Benchmark
    public void benchmarkIterate(FilledState s, Blackhole bh) {
        for (ObjectCharCursor c : s.map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }
}
