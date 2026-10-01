package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.mutable.MutableDecimal15f;
import org.decimal4j.immutable.Decimal15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal15fBenchmark {

    private MutableDecimal15f mutableDecimal;

    // Setup: Initialize common inputs and objects
    @Setup
    public void setup() {
        // Initialize a base mutable decimal value (e.g., 1.234567890123456)
        mutableDecimal = new MutableDecimal15f(1.234567890123456);
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void add(Blackhole bh) {
        MutableDecimal15f result = mutableDecimal.add(MutableDecimal15f.ten());
        bh.consume(result);
    }

    @Benchmark
    public void subtract(Blackhole bh) {
        MutableDecimal15f result = mutableDecimal.subtract(MutableDecimal15f.two());
        bh.consume(result);
    }

    @Benchmark
    public void multiply(Blackhole bh) {
        MutableDecimal15f result = mutableDecimal.multiply(MutableDecimal15f.three());
        bh.consume(result);
    }

    @Benchmark
    public void divide(Blackhole bh) {
        MutableDecimal15f result = mutableDecimal.divide(MutableDecimal15f.five());
        bh.consume(result);
    }

    @Benchmark
    public void negate(Blackhole bh) {
        MutableDecimal15f result = mutableDecimal.negate();
        bh.consume(result);
    }

    @Benchmark
    public void abs(Blackhole bh) {
        MutableDecimal15f result = mutableDecimal.abs();
        bh.consume(result);
    }

    @Benchmark
    public void square(Blackhole bh) {
        MutableDecimal15f result = mutableDecimal.square();
        bh.consume(result);
    }

    @Benchmark
    public void sqrt(Blackhole bh) {
        MutableDecimal15f result = mutableDecimal.sqrt();
        bh.consume(result);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void toImmutableDecimal(Blackhole bh) {
        Decimal15f result = mutableDecimal.toImmutableDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void toMutableDecimal(Blackhole bh) {
        MutableDecimal15f result = mutableDecimal.toMutableDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void fromLong(Blackhole bh) {
        MutableDecimal15f result = new MutableDecimal15f(123456789012345L);
        bh.consume(result);
    }

    @Benchmark
    public void fromDouble(Blackhole bh) {
        MutableDecimal15f result = new MutableDecimal15f(3.141592653589793);
        bh.consume(result);
    }

    @Benchmark
    public void fromBigInteger(Blackhole bh) {
        BigInteger bigInt = new BigInteger("9876543210987654321");
        MutableDecimal15f result = new MutableDecimal15f(bigInt);
        bh.consume(result);
    }

    @Benchmark
    public void fromBigDecimal(Blackhole bh) {
        BigDecimal bd = new BigDecimal("123456789012345.6789");
        MutableDecimal15f result = new MutableDecimal15f(bd);
        bh.consume(result);
    }

    @Benchmark
    public void fromDecimal(Blackhole bh) {
        // Create a dummy Decimal15f for conversion test
        Decimal15f source = Decimal15f.valueOf(123456789012345L);
        MutableDecimal15f result = new MutableDecimal15f(source);
        bh.consume(result);
    }

    // --- Static Factory Benchmarks ---

    @Benchmark
    public void staticZero(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void staticOne(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.one();
        bh.consume(result);
    }

    @Benchmark
    public void staticTen(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.ten();
        bh.consume(result);
    }

    @Benchmark
    public void staticHalf(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.half();
        bh.consume(result);
    }

    @Benchmark
    public void staticQuadrillionth(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.quadrillionth();
        bh.consume(result);
    }

    // --- Unscaled/Clone Benchmarks ---

    @Benchmark
    public void unscaled(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.unscaled(123456789012345L);
        bh.consume(result);
    }

    @Benchmark
    public void clone(Blackhole bh) {
        MutableDecimal15f result = mutableDecimal.clone();
        bh.consume(result);
    }
}
