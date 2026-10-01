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

    // Since EnumUtils is static, no instance state is strictly required.
    // We rely on the static method call within the benchmark methods.

    /**
     * Benchmark for finding an enum constant case-insensitively when a match exists.
     * This tests the iteration and comparison logic of the method.
     */
    @Benchmark
    public void testFindEnumInsensitiveCase_Success(Blackhole bh) {
        try {
            // Test against a known enum type (e.g., java.lang.Enum)
            // We use a simple, standard enum for testing purposes.
            // Note: This relies on the necessary classes being available on the classpath.
            EnumUtils.findEnumInsensitiveCase(Enum.class, "UNKNOWN");
        } catch (IllegalArgumentException e) {
            // Expected to not happen for a successful test case
        }
        bh.consume(null);
    }

    /**
     * Benchmark for finding an enum constant case-insensitively when no match exists,
     * expecting an exception. This tests the loop termination and exception throwing path.
     */
    @Benchmark
    public void testFindEnumInsensitiveCase_Failure(Blackhole bh) {
        try {
            // Attempt to find a constant that definitely doesn't exist
            EnumUtils.findEnumInsensitiveCase(Enum.class, "NON_EXISTENT_CONSTANT");
        } catch (IllegalArgumentException e) {
            // Expected behavior
        }
        bh.consume(null);
    }
}
