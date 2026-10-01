package bench;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import java.util.HashMap;
import java.util.Map;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class TypeCacheBenchmark {

    @Param({"256"})
    public int entries;

    @Param({"128"})
    public int lookups;

    private TypeCache cache;
    private TypeCache.Key[] keys;

    @Setup
    public void setup() {
        Map<TypeCache.Key, Integer> seed = new HashMap<>();
        for (int i = 0; i < entries; i++) seed.put(new TypeCache.Key("a" + i, "b" + i), i);
        cache = new TypeCache(seed);
        keys = new TypeCache.Key[lookups];
        for (int i = 0; i < lookups; i++) keys[i] = new TypeCache.Key("a" + (i % entries), "b" + (i % entries));
    }

    @Benchmark
    public void lookup(Blackhole bh) {
        for (int i = 0; i < lookups; i++) bh.consume(cache.lookup(keys[i]));
    }
}
