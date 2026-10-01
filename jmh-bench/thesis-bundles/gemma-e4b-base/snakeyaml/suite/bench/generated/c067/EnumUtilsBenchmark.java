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

    // Define a simple enum for testing purposes
    public enum TestEnum {
        RED, GREEN, BLUE, YELLOW
    }

    private Class<TestEnum> enumType;
    private String searchName;

    @Setup(Level.Trial)
    public void setup() {
        // Setup for a typical case: searching for a name that matches case-insensitively
        this.enumType = TestEnum.class;
        this.searchName = "green"; // Test case insensitive matching
    }

    @Benchmark
    public TestEnum findEnumInsensitiveCase_CaseInsensitiveMatch(Blackhole bh) {
        // The method is static, so no instance state is needed for the SUT.
        TestEnum result = EnumUtils.findEnumInsensitiveCase(enumType, searchName);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public TestEnum findEnumInsensitiveCase_ExactMatch(Blackhole bh) {
        // Setup for an exact match case
        this.searchName = "RED";
        TestEnum result = EnumUtils.findEnumInsensitiveCase(enumType, searchName);
        bh.consume(result);
        return result;
    }
}
