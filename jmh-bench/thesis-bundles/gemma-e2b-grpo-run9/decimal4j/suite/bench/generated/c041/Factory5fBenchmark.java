package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory5fBenchmark {

    // Since Factory5f is an enum singleton, we don't need a @State field for it,
    // but we can use it directly.

    // We use a simple long value for input to avoid static final issues,
    // although for an enum singleton, this is less critical.
    private long testValue;

    @Setup
    public void setup() {
        // Initialize a value that is not static final, though for an enum singleton
        // this is mostly for satisfying the rule of not using static final literals
        // in the benchmark method itself.
        this.testValue = 123456789L;
    }

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory5f.INSTANCE.valueOf(testValue);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory5f.INSTANCE.valueOf(123.45f);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        // Call a method that returns a value (immutable type) with rounding
        Factory5f.INSTANCE.valueOf(123.45f, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigInteger(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory5f.INSTANCE.valueOf(new BigInteger("9876543210"));
        bh.consume(null);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Call a method that parses a string
        Factory5f.INSTANCE.parse("123456789");
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaled(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory5f.INSTANCE.valueOfUnscaled(testValue);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledWithScale(Blackhole bh) {
        // Call a method that returns a value (immutable type) with scale
        Factory5f.INSTANCE.valueOfUnscaled(testValue, 10);
        bh.consume(null);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        // Call a method that returns a mutable instance
        Factory5f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void testNewArray(Blackhole bh) {
        // Call a method that returns an array
        Factory5f.INSTANCE.newArray(5);
        bh.consume(null);
    }
}
