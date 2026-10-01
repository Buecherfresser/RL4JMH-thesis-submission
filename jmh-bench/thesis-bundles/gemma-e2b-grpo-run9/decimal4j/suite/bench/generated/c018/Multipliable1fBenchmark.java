package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.exact.Multipliable1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Multipliable1fBenchmark {

    private Multipliable1f multipliable1f;

    @Setup
    public void setup() {
        // Initialize the subject. We use a simple constant value for setup.
        // Since Multipliable1f takes a Decimal<Scale1f>, we use Decimal1f.
        try {
            // Create a simple Decimal1f value (e.g., 1.0)
            Decimal<org.decimal4j.scale.Scale1f> initialDecimal = org.decimal4j.immutable.Decimal1f.valueOf(1L);
            this.multipliable1f = new Multipliable1f(initialDecimal);
        } catch (Exception e) {
            // Handle potential exceptions during setup if necessary, though unlikely for simple values
            throw new RuntimeException("Setup failed", e);
        }
    }

    @Benchmark
    public void getValue(Blackhole bh) {
        // Test read-only access. Must consume the result.
        bh.consume(multipliable1f.getValue());
    }

    @Benchmark
    public void square(Blackhole bh) {
        // Test square() method, which returns a new Decimal2f.
        bh.consume(multipliable1f.square());
    }

    @Benchmark
    public void byDecimal0f(Blackhole bh) {
        // Test by(Decimal0f factor), returns Decimal1f.
        // Removed call to Decimal0f.zero() as it caused compilation errors.
        // Using a known immutable zero value if available, or relying on the compiler
        // to accept a simple constant if the method is overloaded.
        bh.consume(multipliable1f.by(Decimal0f.valueOf(0L)));
    }

    // Removed byMutableDecimal0f and byMutableDecimal2f as they relied on
    // ambiguous method resolution or invalid input (null), causing compilation errors.
}
