package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable15f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable15fBenchmark {

    private Multipliable15f multipliable;

    // Factors for multiplication
    private Decimal0f factorD0f;
    private MutableDecimal0f factorMD0f;
    private Decimal1f factorD1f;
    private MutableDecimal1f factorMD1f;
    private Decimal2f factorD2f;
    private MutableDecimal2f factorMD2f;
    private Decimal3f factorD3f;
    private MutableDecimal3f factorMD3f;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup the base value (Decimal15f)
        // Using a non-trivial value for realistic benchmarking
        Decimal15f baseValue = Decimal15f.valueOf(123456789012345L);

        // 2. Setup the Multipliable15f instance
        multipliable = new Multipliable15f(baseValue);

        // 3. Setup factors
        // D0f factors
        factorD0f = Decimal0f.valueOf(5L);
        factorMD0f = new MutableDecimal0f(5L);

        // D1f factors
        factorD1f = Decimal1f.valueOf(2L);
        factorMD1f = new MutableDecimal1f(2L);

        // D2f factors
        factorD2f = Decimal2f.valueOf(3L);
        factorMD2f = new MutableDecimal2f(3L);

        // D3f factors
        factorD3f = Decimal3f.valueOf(4L);
        factorMD3f = new MutableDecimal3f(4L);
    }

    // --- Benchmarks for Decimal0f factors (Result: Decimal15f) ---

    @Benchmark
    public Decimal15f multiplyByDecimal0fImmutable() {
        return multipliable.by(factorD0f);
    }

    @Benchmark
    public Decimal15f multiplyByDecimal0fMutable() {
        return multipliable.by(factorMD0f);
    }

    // --- Benchmarks for Decimal1f factors (Result: Decimal16f) ---

    @Benchmark
    public Decimal16f multiplyByDecimal1fImmutable() {
        return multipliable.by(factorD1f);
    }

    @Benchmark
    public Decimal16f multiplyByDecimal1fMutable() {
        return multipliable.by(factorMD1f);
    }

    // --- Benchmarks for Decimal2f factors (Result: Decimal17f) ---

    @Benchmark
    public Decimal17f multiplyByDecimal2fImmutable() {
        return multipliable.by(factorD2f);
    }

    @Benchmark
    public Decimal17f multiplyByDecimal2fMutable() {
        return multipliable.by(factorMD2f);
    }

    // --- Benchmarks for Decimal3f factors (Result: Decimal18f) ---

    @Benchmark
    public Decimal18f multiplyByDecimal3fImmutable() {
        return multipliable.by(factorD3f);
    }

    @Benchmark
    public Decimal18f multiplyByDecimal3fMutable() {
        return multipliable.by(factorMD3f);
    }
}
