package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.exact.Multipliable10f;
import org.decimal4j.factory.Factory10f;
import org.decimal4j.mutable.MutableDecimal10f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal10fBenchmark {

    // State fields for read-only operations or reusable immutable objects
    private Decimal10f immutableDecimal;

    @Setup
    public void setup() {
        // Initialize a standard Decimal10f instance for use in benchmarks
        try {
            this.immutableDecimal = Decimal10f.ONE;
        } catch (Exception e) {
            // Ignore exceptions during setup
        }
    }

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Test static factory method with a small, non-constant long
        try {
            Decimal10f result = Decimal10f.valueOf(123456789L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        // Test static factory method with a float
        try {
            Decimal10f result = Decimal10f.valueOf(1.2345f);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Test static factory method with a double
        try {
            Decimal10f result = Decimal10f.valueOf(123.456789);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testValueOfUnscaled(Blackhole bh) {
        // Test static factory method for unscaled value
        try {
            Decimal10f result = Decimal10f.valueOfUnscaled(123456789L, 5);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testValueOfUnscaledZero(Blackhole bh) {
        // Test edge case: zero
        try {
            Decimal10f result = Decimal10f.valueOfUnscaled(0L, 10);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testValueOfUnscaledOne(Blackhole bh) {
        // Test edge case: one
        try {
            Decimal10f result = Decimal10f.valueOfUnscaled(1L, 10);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testMultiplyExact(Blackhole bh) {
        // Test multiplyExact which returns a Multipliable10f
        try {
            Multipliable10f multiplier = immutableDecimal.multiplyExact();
            bh.consume(multiplier);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testToMutableDecimal(Blackhole bh) {
        // Test conversion to mutable
        try {
            MutableDecimal10f mutableDec = immutableDecimal.toMutableDecimal();
            bh.consume(mutableDec);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testToImmutableDecimal(Blackhole bh) {
        // Test conversion to immutable (should return self)
        try {
            Decimal10f result = immutableDecimal.toImmutableDecimal();
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
