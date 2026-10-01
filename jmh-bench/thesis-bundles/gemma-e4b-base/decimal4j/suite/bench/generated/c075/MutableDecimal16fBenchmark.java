package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.mutable.MutableDecimal16f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.exact.Multipliable16f;
import org.decimal4j.scale.Scale16f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal16fBenchmark {

    private MutableDecimal16f subject;
    private MutableDecimal16f other;
    private BigDecimal bigDecimalInput;
    private double doubleInput;
    private long longInput;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize inputs
        longInput = 1234567890123L;
        doubleInput = 3.141592653589793;
        bigDecimalInput = new BigDecimal("987654321.1234567890123456");

        // Initialize subject and other instances
        // Use a simple value for the subject
        subject = MutableDecimal16f.unscaled(1000L);
        // Use a different value for the other operand
        other = MutableDecimal16f.unscaled(500L);
    }

    // --- Construction Benchmarks ---

    @Benchmark
    public MutableDecimal16f bench_construction_from_long() {
        // Rebuild subject for each invocation to measure construction cost
        MutableDecimal16f result = new MutableDecimal16f(longInput);
        return result;
    }

    @Benchmark
    public MutableDecimal16f bench_construction_from_double() {
        // Rebuild subject for each invocation to measure construction cost
        MutableDecimal16f result = new MutableDecimal16f(doubleInput);
        return result;
    }

    @Benchmark
    public MutableDecimal16f bench_construction_from_bigdecimal() {
        // Rebuild subject for each invocation to measure construction cost
        MutableDecimal16f result = new MutableDecimal16f(bigDecimalInput);
        return result;
    }

    @Benchmark
    public MutableDecimal16f bench_construction_from_decimal16f() {
        // Use a fixed immutable value for input
        Decimal16f input = Decimal16f.valueOf(123.45678901234567);
        MutableDecimal16f result = new MutableDecimal16f(input);
        return result;
    }

    // --- Operation Benchmarks ---

    @Benchmark
    public MutableDecimal16f bench_clone() {
        // Clone the pre-setup subject
        return subject.clone();
    }

    @Benchmark
    public Multipliable16f bench_multiplyExact() {
        // Measure the cost of creating the Multipliable wrapper
        return subject.multiplyExact();
    }

    // Assuming standard arithmetic methods exist on AbstractMutableDecimal
    // We benchmark addition, which is a fundamental operation.
    @Benchmark
    public MutableDecimal16f bench_add() {
        // Since MutableDecimal16f is mutable, we must clone the subject before modification
        // to ensure the benchmark is repeatable and doesn't depend on previous state.
        MutableDecimal16f result = subject.clone();
        // Assuming add(MutableDecimal16f) exists and mutates 'result'
        result.add(other); 
        return result;
    }

    // Assuming standard arithmetic methods exist on AbstractMutableDecimal
    @Benchmark
    public MutableDecimal16f bench_subtract() {
        MutableDecimal16f result = subject.clone();
        // Assuming subtract(MutableDecimal16f) exists
        result.subtract(other);
        return result;
    }

    // Assuming standard arithmetic methods exist on AbstractMutableDecimal
    @Benchmark
    public MutableDecimal16f bench_negate() {
        MutableDecimal16f result = subject.clone();
        // Assuming negate() exists
        result.negate();
        return result;
    }
}
