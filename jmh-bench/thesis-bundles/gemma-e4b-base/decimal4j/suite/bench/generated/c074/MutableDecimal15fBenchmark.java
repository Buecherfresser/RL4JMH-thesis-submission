package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.mutable.MutableDecimal15f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.exact.Multipliable15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal15fBenchmark {

    private MutableDecimal15f operandA;
    private MutableDecimal15f operandB;
    private MutableDecimal15f resultA; // Used to hold the result of mutation

    @Setup(Level.Trial)
    public void setup() {
        // Initialize operands with fixed values
        operandA = MutableDecimal15f.ten(); // 10.0
        operandB = MutableDecimal15f.half(); // 0.5
        resultA = MutableDecimal15f.zero();
    }

    // --- Initialization Benchmarks ---

    @Benchmark
    public void initFromLong(Blackhole bh) {
        MutableDecimal15f m = new MutableDecimal15f(123456789012345L);
        bh.consume(m);
    }

    @Benchmark
    public void initFromDouble(Blackhole bh) {
        // Use a value that requires rounding
        MutableDecimal15f m = new MutableDecimal15f(1.2345678901234567);
        bh.consume(m);
    }

    @Benchmark
    public void initFromString(Blackhole bh) {
        // String input
        MutableDecimal15f m = new MutableDecimal15f("123.45678901234567");
        bh.consume(m);
    }

    @Benchmark
    public void initFromBigDecimal(Blackhole bh) {
        // BigDecimal input
        BigDecimal bd = new BigDecimal("987.65432109876543");
        MutableDecimal15f m = new MutableDecimal15f(bd);
        bh.consume(m);
    }

    @Benchmark
    public void initFromDecimal15f(Blackhole bh) {
        // Decimal15f input
        Decimal15f d15f = Decimal15f.valueOf(123.456);
        MutableDecimal15f m = new MutableDecimal15f(d15f);
        bh.consume(m);
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void add(Blackhole bh) {
        // Since addition mutates operandA, we must clone the initial state of A for each invocation
        MutableDecimal15f a = operandA.clone();
        MutableDecimal15f b = operandB.clone();
        
        // Perform operation
        a.add(b);
        bh.consume(a);
    }

    @Benchmark
    public void subtract(Blackhole bh) {
        MutableDecimal15f a = operandA.clone();
        MutableDecimal15f b = operandB.clone();
        
        a.subtract(b);
        bh.consume(a);
    }

    @Benchmark
    public void multiply(Blackhole bh) {
        MutableDecimal15f a = operandA.clone();
        MutableDecimal15f b = operandB.clone();
        
        a.multiply(b);
        bh.consume(a);
    }

    @Benchmark
    public void divide(Blackhole bh) {
        MutableDecimal15f a = operandA.clone();
        MutableDecimal15f b = operandB.clone();
        
        a.divide(b);
        bh.consume(a);
    }

    @Benchmark
    public void negate(Blackhole bh) {
        MutableDecimal15f a = operandA.clone();
        a.negate();
        bh.consume(a);
    }

    @Benchmark
    public void absoluteValue(Blackhole bh) {
        MutableDecimal15f a = operandA.clone();
        a.abs();
        bh.consume(a);
    }

    @Benchmark
    public void square(Blackhole bh) {
        MutableDecimal15f a = operandA.clone();
        a.square();
        bh.consume(a);
    }

    @Benchmark
    public void shiftLeft(Blackhole bh) {
        MutableDecimal15f a = operandA.clone();
        a.shiftLeft(3);
        bh.consume(a);
    }

    @Benchmark
    public void shiftRight(Blackhole bh) {
        MutableDecimal15f a = operandA.clone();
        a.shiftRight(3);
        bh.consume(a);
    }

    // --- Utility and Conversion Benchmarks ---

    @Benchmark
    public void clone(Blackhole bh) {
        MutableDecimal15f cloned = operandA.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void toImmutableDecimal(Blackhole bh) {
        Decimal15f immutable = operandA.toImmutableDecimal();
        bh.consume(immutable);
    }

    @Benchmark
    public void toMutableDecimal(Blackhole bh) {
        // This is a no-op but tests the method call overhead
        MutableDecimal15f mutable = operandA.toMutableDecimal();
        bh.consume(mutable);
    }

    @Benchmark
    public void getUnscaledValue(Blackhole bh) {
        long unscaled = operandA.unscaledValue();
        bh.consume(unscaled);
    }

    @Benchmark
    public void getScale(Blackhole bh) {
        int scale = operandA.getScale();
        bh.consume(scale);
    }

    @Benchmark
    public void getScaleMetrics(Blackhole bh) {
        org.decimal4j.scale.Scale15f metrics = operandA.getScaleMetrics();
        bh.consume(metrics);
    }

    @Benchmark
    public void multiplyExact(Blackhole bh) {
        // This returns a Multipliable15f object
        Multipliable15f multiplier = operandA.multiplyExact();
        bh.consume(multiplier);
    }
}
