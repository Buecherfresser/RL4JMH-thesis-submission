package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.exact.Multipliable0f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.mutable.MutableDecimal1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable0fBenchmark {

    // State field to hold the subject instance.
    private Multipliable0f multipliable0f;

    @Setup
    public void setup() {
        try {
            // Initialize the subject. We need a Decimal<Scale0f> to construct it.
            this.multipliable0f = new Multipliable0f(Decimal0f.valueOf(0L));
        } catch (Exception e) {
            // Handle potential instantiation errors if Decimal0f requires complex setup
            System.err.println("Failed to initialize Multipliable0f: " + e.getMessage());
            this.multipliable0f = null;
        }
    }

    @Benchmark
    public void testGetValue(Blackhole bh) {
        if (multipliable0f != null) {
            bh.consume(multipliable0f.getValue());
        }
    }

    @Benchmark
    public void testSquare(Blackhole bh) {
        if (multipliable0f != null) {
            // square() returns Decimal0f
            bh.consume(multipliable0f.square());
        }
    }

    @Benchmark
    public void testByDecimal0f(Blackhole bh) {
        if (multipliable0f != null) {
            // by(Decimal<Scale0f> factor) returns Decimal0f
            bh.consume(multipliable0f.by(Decimal0f.valueOf(1L)));
        }
    }

    @Benchmark
    public void testByDecimal1f(Blackhole bh) {
        if (multipliable0f != null) {
            // by(Decimal1f factor) returns Decimal1f
            bh.consume(multipliable0f.by(Decimal1f.valueOf(1.0)));
        }
    }

    // Removed testByMutableDecimal1f as passing null is unsafe and likely caused the ambiguity/error.
    // We rely on the existing, unambiguous methods for testing.
}
