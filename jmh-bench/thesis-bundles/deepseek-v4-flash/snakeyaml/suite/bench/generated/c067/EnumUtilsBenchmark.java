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

    private enum SmallEnum { VALUE1, VALUE2, VALUE3 }
    private enum LargeEnum { A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T }

    private Class<SmallEnum> smallEnumClass;
    private Class<LargeEnum> largeEnumClass;
    private String smallExactName;
    private String smallDifferentCaseName;
    private String smallNonExistentName;
    private String largeExactName;
    private String largeDifferentCaseName;
    private String largeNonExistentName;
    private String largeLastName;

    @Setup(Level.Trial)
    public void setup() {
        smallEnumClass = SmallEnum.class;
        largeEnumClass = LargeEnum.class;
        smallExactName = "VALUE1";
        smallDifferentCaseName = "value1";
        smallNonExistentName = "NOPE";
        largeExactName = "A";
        largeDifferentCaseName = "a";
        largeNonExistentName = "NOPE";
        largeLastName = "T";
    }

    @Benchmark
    public SmallEnum smallExactCase() {
        return EnumUtils.findEnumInsensitiveCase(smallEnumClass, smallExactName);
    }

    @Benchmark
    public SmallEnum smallDifferentCase() {
        return EnumUtils.findEnumInsensitiveCase(smallEnumClass, smallDifferentCaseName);
    }

    @Benchmark
    public void smallNonExistent(Blackhole bh) {
        try {
            EnumUtils.findEnumInsensitiveCase(smallEnumClass, smallNonExistentName);
        } catch (IllegalArgumentException e) {
            bh.consume(e);
        }
    }

    @Benchmark
    public LargeEnum largeExactCase() {
        return EnumUtils.findEnumInsensitiveCase(largeEnumClass, largeExactName);
    }

    @Benchmark
    public LargeEnum largeDifferentCase() {
        return EnumUtils.findEnumInsensitiveCase(largeEnumClass, largeDifferentCaseName);
    }

    @Benchmark
    public LargeEnum largeLastConstant() {
        return EnumUtils.findEnumInsensitiveCase(largeEnumClass, largeLastName);
    }

    @Benchmark
    public void largeNonExistent(Blackhole bh) {
        try {
            EnumUtils.findEnumInsensitiveCase(largeEnumClass, largeNonExistentName);
        } catch (IllegalArgumentException e) {
            bh.consume(e);
        }
    }
}
