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

    String existingKey;
    String existingValue;
    String missingKey;
    String booleanKey;
    String booleanValue;
    String intKey;
    String intValue;
    String longKey;
    String longValue;

    @Setup(Level.Trial)
    public void setUp() {
        existingKey = "bench.prop.existing";
        existingValue = "existingValue";
        System.setProperty(existingKey, existingValue);

        missingKey = "bench.prop.missing";

        booleanKey = "bench.prop.bool";
        booleanValue = "true";
        System.setProperty(booleanKey, booleanValue);

        intKey = "bench.prop.int";
        intValue = "12345";
        System.setProperty(intKey, intValue);

        longKey = "bench.prop.long";
        longValue = "123456789";
        System.setProperty(longKey, longValue);
    }

    @Benchmark
    public String getExisting() {
        return SystemUtil.get(existingKey);
    }

    @Benchmark
    public String getMissingWithDefault() {
        return SystemUtil.get(missingKey, "defaultValue");
    }

    @Benchmark
    public boolean getBooleanTrue() {
        return SystemUtil.getBoolean(booleanKey, false);
    }

    @Benchmark
    public long getInt() {
        return SystemUtil.getInt(intKey, 0);
    }

    @Benchmark
    public long getLong() {
        return SystemUtil.getLong(longKey, 0L);
    }

    @Benchmark
    public SystemInfo info() {
        return SystemUtil.info();
    }
}
