package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable2f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.immutable.Decimal12f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.mutable.MutableDecimal12f;
import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.mutable.MutableDecimal14f;
import org.decimal4j.mutable.MutableDecimal15f;
import org.decimal4j.mutable.MutableDecimal16f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable2fBenchmark {

    private Multipliable2f multipliable2f;

    // Factors for multiplication methods
    private Decimal0f factorD0f;
    private MutableDecimal0f factorM0f;
    private Decimal1f factorD1f;
    private MutableDecimal1f factorM1f;
    private Decimal3f factorD3f;
    private MutableDecimal3f factorM3f;
    private Decimal4f factorD4f;
    private MutableDecimal4f factorM4f;
    private Decimal5f factorD5f;
    private MutableDecimal5f factorM5f;
    private Decimal6f factorD6f;
    private MutableDecimal6f factorM6f;
    private Decimal7f factorD7f;
    private MutableDecimal7f factorM7f;
    private Decimal8f factorD8f;
    private MutableDecimal8f factorM8f;
    private Decimal9f factorD9f;
    private MutableDecimal9f factorM9f;
    private Decimal10f factorD10f;
    private MutableDecimal10f factorM10f;
    private Decimal11f factorD11f;
    private MutableDecimal11f factorM11f;
    private Decimal12f factorD12f;
    private MutableDecimal12f factorM12f;
    private Decimal13f factorD13f;
    private MutableDecimal13f factorM13f;
    private Decimal14f factorD14f;
    private MutableDecimal14f factorM14f;
    private Decimal15f factorD15f;
    private MutableDecimal15f factorM15f;
    private Decimal16f factorD16f;
    private MutableDecimal16f factorM16f;

    @Setup
    public void setup() {
        // Base value for Multipliable2f (Scale 2)
        // Using 1.23 (unscaled 123)
        Decimal2f baseValue = Decimal2f.valueOf(123L);
        multipliable2f = new Multipliable2f(baseValue);

        // Setup factors (using simple values like 1.0, 2.0, etc.)
        
        // Scale 0
        factorD0f = Decimal0f.valueOf(1L);
        factorM0f = new MutableDecimal0f(2L);

        // Scale 1
        factorD1f = Decimal1f.valueOf(10L);
        factorM1f = new MutableDecimal1f(20L);

        // Scale 3
        factorD3f = Decimal3f.valueOf(1000L);
        factorM3f = new MutableDecimal3f(2000L);

        // Scale 4
        factorD4f = Decimal4f.valueOf(10000L);
        factorM4f = new MutableDecimal4f(20000L);

        // Scale 5
        factorD5f = Decimal5f.valueOf(100000L);
        factorM5f = new MutableDecimal5f(200000L);

        // Scale 6
        factorD6f = Decimal6f.valueOf(1000000L);
        factorM6f = new MutableDecimal6f(2000000L);

        // Scale 7
        factorD7f = Decimal7f.valueOf(10000000L);
        factorM7f = new MutableDecimal7f(20000000L);

        // Scale 8
        factorD8f = Decimal8f.valueOf(100000000L);
        factorM8f = new MutableDecimal8f(200000000L);

        // Scale 9
        factorD9f = Decimal9f.valueOf(1000000000L);
        factorM9f = new MutableDecimal9f(2000000000L);

        // Scale 10
        factorD10f = Decimal10f.valueOf(10000000000L);
        factorM10f = new MutableDecimal10f(20000000000L);

        // Scale 11
        factorD11f = Decimal11f.valueOf(100000000000L);
        factorM11f = new MutableDecimal11f(200000000000L);

        // Scale 12
        factorD12f = Decimal12f.valueOf(1000000000000L);
        factorM12f = new MutableDecimal12f(2000000000000L);

        // Scale 13
        factorD13f = Decimal13f.valueOf(10000000000000L);
        factorM13f = new MutableDecimal13f(20000000000000L);

        // Scale 14
        factorD14f = Decimal14f.valueOf(100000000000000L);
        factorM14f = new MutableDecimal14f(200000000000000L);

        // Scale 15
        factorD15f = Decimal15f.valueOf(1000000000000000L);
        factorM15f = new MutableDecimal15f(2000000000000000L);

        // Scale 16
        factorD16f = Decimal16f.valueOf(10000000000000000L);
        factorM16f = new MutableDecimal16f(20000000000000000L);
    }

    // --- Benchmarks for square() ---

    @Benchmark
    public Decimal4f benchSquare() {
        return multipliable2f.square();
    }

    // --- Benchmarks for by(Decimal<Scale2f> factor) ---

    @Benchmark
    public Decimal4f benchByDecimal2fFactor() {
        // Using a simple Decimal2f factor
        Decimal2f factor = Decimal2f.valueOf(10L);
        return multipliable2f.by(factor);
    }

    // --- Benchmarks for by(Decimal0f factor) ---

    @Benchmark
    public Decimal2f benchByDecimal0f() {
        return multipliable2f.by(factorD0f);
    }

    @Benchmark
    public Decimal2f benchByMutableDecimal0f() {
        return multipliable2f.by(factorM0f);
    }

    // --- Benchmarks for by(Decimal1f factor) ---

    @Benchmark
    public Decimal3f benchByDecimal1f() {
        return multipliable2f.by(factorD1f);
    }

    @Benchmark
    public Decimal3f benchByMutableDecimal1f() {
        return multipliable2f.by(factorM1f);
    }

    // --- Benchmarks for by(Decimal3f factor) ---

    @Benchmark
    public Decimal5f benchByDecimal3f() {
        return multipliable2f.by(factorD3f);
    }

    @Benchmark
    public Decimal5f benchByMutableDecimal3f() {
        return multipliable2f.by(factorM3f);
    }

    // --- Benchmarks for by(Decimal4f factor) ---

    @Benchmark
    public Decimal6f benchByDecimal4f() {
        return multipliable2f.by(factorD4f);
    }

    @Benchmark
    public Decimal6f benchByMutableDecimal4f() {
        return multipliable2f.by(factorM4f);
    }

    // --- Benchmarks for by(Decimal5f factor) ---

    @Benchmark
    public Decimal7f benchByDecimal5f() {
        return multipliable2f.by(factorD5f);
    }

    @Benchmark
    public Decimal7f benchByMutableDecimal5f() {
        return multipliable2f.by(factorM5f);
    }

    // --- Benchmarks for by(Decimal6f factor) ---

    @Benchmark
    public Decimal8f benchByDecimal6f() {
        return multipliable2f.by(factorD6f);
    }

    @Benchmark
    public Decimal8f benchByMutableDecimal6f() {
        return multipliable2f.by(factorM6f);
    }

    // --- Benchmarks for by(Decimal7f factor) ---

    @Benchmark
    public Decimal9f benchByDecimal7f() {
        return multipliable2f.by(factorD7f);
    }

    @Benchmark
    public Decimal9f benchByMutableDecimal7f() {
        return multipliable2f.by(factorM7f);
    }

    // --- Benchmarks for by(Decimal8f factor) ---

    @Benchmark
    public Decimal10f benchByDecimal8f() {
        return multipliable2f.by(factorD8f);
    }

    @Benchmark
    public Decimal10f benchByMutableDecimal8f() {
        return multipliable2f.by(factorM8f);
    }

    // --- Benchmarks for by(Decimal9f factor) ---

    @Benchmark
    public Decimal11f benchByDecimal9f() {
        return multipliable2f.by(factorD9f);
    }

    @Benchmark
    public Decimal11f benchByMutableDecimal9f() {
        return multipliable2f.by(factorM9f);
    }

    // --- Benchmarks for by(Decimal10f factor) ---

    @Benchmark
    public Decimal12f benchByDecimal10f() {
        return multipliable2f.by(factorD10f);
    }

    @Benchmark
    public Decimal12f benchByMutableDecimal10f() {
        return multipliable2f.by(factorM10f);
    }

    // --- Benchmarks for by(Decimal11f factor) ---

    @Benchmark
    public Decimal13f benchByDecimal11f() {
        return multipliable2f.by(factorD11f);
    }

    @Benchmark
    public Decimal13f benchByMutableDecimal11f() {
        return multipliable2f.by(factorM11f);
    }

    // --- Benchmarks for by(Decimal12f factor) ---

    @Benchmark
    public Decimal14f benchByDecimal12f() {
        return multipliable2f.by(factorD12f);
    }

    @Benchmark
    public Decimal14f benchByMutableDecimal12f() {
        return multipliable2f.by(factorM12f);
    }

    // --- Benchmarks for by(Decimal13f factor) ---

    @Benchmark
    public Decimal15f benchByDecimal13f() {
        return multipliable2f.by(factorD13f);
    }

    @Benchmark
    public Decimal15f benchByMutableDecimal13f() {
        return multipliable2f.by(factorM13f);
    }

    // --- Benchmarks for by(Decimal14f factor) ---

    @Benchmark
    public Decimal16f benchByDecimal14f() {
        return multipliable2f.by(factorD14f);
    }

    @Benchmark
    public Decimal16f benchByMutableDecimal14f() {
        return multipliable2f.by(factorM14f);
    }

    // --- Benchmarks for by(Decimal15f factor) ---

    @Benchmark
    public Decimal17f benchByDecimal15f() {
        return multipliable2f.by(factorD15f);
    }

    @Benchmark
    public Decimal17f benchByMutableDecimal15f() {
        return multipliable2f.by(factorM15f);
    }

    // --- Benchmarks for by(Decimal16f factor) ---

    @Benchmark
    public Decimal18f benchByDecimal16f() {
        return multipliable2f.by(factorD16f);
    }

    @Benchmark
    public Decimal18f benchByMutableDecimal16f() {
        return multipliable2f.by(factorM16f);
    }
}
