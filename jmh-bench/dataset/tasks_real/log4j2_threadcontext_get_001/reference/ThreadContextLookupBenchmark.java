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
public class ThreadContextLookupBenchmark {

    @Param({"16"})
    public int entries;

    @Param({"128"})
    public int lookups;

    private Map<String, String> ctx;
    private String[] keys;

    @Setup
    public void setup() {
        ctx = new HashMap<>();
        for (int i = 0; i < entries; i++) ctx.put("k" + i, "v" + i);
        keys = new String[lookups];
        for (int i = 0; i < lookups; i++) keys[i] = "absent" + i;
    }

    @Benchmark
    public void get(Blackhole bh) {
        for (int i = 0; i < lookups; i++) bh.consume(ThreadContextLookup.get(ctx, keys[i]));
    }
}
