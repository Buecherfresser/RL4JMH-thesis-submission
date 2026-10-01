package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectShortHashMap;
import com.carrotsearch.hppc.procedures.ObjectShortProcedure;
import com.carrotsearch.hppc.predicates.ObjectShortPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectShortHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ObjectShortHashMap<String> map;
        ObjectShortHashMap<String> mapCopy;
        String existingKey;
        String missingKey;
        int existingIndex;

        @Setup(Level.Trial)
        public void setUp() {
            int size = 1024;
            map = new ObjectShortHashMap<>(size);
            for (int i = 0; i < size; i++) {
                String key = "key" + i;
                map.put(key, (short) i);
            }
            existingKey = "key0";
            missingKey = "missing-key";
            existingIndex = map.indexOf(existingKey);
            mapCopy = map.clone();
        }
    }

    @Benchmark
    public short benchGetExisting(BenchmarkState s) {
        return s.map.get(s.existingKey);
    }

    @Benchmark
    public short benchGetMissing(BenchmarkState s) {
        return s.map.get(s.missingKey);
    }

    @Benchmark
    public short benchGetOrDefaultExisting(BenchmarkState s) {
        return s.map.getOrDefault(s.existingKey, (short) -1);
    }

    @Benchmark
    public short benchGetOrDefaultMissing(BenchmarkState s) {
        return s.map.getOrDefault(s.missingKey, (short) -1);
    }

    @Benchmark
    public boolean benchContainsKeyExisting(BenchmarkState s) {
        return s.map.containsKey(s.existingKey);
    }

    @Benchmark
    public boolean benchContainsKeyMissing(BenchmarkState s) {
        return s.map.containsKey(s.missingKey);
    }

    @Benchmark
    public int benchSize(BenchmarkState s) {
        return s.map.size();
    }

    @Benchmark
    public boolean benchIsEmpty(BenchmarkState s) {
        return s.map.isEmpty();
    }

    @Benchmark
    public int benchHashCode(BenchmarkState s) {
        return s.map.hashCode();
    }

    @Benchmark
    public String benchToString(BenchmarkState s) {
        return s.map.toString();
    }

    @Benchmark
    public ObjectShortHashMap<String> benchClone(BenchmarkState s) {
        return s.map.clone();
    }

    @Benchmark
    public void benchForEachProcedure(BenchmarkState s, Blackhole bh) {
        s.map.forEach((ObjectShortProcedure<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchForEachPredicate(BenchmarkState s, Blackhole bh) {
        s.map.forEach((ObjectShortPredicate<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public int benchKeysSize(BenchmarkState s) {
        return s.map.keys().size();
    }

    @Benchmark
    public int benchValuesSize(BenchmarkState s) {
        return s.map.values().size();
    }

    @Benchmark
    public boolean benchEqualsClone(BenchmarkState s) {
        return s.map.equals(s.mapCopy);
    }

    @Benchmark
    public int benchIndexOfExisting(BenchmarkState s) {
        return s.map.indexOf(s.existingKey);
    }

    @Benchmark
    public int benchIndexOfMissing(BenchmarkState s) {
        return s.map.indexOf(s.missingKey);
    }

    @Benchmark
    public short benchIndexGetExisting(BenchmarkState s) {
        return s.map.indexGet(s.existingIndex);
    }
}
