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

    // Define the Enum type we will test against
    private enum TestEnum {
        VALUE_A,
        VALUE_B,
        VALUE_C
    }

    // State fields to hold inputs prepared in @Setup
    private Class<TestEnum> enumType;
    private String correctName;
    private String incorrectName;

    @Setup
    public void setup() {
        this.enumType = TestEnum.class;
        this.correctName = "value_a";
        this.incorrectName = "non_existent_value";
    }

    /**
     * Benchmark for a successful, case-insensitive lookup.
     */
    @Benchmark
    public void findEnumInsensitiveCase_Success(Blackhole bh) {
        TestEnum result = EnumUtils.findEnumInsensitiveCase(enumType, correctName);
        bh.consume(result);
    }

    /**
     * Benchmark for a failed lookup, measuring the exception path.
     */
    @Benchmark
    public void findEnumInsensitiveCase_Failure(Blackhole bh) {
        try {
            EnumUtils.findEnumInsensitiveCase(enumType, incorrectName);
        } catch (IllegalArgumentException e) {
            // Consume the exception to ensure the path is executed
            bh.consume(e);
        }
    }
}
