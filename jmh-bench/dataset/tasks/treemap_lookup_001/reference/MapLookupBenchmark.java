package bench.generated;

import bench.MapLookup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@State(Scope.Benchmark)
public class MapLookupBenchmark {

    @Param({"50000"})
    public int mapSize;

    @Param({"5000"})
    public int probes;

    private Map<String, Long> map;
    private List<String> keys;

    @Setup
    public void setup() {
        Random rng = new Random(37);
        map = MapLookup.newFastMap(mapSize * 2);
        List<String> all = new ArrayList<>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            String k = "k-" + i;
            map.put(k, (long) i);
            all.add(k);
        }
        keys = new ArrayList<>(probes);
        for (int i = 0; i < probes; i++) {
            keys.add(all.get(rng.nextInt(mapSize)));
        }
    }

    @Benchmark
    public long sumGet() {
        return MapLookup.sumGet(map, keys);
    }
}
