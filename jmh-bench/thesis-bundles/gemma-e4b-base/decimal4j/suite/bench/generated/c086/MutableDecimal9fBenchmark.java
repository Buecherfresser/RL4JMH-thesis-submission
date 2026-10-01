package bench.generated.c086;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.scale.Scale9f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable9f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal9fBenchmark {

    private MutableDecimal9f valueA;
    private MutableDecimal9f valueB;
    private MutableDecimal9f largeValue;
    private BigDecimal bigDecimalInput;
    private String stringInput;

    @Setup
    public void setup() {
        // Setup for basic arithmetic and construction
        valueA = MutableDecimal9f.ten(); // 10.0
        valueB = MutableDecimal9f.five(); // 5.0

        // Setup for large/complex inputs
        // A large number that fits within long limits but tests construction
        largeValue = MutableDecimal9f.unscaled(Long.MAX_VALUE / 1000); 

        // Setup for BigDecimal construction
        bigDecimalInput = new BigDecimal("123456789.123456789");
        
        // Setup for String construction
        stringInput = "987654321.000000001";
    }

    // --- Constructor Benchmarks ---

    @Benchmark
    public MutableDecimal9f benchmarkConstructorLong(Blackhole bh) {
        // Test MutableDecimal9f(long value)
        MutableDecimal9f result = new MutableDecimal9f(123456789L);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal9f benchmarkConstructorDouble(Blackhole bh) {
        // Test MutableDecimal9f(double value)
        MutableDecimal9f result = new MutableDecimal9f(123.4567890123);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal9f benchmarkConstructorBigDecimal(Blackhole bh) {
        // Test MutableDecimal9f(BigDecimal value)
        MutableDecimal9f result = new MutableDecimal9f(bigDecimalInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal9f benchmarkConstructorString(Blackhole bh) {
        // Test MutableDecimal9f(String value)
        MutableDecimal9f result = new MutableDecimal9f(stringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal9f benchmarkStaticUnscaled(Blackhole bh) {
        // Test MutableDecimal9f.unscaled(long unscaledValue)
        MutableDecimal9f result = MutableDecimal9f.unscaled(987654321L);
        bh.consume(result);
        return result;
    }

    // --- Accessor/Utility Benchmarks ---

    @Benchmark
    public MutableDecimal9f benchmarkClone(Blackhole bh) {
        // Test clone()
        MutableDecimal9f result = valueA.clone();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal9f benchmarkToImmutableDecimal(Blackhole bh) {
        // Test toImmutableDecimal()
        Decimal9f result = valueA.toImmutableDecimal();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal9f benchmarkToMutableDecimal(Blackhole bh) {
        // Test toMutableDecimal()
        MutableDecimal9f result = valueA.toMutableDecimal();
        bh.consume(result);
        return result;
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public MutableDecimal9f benchmarkAdd(Blackhole bh) {
        // Test add()
        MutableDecimal9f result = valueA.add(valueB);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal9f benchmarkSubtract(Blackhole bh) {
        // Test subtract()
        MutableDecimal9f result = valueA.subtract(valueB);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal9f benchmarkMultiply(Blackhole bh) {
        // Test multiply()
        MutableDecimal9f result = valueA.multiply(valueB);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal9f benchmarkDivide(Blackhole bh) {
        // Test divide()
        MutableDecimal9f result = valueA.divide(valueB);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal9f benchmarkNegate(Blackhole bh) {
        // Test negate()
        MutableDecimal9f result = valueA.negate();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal9f benchmarkSquare(Blackhole bh) {
        // Test square()
        MutableDecimal9f result = valueA.square();
        bh.consume(result);
        return result;
    }

    // --- Specialized Method Benchmarks ---

    @Benchmark
    public Multipliable9f benchmarkMultiplyExact(Blackhole bh) {
        // Test multiplyExact()
        Multipliable9f multiplier = valueA.multiplyExact();
        // We must consume the result of the fluent call to ensure the operation runs
        multiplier.by(valueB); 
        bh.consume(multiplier);
        return multiplier;
    }
}
