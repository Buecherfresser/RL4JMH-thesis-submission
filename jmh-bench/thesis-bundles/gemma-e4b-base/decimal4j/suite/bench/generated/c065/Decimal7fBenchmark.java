package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal7f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale7f;
import org.decimal4j.factory.Factory7f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.exact.Multipliable7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal7fBenchmark {

    private long testLong;
    private double testDouble;
    private BigDecimal testBigDecimal;
    private BigInteger testBigInteger;
    private Decimal testDecimal;
    private long testUnscaled;
    private Decimal7f constantDecimal;

    @Setup(Level.Trial)
    public void setup() {
        // Inputs for conversion methods (using smaller values to avoid compilation issues)
        testLong = 12345L;
        testDouble = 123.456789;
        testBigDecimal = new BigDecimal("987.1234567");
        testBigInteger = BigInteger.valueOf(123456789L);
        
        // Create a mock Decimal input
        testDecimal = Decimal7f.valueOf(testDouble); 
        
        // Input for unscaled value conversion
        testUnscaled = 1234567L;
        
        // Constant instance for operations
        constantDecimal = Decimal7f.valueOf(10.0);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfLong(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(testLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal7f benchmarkValueOfDouble(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(testDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal7f benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(testBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal7f benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(testBigInteger);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal7f benchmarkValueOfDecimal(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOf(testDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal7f benchmarkValueOfUnscaled(Blackhole bh) {
        Decimal7f result = Decimal7f.valueOfUnscaled(testUnscaled);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Multipliable7f benchmarkMultiplyExact(Blackhole bh) {
        Multipliable7f result = constantDecimal.multiplyExact();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal7f benchmarkToMutableDecimal(Blackhole bh) {
        MutableDecimal7f result = constantDecimal.toMutableDecimal();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal7f benchmarkToImmutableDecimal(Blackhole bh) {
        Decimal7f result = constantDecimal.toImmutableDecimal();
        bh.consume(result);
        return result;
    }
}
