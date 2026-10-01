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
public class TokenStoreBenchmark {

    @Param({"16"})
    public int entries;

    @Param({"32"})
    public int lookups;

    private TokenStore store;
    private String[] keys;

    @Setup
    public void setup() {
        Map<String, String> seed = new HashMap<>();
        for (int i = 0; i < entries; i++) seed.put("k" + i, "v" + i);
        store = new TokenStore(seed);
        keys = new String[lookups];
        for (int i = 0; i < lookups; i++) keys[i] = "k" + (i % entries);
    }

    @Benchmark
    public void lookup(Blackhole bh) {
        for (int i = 0; i < lookups; i++) bh.consume(store.lookup(keys[i]));
    }
}
