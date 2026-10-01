package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortDoubleHashMap;
import com.carrotsearch.hppc.cursors.ShortDoubleCursor;
import com.carrotsearch.hppc.procedures.ShortDoubleProcedure;
import com.carrotsearch.hppc.predicates.ShortDoublePredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortDoubleHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class MapState {
        ShortDoubleHashMap map;
        short existingKey;
        short nonExistingKey;
        double existingValue;

        @Setup(Level.Trial)
        public void setUp() {
            int size = 1024;
            map = new ShortDoubleHashMap(size);
            Random rnd = new Random(0);
            for (int i = 1; i <= size; i++) {
                short k = (short) i;
                double v = rnd.nextDouble();
                map.put(k, v);
                if (i == 1) {
                    existingKey = k;
                    existingValue = v;
                }
            }
            nonExistingKey = (short) (size + 1);
        }
    }

    @Benchmark
    public double getExisting(MapState s) {
        return s.map.get(s.existingKey);
    }

    @Benchmark
    public double getNonExisting(MapState s) {
        return s.map.get(s.nonExistingKey);
    }

    @Benchmark
    public boolean containsKeyExisting(MapState s) {
        return s.map.containsKey(s.existingKey);
    }

    @Benchmark
    public boolean containsKeyNonExisting(MapState s) {
        return s.map.containsKey(s.nonExistingKey);
    }

    @Benchmark
    public double putExisting(MapState s) {
        return s.map.put(s.existingKey, 123.456);
    }

    @Benchmark
    public void putNew(MapState s, Blackhole bh) {
        ShortDoubleHashMap m = s.map.clone();
        double prev = m.put(s.nonExistingKey, 1.0);
        bh.consume(prev);
    }

    @Benchmark
    public double putOrAddExisting(MapState s) {
        return s.map.putOrAdd(s.existingKey, 1.0, 2.0);
    }

    @Benchmark
    public double addToExisting(MapState s) {
        return s.map.addTo(s.existingKey, 1.0);
    }

    @Benchmark
    public void removeExisting(MapState s, Blackhole bh) {
        ShortDoubleHashMap m = s.map.clone();
        double prev = m.remove(s.existingKey);
        bh.consume(prev);
    }

    @Benchmark
    public double removeNonExisting(MapState s) {
        return s.map.remove(s.nonExistingKey);
    }

    @Benchmark
    public void clear(MapState s, Blackhole bh) {
        ShortDoubleHashMap m = s.map.clone();
        m.clear();
        bh.consume(m.size());
    }

    @Benchmark
    public int cloneSize(MapState s) {
        ShortDoubleHashMap cloned = s.map.clone();
        return cloned.size();
    }

    @Benchmark
    public void forEachProcedure(MapState s, Blackhole bh) {
        s.map.forEach(new ShortDoubleProcedure() {
            @Override
            public void apply(short key, double value) {
                bh.consume(key);
                bh.consume(value);
            }
        });
    }

    @Benchmark
    public void forEachPredicate(MapState s, Blackhole bh) {
        s.map.forEach(new ShortDoublePredicate() {
            @Override
            public boolean apply(short key, double value) {
                bh.consume(key);
                bh.consume(value);
                return true;
            }
        });
    }

    @Benchmark
    public void iteratorLoop(MapState s, Blackhole bh) {
        for (ShortDoubleCursor c : s.map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public boolean valuesContains(MapState s) {
        return s.map.values().contains(s.existingValue);
    }

    @Benchmark
    public boolean keysContains(MapState s) {
        return s.map.keys().contains(s.existingKey);
    }
}
