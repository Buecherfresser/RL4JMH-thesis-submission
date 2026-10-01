package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.api.Decimal;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.scale.Scale2f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class MutableDecimal2fBenchmark {

    private MutableDecimal2f mutableDecimal;
    private MutableDecimal2f bigDecimalDecimal;
    private MutableDecimal2f doubleDecimal;
    private MutableDecimal2f bigIntegerDecimal;
    private MutableDecimal2f stringDecimal;
    private MutableDecimal2f unscaledDecimal;

    // Setup method to prepare inputs
    @Setup
    public void setup() {
        // 1. Setup a base mutable decimal value (e.g., 10.0)
        mutableDecimal = MutableDecimal2f.ten(); 
        
        // 2. Setup a complex value from BigDecimal
        BigDecimal bigDecimal = new BigDecimal("123.456789");
        bigDecimalDecimal = new MutableDecimal2f(bigDecimal);

        // 3. Setup a value from double
        doubleDecimal = new MutableDecimal2f(123.45);

        // 4. Setup a value from BigInteger
        bigIntegerDecimal = new MutableDecimal2f(new BigInteger("1234567890123456789L"));

        // 5. Setup a value from String
        stringDecimal = new MutableDecimal2f("98.76");

        // 6. Setup a value from unscaled long
        unscaledDecimal = MutableDecimal2f.unscaled(1234567890123456789L);
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        MutableDecimal2f result = mutableDecimal.add(MutableDecimal2f.one());
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSubtract(Blackhole bh) {
        MutableDecimal2f result = mutableDecimal.subtract(MutableDecimal2f.ten());
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiply(Blackhole bh) {
        MutableDecimal2f result = mutableDecimal.multiply(MutableDecimal2f.two());
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivide(Blackhole bh) {
        MutableDecimal2f result = mutableDecimal.divide(MutableDecimal2f.five());
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkNegate(Blackhole bh) {
        MutableDecimal2f result = mutableDecimal.negate();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAbs(Blackhole bh) {
        MutableDecimal2f result = mutableDecimal.abs();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSquare(Blackhole bh) {
        MutableDecimal2f result = mutableDecimal.square();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSqrt(Blackhole bh) {
        MutableDecimal2f result = mutableDecimal.sqrt();
        bh.consume(result);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void benchmarkFromLong(Blackhole bh) {
        MutableDecimal2f result = new MutableDecimal2f(1234567890123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromDouble(Blackhole bh) {
        MutableDecimal2f result = new MutableDecimal2f(123.45);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromBigInteger(Blackhole bh) {
        MutableDecimal2f result = new MutableDecimal2f(new BigInteger("1234567890123456789L"));
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromString(Blackhole bh) {
        MutableDecimal2f result = new MutableDecimal2f("98.76");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromBigDecimal(Blackhole bh) {
        MutableDecimal2f result = new MutableDecimal2f(new BigDecimal("123.456789"));
        bh.consume(result);
    }

    // --- Factory/Static Method Benchmarks ---

    @Benchmark
    public void benchmarkZero(Blackhole bh) {
        MutableDecimal2f result = MutableDecimal2f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkOne(Blackhole bh) {
        MutableDecimal2f result = MutableDecimal2f.one();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkTen(Blackhole bh) {
        MutableDecimal2f result = MutableDecimal2f.ten();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkHalf(Blackhole bh) {
        MutableDecimal2f result = MutableDecimal2f.half();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkHundredth(Blackhole bh) {
        MutableDecimal2f result = MutableDecimal2f.hundredth();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkUnscaled(Blackhole bh) {
        MutableDecimal2f result = MutableDecimal2f.unscaled(1234567890123456789L);
        bh.consume(result);
    }

    // --- Clone/Mutability Benchmarks ---

    @Benchmark
    public void benchmarkClone(Blackhole bh) {
        MutableDecimal2f cloned = mutableDecimal.clone();
        bh.consume(cloned);
    }
    
    @Benchmark
    public void benchmarkToMutableDecimal(Blackhole bh) {
        MutableDecimal2f result = mutableDecimal.toMutableDecimal();
        bh.consume(result);
    }
}
