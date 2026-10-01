package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable0f;
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
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
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
import org.decimal4j.mutable.MutableDecimal17f;
import org.decimal4j.mutable.MutableDecimal18f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable0fBenchmark {

    private Multipliable0f multipliable0f;

    // Factors for multiplication tests
    private Decimal0f factor0f;
    private Decimal1f factor1f;
    private MutableDecimal1f mutableFactor1f;
    private Decimal2f factor2f;
    private MutableDecimal2f mutableFactor2f;
    private Decimal3f factor3f;
    private MutableDecimal3f mutableFactor3f;
    private Decimal4f factor4f;
    private MutableDecimal4f mutableFactor4f;
    private Decimal5f factor5f;
    private MutableDecimal5f mutableFactor5f;
    private Decimal6f factor6f;
    private MutableDecimal6f mutableFactor6f;
    private Decimal7f factor7f;
    private MutableDecimal7f mutableFactor7f;
    private Decimal8f factor8f;
    private MutableDecimal8f mutableFactor8f;
    private Decimal9f factor9f;
    private MutableDecimal9f mutableFactor9f;
    private Decimal10f factor10f;
    private MutableDecimal10f mutableFactor10f;
    private Decimal11f factor11f;
    private MutableDecimal11f mutableFactor11f;
    private Decimal12f factor12f;
    private MutableDecimal12f mutableFactor12f;
    private Decimal13f factor13f;
    private MutableDecimal13f mutableFactor13f;
    private Decimal14f factor14f;
    private MutableDecimal14f mutableFactor14f;
    private Decimal15f factor15f;
    private MutableDecimal15f mutableFactor15f;
    private Decimal16f factor16f;
    private MutableDecimal16f mutableFactor16f;
    private Decimal17f factor17f;
    private MutableDecimal17f mutableFactor17f;
    private Decimal18f factor18f;
    private MutableDecimal18f mutableFactor18f;

    @Setup(Level.Trial)
    public void setup() {
        // Base value for Multipliable0f (e.g., 1.0)
        Decimal0f baseValue = Decimal0f.valueOf(1L);
        multipliable0f = new Multipliable0f(baseValue);

        // Setup factors (using simple non-zero values for representative testing)
        factor0f = Decimal0f.valueOf(2L);
        
        factor1f = Decimal1f.valueOf(3L);
        mutableFactor1f = new MutableDecimal1f(3L);

        factor2f = Decimal2f.valueOf(4L);
        mutableFactor2f = new MutableDecimal2f(4L);

        factor3f = Decimal3f.valueOf(5L);
        mutableFactor3f = new MutableDecimal3f(5L);

        factor4f = Decimal4f.valueOf(6L);
        mutableFactor4f = new MutableDecimal4f(6L);

        factor5f = Decimal5f.valueOf(7L);
        mutableFactor5f = new MutableDecimal5f(7L);

        factor6f = Decimal6f.valueOf(8L);
        mutableFactor6f = new MutableDecimal6f(8L);

        factor7f = Decimal7f.valueOf(9L);
        mutableFactor7f = new MutableDecimal7f(9L);

        factor8f = Decimal8f.valueOf(10L);
        mutableFactor8f = new MutableDecimal8f(10L);

        factor9f = Decimal9f.valueOf(11L);
        mutableFactor9f = new MutableDecimal9f(11L);

        factor10f = Decimal10f.valueOf(12L);
        mutableFactor10f = new MutableDecimal10f(12L);

        factor11f = Decimal11f.valueOf(13L);
        mutableFactor11f = new MutableDecimal11f(13L);

        factor12f = Decimal12f.valueOf(14L);
        mutableFactor12f = new MutableDecimal12f(14L);

        factor13f = Decimal13f.valueOf(15L);
        mutableFactor13f = new MutableDecimal13f(15L);

        factor14f = Decimal14f.valueOf(16L);
        mutableFactor14f = new MutableDecimal14f(16L);

        factor15f = Decimal15f.valueOf(17L);
        mutableFactor15f = new MutableDecimal15f(17L);

        factor16f = Decimal16f.valueOf(18L);
        mutableFactor16f = new MutableDecimal16f(18L);

        factor17f = Decimal17f.valueOf(19L);
        mutableFactor17f = new MutableDecimal17f(19L);

        factor18f = Decimal18f.valueOf(20L);
        mutableFactor18f = new MutableDecimal18f(20L);
    }

    // --- Benchmarks for square() ---

    @Benchmark
    public Decimal0f benchSquare() {
        return multipliable0f.square();
    }

    // --- Benchmarks for by(Decimal<Scale0f> factor) ---

    @Benchmark
    public Decimal0f benchByDecimal0f() {
        return multipliable0f.by(factor0f);
    }

    // --- Benchmarks for by(Decimal1f factor) ---

    @Benchmark
    public Decimal1f benchByDecimal1fImmutable() {
        return multipliable0f.by(factor1f);
    }

    @Benchmark
    public Decimal1f benchByDecimal1fMutable() {
        return multipliable0f.by(mutableFactor1f);
    }

    // --- Benchmarks for by(Decimal2f factor) ---

    @Benchmark
    public Decimal2f benchByDecimal2fImmutable() {
        return multipliable0f.by(factor2f);
    }

    @Benchmark
    public Decimal2f benchByDecimal2fMutable() {
        return multipliable0f.by(mutableFactor2f);
    }

    // --- Benchmarks for by(Decimal3f factor) ---

    @Benchmark
    public Decimal3f benchByDecimal3fImmutable() {
        return multipliable0f.by(factor3f);
    }

    @Benchmark
    public Decimal3f benchByDecimal3fMutable() {
        return multipliable0f.by(mutableFactor3f);
    }

    // --- Benchmarks for by(Decimal4f factor) ---

    @Benchmark
    public Decimal4f benchByDecimal4fImmutable() {
        return multipliable0f.by(factor4f);
    }

    @Benchmark
    public Decimal4f benchByDecimal4fMutable() {
        return multipliable0f.by(mutableFactor4f);
    }

    // --- Benchmarks for by(Decimal5f factor) ---

    @Benchmark
    public Decimal5f benchByDecimal5fImmutable() {
        return multipliable0f.by(factor5f);
    }

    @Benchmark
    public Decimal5f benchByDecimal5fMutable() {
        return multipliable0f.by(mutableFactor5f);
    }

    // --- Benchmarks for by(Decimal6f factor) ---

    @Benchmark
    public Decimal6f benchByDecimal6fImmutable() {
        return multipliable0f.by(factor6f);
    }

    @Benchmark
    public Decimal6f benchByDecimal6fMutable() {
        return multipliable0f.by(mutableFactor6f);
    }

    // --- Benchmarks for by(Decimal7f factor) ---

    @Benchmark
    public Decimal7f benchByDecimal7fImmutable() {
        return multipliable0f.by(factor7f);
    }

    @Benchmark
    public Decimal7f benchByDecimal7fMutable() {
        return multipliable0f.by(mutableFactor7f);
    }

    // --- Benchmarks for by(Decimal8f factor) ---

    @Benchmark
    public Decimal8f benchByDecimal8fImmutable() {
        return multipliable0f.by(factor8f);
    }

    @Benchmark
    public Decimal8f benchByDecimal8fMutable() {
        return multipliable0f.by(mutableFactor8f);
    }

    // --- Benchmarks for by(Decimal9f factor) ---

    @Benchmark
    public Decimal9f benchByDecimal9fImmutable() {
        return multipliable0f.by(factor9f);
    }

    @Benchmark
    public Decimal9f benchByDecimal9fMutable() {
        return multipliable0f.by(mutableFactor9f);
    }

    // --- Benchmarks for by(Decimal10f factor) ---

    @Benchmark
    public Decimal10f benchByDecimal10fImmutable() {
        return multipliable0f.by(factor10f);
    }

    @Benchmark
    public Decimal10f benchByDecimal10fMutable() {
        return multipliable0f.by(mutableFactor10f);
    }

    // --- Benchmarks for by(Decimal11f factor) ---

    @Benchmark
    public Decimal11f benchByDecimal11fImmutable() {
        return multipliable0f.by(factor11f);
    }

    @Benchmark
    public Decimal11f benchByDecimal11fMutable() {
        return multipliable0f.by(mutableFactor11f);
    }

    // --- Benchmarks for by(Decimal12f factor) ---

    @Benchmark
    public Decimal12f benchByDecimal12fImmutable() {
        return multipliable0f.by(factor12f);
    }

    @Benchmark
    public Decimal12f benchByDecimal12fMutable() {
        return multipliable0f.by(mutableFactor12f);
    }

    // --- Benchmarks for by(Decimal13f factor) ---

    @Benchmark
    public Decimal13f benchByDecimal13fImmutable() {
        return multipliable0f.by(factor13f);
    }

    @Benchmark
    public Decimal13f benchByDecimal13fMutable() {
        return multipliable0f.by(mutableFactor13f);
    }

    // --- Benchmarks for by(Decimal14f factor) ---

    @Benchmark
    public Decimal14f benchByDecimal14fImmutable() {
        return multipliable0f.by(factor14f);
    }

    @Benchmark
    public Decimal14f benchByDecimal14fMutable() {
        return multipliable0f.by(mutableFactor14f);
    }

    // --- Benchmarks for by(Decimal15f factor) ---

    @Benchmark
    public Decimal15f benchByDecimal15fImmutable() {
        return multipliable0f.by(factor15f);
    }

    @Benchmark
    public Decimal15f benchByDecimal15fMutable() {
        return multipliable0f.by(mutableFactor15f);
    }

    // --- Benchmarks for by(Decimal16f factor) ---

    @Benchmark
    public Decimal16f benchByDecimal16fImmutable() {
        return multipliable0f.by(factor16f);
    }

    @Benchmark
    public Decimal16f benchByDecimal16fMutable() {
        return multipliable0f.by(mutableFactor16f);
    }

    // --- Benchmarks for by(Decimal17f factor) ---

    @Benchmark
    public Decimal17f benchByDecimal17fImmutable() {
        return multipliable0f.by(factor17f);
    }

    @Benchmark
    public Decimal17f benchByDecimal17fMutable() {
        return multipliable0f.by(mutableFactor17f);
    }

    // --- Benchmarks for by(Decimal18f factor) ---

    @Benchmark
    public Decimal18f benchByDecimal18fImmutable() {
        return multipliable0f.by(factor18f);
    }

    @Benchmark
    public Decimal18f benchByDecimal18fMutable() {
        return multipliable0f.by(mutableFactor18f);
    }
}
