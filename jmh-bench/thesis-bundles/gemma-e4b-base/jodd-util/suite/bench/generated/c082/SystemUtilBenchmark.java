package bench.generated.c082;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.SystemUtil;
import jodd.util.SystemInfo;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SystemUtilBenchmark {

    private String propertyName;
    private String defaultStringValue;
    private String booleanPropertyName;
    private String intPropertyName;
    private String longPropertyName;

    @Setup(Level.Trial)
    public void setup() {
        // Inputs for general string retrieval
        this.propertyName = "jodd.test.prop";
        this.defaultStringValue = "default_value";

        // Inputs for type conversion
        this.booleanPropertyName = "jodd.test.bool";
        this.intPropertyName = "jodd.test.int";
        this.longPropertyName = "jodd.test.long";
    }

    @Benchmark
    public String benchmarkGetSimple(Blackhole bh) {
        String result = SystemUtil.get(propertyName);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkGetWithDefault(Blackhole bh) {
        String result = SystemUtil.get(propertyName, defaultStringValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean benchmarkGetBoolean(Blackhole bh) {
        boolean result = SystemUtil.getBoolean(booleanPropertyName, false);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkGetInt(Blackhole bh) {
        // Note: getInt returns long, even if the input is an int
        long result = SystemUtil.getInt(intPropertyName, 0);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkGetLong(Blackhole bh) {
        long result = SystemUtil.getLong(longPropertyName, 0L);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public SystemInfo benchmarkInfo(Blackhole bh) {
        SystemInfo result = SystemUtil.info();
        bh.consume(result);
        return result;
    }
}
