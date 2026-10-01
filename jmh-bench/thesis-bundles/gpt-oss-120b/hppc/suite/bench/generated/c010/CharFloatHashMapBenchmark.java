package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.CharFloatHashMap;
import com.carrotsearch.hppc.cursors.CharFloatCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.procedures.CharFloatProcedure;
import com.carrotsearch.hppc.predicates.CharFloatPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharFloatHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class SharedState {
        CharFloatHashMap map;
        char[] keys;
        float[] values;
        int size = 1024;
        int idx = 0;

        @Setup(Level.Trial)
        public void setUp() {
            map = new CharFloatHashMap(size);
            keys = new char[size];
            values = new float[size];
            for (int i = 0; i < size; i++) {
                char k = (char) i;
                float v = i * 1.0f;
                map.put(k, v);
                keys[i] = k;
                values[i] = v;
            }
        }

        char nextKey() {
            char k = keys[idx];
            idx = (idx + 1) % size;
            return k;
        }

        float nextValue() {
            int i = (idx == 0) ? size - 1 : idx - 1;
            return values[i] + 1.0f;
        }

        int nextIndex() {
            return map.indexOf(nextKey());
        }
    }

    @State(Scope.Benchmark)
    public static class ClearState {
        CharFloatHashMap map;
        int size = 1024;

        @Setup(Level.Trial)
        public void setUp() {
            map = new CharFloatHashMap(size);
            for (int i = 0; i < size; i++) {
                map.put((char) i, i * 1.0f);
            }
        }
    }

    @Benchmark
    public float benchmarkPutReplace(SharedState s) {
        char k = s.nextKey();
        float v = s.nextValue();
        return s.map.put(k, v);
    }

    @Benchmark
    public float benchmarkGet(SharedState s) {
        char k = s.nextKey();
        return s.map.get(k);
    }

    @Benchmark
    public float benchmarkGetOrDefault(SharedState s) {
        char k = s.nextKey();
        return s.map.getOrDefault(k, -1.0f);
    }

    @Benchmark
    public boolean benchmarkContainsKey(SharedState s) {
        char k = s.nextKey();
        return s.map.containsKey(k);
    }

    @Benchmark
    public float benchmarkRemove(SharedState s) {
        char k = s.nextKey();
        return s.map.remove(k);
    }

    @Benchmark
    public float benchmarkPutOrAdd(SharedState s) {
        char k = s.nextKey();
        return s.map.putOrAdd(k, 1.0f, 2.0f);
    }

    @Benchmark
    public float benchmarkAddTo(SharedState s) {
        char k = s.nextKey();
        return s.map.addTo(k, 1.5f);
    }

    @Benchmark
    public int benchmarkIndexOf(SharedState s) {
        char k = s.nextKey();
        return s.map.indexOf(k);
    }

    @Benchmark
    public float benchmarkIndexGet(SharedState s) {
        int idx = s.nextIndex();
        if (idx < 0) {
            return Float.NaN;
        }
        return s.map.indexGet(idx);
    }

    @Benchmark
    public float benchmarkIndexReplace(SharedState s) {
        int idx = s.nextIndex();
        if (idx < 0) {
            return Float.NaN;
        }
        return s.map.indexReplace(idx, 42.0f);
    }

    @Benchmark
    public void benchmarkForEachProcedure(SharedState s, Blackhole bh) {
        s.map.forEach((CharFloatProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(SharedState s, Blackhole bh) {
        s.map.forEach((CharFloatPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkIterator(SharedState s, Blackhole bh) {
        for (CharFloatCursor c : s.map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkKeysIteration(SharedState s, Blackhole bh) {
        for (CharCursor c : s.map.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIteration(SharedState s, Blackhole bh) {
        for (FloatCursor c : s.map.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int benchmarkClone(SharedState s) {
        CharFloatHashMap cloned = s.map.clone();
        return cloned.size();
    }

    @Benchmark
    public void benchmarkClear(ClearState s, Blackhole bh) {
        s.map.clear();
        bh.consume(s.map.size());
    }
}
