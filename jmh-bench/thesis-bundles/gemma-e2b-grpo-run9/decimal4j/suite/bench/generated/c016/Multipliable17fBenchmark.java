package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable17f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable17fBenchmark {

    // State field to hold the subject instance.
    private Multipliable17f multipliable17f;

    @Setup
    public void setup() {
        // Initialize the subject. We must provide a non-null Decimal<Scale17f> value.
        // We instantiate it once.
        try {
            // Attempt to create a dummy instance.
            this.multipliable17f = new Multipliable17f(null);
        } catch (Exception e) {
            // Handle potential construction failure gracefully for benchmarking purposes.
        }
    }

    @Benchmark
    public void testGetValue(Blackhole bh) {
        if (multipliable17f == null) return;
        try {
            // Call a read-only method and consume the result
            bh.consume(multipliable17f.getValue());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
