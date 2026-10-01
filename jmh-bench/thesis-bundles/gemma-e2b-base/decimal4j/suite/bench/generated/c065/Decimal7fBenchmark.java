package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factory7f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.scale.Scale7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal7fBenchmark {

    private Decimal7f testDecimal;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private double doubleInput;
    private BigInteger bigIntegerInput;
    private Decimal<?> decimalInput;

    @Setup(Level.Trial)
    public void setup() {
        // Setup a representative Decimal7f instance for general operations
        this.testDecimal = Decimal7f.valueOf(123.4567890);

        // Setup inputs for various conversion types
        this.bigDecimalInput = new BigDecimal("1234567890123456789"); // Large number
        this.stringInput = "123.4567890123456789"; // String representation
        this.doubleInput = 123.456789;
        this.bigIntegerInput = new BigInteger("9876543210987654321");
        
        // Setup a Decimal<?> input (using the testDecimal itself for simplicity)
        this.decimalInput = testDecimal;
    }

    @Benchmark
    public void valueOf_Long(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(1234567890L);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_Double(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_BigDecimal_Default(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_String(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_BigInteger(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(bigIntegerInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_Decimal(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(decimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaled_NoRounding(Blackhole bh) {
        long unscaledValue = 1234567890L;
        Decimal7f result = Decimal7f.valueOfUnscaled(unscaledValue, Decimal7f.SCALE);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaled_WithRounding_HalfUp(Blackhole bh) {
        // Test rounding behavior on unscaled value
        long unscaledValue = 12345678901L; // Value that requires rounding if scale is applied
        Decimal7f result = Decimal7f.valueOfUnscaled(unscaledValue, Decimal7f.SCALE, RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyExact(Blackhole bh) {
        // Test the multiplyExact method which returns a Multipliable7f
        org.decimal4j.exact.Multipliable7f multiplier = testDecimal.multiplyExact();
        bh.consume(multiplier);
    }
}
