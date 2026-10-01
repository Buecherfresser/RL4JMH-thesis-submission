package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.util.EnumUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class EnumUtilsBenchmark {

    private enum SampleEnum {
        ALPHA, BETA, GAMMA, DELTA
    }

    private enum LargeEnum {
        VAL0, VAL1, VAL2, VAL3, VAL4, VAL5, VAL6, VAL7, VAL8, VAL9,
        VAL10, VAL11, VAL12, VAL13, VAL14, VAL15, VAL16, VAL17, VAL18, VAL19,
        VAL20, VAL21, VAL22, VAL23, VAL24, VAL25, VAL26, VAL27, VAL28, VAL29,
        VAL30, VAL31, VAL32, VAL33, VAL34, VAL35, VAL36, VAL37, VAL38, VAL39,
        VAL40, VAL41, VAL42, VAL43, VAL44, VAL45, VAL46, VAL47, VAL48, VAL49
    }

    private String lowerCaseName;
    private String mixedCaseName;
    private String notFoundName;
    private String lastName;

    @Setup(Level.Trial)
    public void setup() {
        lowerCaseName = "alpha";
        mixedCaseName = "BeTa";
        notFoundName = "epsilon";
        lastName = "val49";
    }

    @Benchmark
    public SampleEnum findEnumLowerCase() {
        return EnumUtils.findEnumInsensitiveCase(SampleEnum.class, lowerCaseName);
    }

    @Benchmark
    public SampleEnum findEnumMixedCase() {
        return EnumUtils.findEnumInsensitiveCase(SampleEnum.class, mixedCaseName);
    }

    @Benchmark
    public boolean findEnumNotFound(Blackhole bh) {
        try {
            EnumUtils.findEnumInsensitiveCase(SampleEnum.class, notFoundName);
            return false;
        } catch (IllegalArgumentException e) {
            bh.consume(e);
            return true;
        }
    }

    @Benchmark
    public LargeEnum findEnumLarge() {
        return EnumUtils.findEnumInsensitiveCase(LargeEnum.class, lastName);
    }
}
