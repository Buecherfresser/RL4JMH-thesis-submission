package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.factory.Factory10f;
import org.decimal4j.scale.Scale10f;
import org.decimal4j.immutable.Decimal10f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class MutableDecimal10fBenchmark {

    private MutableDecimal10f mutableDecimal;

    // Setup method to prepare inputs
    @Setup
    public void setup() {
        // Initialize the mutable decimal value
        mutableDecimal = MutableDecimal10f.ten();
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void mutableDecimal_add(Blackhole bh) {
        MutableDecimal10f result = mutableDecimal.set(mutableDecimal.toString() + "1.0");
        bh.consume(result);
    }

    @Benchmark
    public void mutableDecimal_subtract(Blackhole bh) {
        MutableDecimal10f result = mutableDecimal.set(mutableDecimal.toString() + "-0.5");
        bh.consume(result);
    }

    @Benchmark
    public void mutableDecimal_multiply(Blackhole bh) {
        // Use a constant factor for multiplication
        MutableDecimal10f result = mutableDecimal.set(mutableDecimal.toString() + "2.0");
        bh.consume(result);
    }

    @Benchmark
    public void mutableDecimal_divide(Blackhole bh) {
        // Use a constant factor for division
        MutableDecimal10f result = mutableDecimal.set(mutableDecimal.toString() + "0.5");
        bh.consume(result);
    }

    @Benchmark
    public void mutableDecimal_negate(Blackhole bh) {
        MutableDecimal10f result = mutableDecimal.set(mutableDecimal.toString() + "0");
        bh.consume(result);
    }

    @Benchmark
    public void mutableDecimal_abs(Blackhole bh) {
        // Set a negative value and check absolute value
        MutableDecimal10f temp = mutableDecimal.set(mutableDecimal.toString() + "-1.2345678901");
        MutableDecimal10f result = temp.abs();
        bh.consume(result);
    }

    @Benchmark
    public void mutableDecimal_square(Blackhole bh) {
        MutableDecimal10f result = mutableDecimal.set(mutableDecimal.toString() + "2.0");
        MutableDecimal10f resultSquared = result.square();
        bh.consume(resultSquared);
    }

    @Benchmark
    public void mutableDecimal_sqrt(Blackhole bh) {
        MutableDecimal10f result = mutableDecimal.set(mutableDecimal.toString() + "4.0");
        MutableDecimal10f resultSqrt = result.sqrt();
        bh.consume(resultSqrt);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void mutableDecimal_fromLong(Blackhole bh) {
        long longValue = 123456789012L;
        MutableDecimal10f result = new MutableDecimal10f(longValue);
        bh.consume(result);
    }

    @Benchmark
    public void mutableDecimal_fromDouble(Blackhole bh) {
        double doubleValue = 123456789012.3456789;
        MutableDecimal10f result = new MutableDecimal10f(doubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void mutableDecimal_fromBigDecimal(Blackhole bh) {
        BigDecimal bigDecimalValue = new BigDecimal("123456789012.3456789");
        MutableDecimal10f result = new MutableDecimal10f(bigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void mutableDecimal_fromDecimal(Blackhole bh) {
        // Create a temporary immutable decimal for conversion
        Decimal10f tempImmutable = Decimal10f.valueOf("123456789012.3456789");
        MutableDecimal10f result = new MutableDecimal10f(tempImmutable);
        bh.consume(result);
    }

    // --- Factory/Static Method Benchmarks ---

    @Benchmark
    public void static_zero(Blackhole bh) {
        MutableDecimal10f result = MutableDecimal10f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void static_one(Blackhole bh) {
        MutableDecimal10f result = MutableDecimal10f.one();
        bh.consume(result);
    }

    @Benchmark
    public void static_ten(Blackhole bh) {
        MutableDecimal10f result = MutableDecimal10f.ten();
        bh.consume(result);
    }

    @Benchmark
    public void static_half(Blackhole bh) {
        MutableDecimal10f result = MutableDecimal10f.half();
        bh.consume(result);
    }

    @Benchmark
    public void static_millionth(Blackhole bh) {
        MutableDecimal10f result = MutableDecimal10f.millionth();
        bh.consume(result);
    }

    // --- Cloning/Utility Benchmarks ---

    @Benchmark
    public void mutableDecimal_clone(Blackhole bh) {
        MutableDecimal10f cloned = mutableDecimal.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void mutableDecimal_toImmutable(Blackhole bh) {
        Decimal10f immutable = mutableDecimal.toImmutableDecimal();
        bh.consume(immutable);
    }

    @Benchmark
    public void mutableDecimal_toMutable(Blackhole bh) {
        MutableDecimal10f mutableCopy = mutableDecimal.toMutableDecimal();
        bh.consume(mutableCopy);
    }
}
