package bench.generated.c076;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.base.AbstractMutableDecimal;
import org.decimal4j.exact.Multipliable17f;
import org.decimal4j.factory.Factory17f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.mutable.MutableDecimal17f;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal17fBenchmark {

    private MutableDecimal17f mutableDecimal;
    private long testUnscaledValue;
    private BigDecimal testBigDecimal;
    private BigInteger testBigInteger;

    @Setup
    public void setup() {
        // Setup a complex base value for testing
        this.testUnscaledValue = 1234567890123456789L; // Large number
        this.testBigDecimal = new BigDecimal("1234567890123456789.123456789");
        this.testBigInteger = new BigInteger("1234567890123456789");

        // Initialize the mutable decimal object once per trial
        this.mutableDecimal = MutableDecimal17f.unscaled(testUnscaledValue);
    }

    @Benchmark
    public void setUnscaled_Long(Blackhole bh) {
        // Test setting the unscaled value
        mutableDecimal.setUnscaled(testUnscaledValue);
    }

    @Benchmark
    public void setUnscaled_BigDecimal(Blackhole bh) {
        // Test setting the unscaled value from BigDecimal
        mutableDecimal.setUnscaled(testBigDecimal.unscaledValue().longValue());
    }

    @Benchmark
    public void multiplyExact(Blackhole bh) {
        // Test the exact multiplication method
        Multipliable17f result = mutableDecimal.multiplyExact();
        bh.consume(result);
    }

    @Benchmark
    public void toImmutableDecimal(Blackhole bh) {
        // Test conversion to immutable type
        Decimal17f result = mutableDecimal.toImmutableDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void set_String_HalfUp(Blackhole bh) {
        // Test setting value from a string with specific rounding mode
        String input = "1234567890123456789.999999999999999"; // Value requiring rounding
        mutableDecimal.set(input, RoundingMode.HALF_UP);
    }

    @Benchmark
    public void set_Double_HalfUp(Blackhole bh) {
        // Test setting value from a double
        double input = 1234567890123456789.123456789;
        mutableDecimal.set(input, RoundingMode.HALF_UP);
    }

    @Benchmark
    public void set_BigInteger(Blackhole bh) {
        // Test setting value from BigInteger
        mutableDecimal.set(testBigInteger.toString(), RoundingMode.HALF_UP);
    }

    @Benchmark
    public void zero(Blackhole bh) {
        // Test static factory method
        MutableDecimal17f result = MutableDecimal17f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void one(Blackhole bh) {
        // Test static factory method
        MutableDecimal17f result = MutableDecimal17f.one();
        bh.consume(result);
    }

    @Benchmark
    public void ten(Blackhole bh) {
        // Test static factory method
        MutableDecimal17f result = MutableDecimal17f.ten();
        bh.consume(result);
    }

    @Benchmark
    public void half(Blackhole bh) {
        // Test static factory method
        MutableDecimal17f result = MutableDecimal17f.half();
        bh.consume(result);
    }
}
