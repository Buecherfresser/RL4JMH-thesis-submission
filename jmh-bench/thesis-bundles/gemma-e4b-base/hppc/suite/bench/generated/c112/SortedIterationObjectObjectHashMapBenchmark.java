package bench.generated.c112;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.procedures.ObjectObjectProcedure;
import com.carrotsearch.hppc.predicates.ObjectObjectPredicate;
import java.util.Comparator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectObjectHashMapBenchmark {

    private SortedIterationObjectObjectHashMap<String, String> sortedMap;
    private ObjectObjectHashMap<String, String> delegateMap;
    private String keyToLookup;
    private String valueToLookup;
    private String[] keys;
    private String[] values;
    private int mapSize;

    @Setup(Level.Trial)
    public void setup() {
        // Use a fixed size for consistent benchmarking
        mapSize = 10000;
        
        // 1. Setup Delegate Map
        delegateMap = new ObjectObjectHashMap<>(mapSize);
        keys = new String[mapSize];
        values = new String[mapSize];

        // Populate the map
        for (int i = 0; i < mapSize; i++) {
            String key = "Key_" + i;
            String value = "Value_" + i;
            delegateMap.put(key, value);
            keys[i] = key;
            values[i] = value;
        }

        // 2. Setup SUT (Sorted View)
        // Since keys are Strings, we use natural ordering
        Comparator<String> comparator = Comparator.naturalOrder();
        sortedMap = new SortedIterationObjectObjectHashMap<>(delegateMap, comparator);

        // 3. Setup lookup targets
        keyToLookup = keys[mapSize / 2];
        valueToLookup = values[mapSize / 2];
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        String result = sortedMap.get(keyToLookup);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        boolean result = sortedMap.containsKey(keyToLookup);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        int result = sortedMap.size();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIsEmpty(Blackhole bh) {
        boolean result = sortedMap.isEmpty();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIterationForEachProcedure(Blackhole bh) {
        ObjectObjectProcedure<String, String> procedure = (k, v) -> {
            // Dummy operation
        };
        sortedMap.forEach(procedure);
        bh.consume(procedure);
    }

    @Benchmark
    public void benchmarkIterationForEachPredicate(Blackhole bh) {
        ObjectObjectPredicate<String, String> predicate = (k, v) -> {
            // Dummy operation
            return true;
        };
        sortedMap.forEach(predicate);
        bh.consume(predicate);
    }
}
