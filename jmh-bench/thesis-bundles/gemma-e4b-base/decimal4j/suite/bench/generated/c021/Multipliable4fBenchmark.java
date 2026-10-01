package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable4f;
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
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
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

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1) @Warmup(iterations = 1, time = 1) @Measurement(iterations = 3, time = 1)
public class Multipliable4fBenchmark {

    private Multipliable4f multipliable4f;

    // Factors for various scales
    private Decimal0f factorD0f;
    private MutableDecimal0f factorMD0f;
    private Decimal1f factorD1f;
    private MutableDecimal1f factorMD1f;
    private Decimal2f factorD2f;
    private MutableDecimal2f factorMD2f;
    private Decimal3f factorD3f;
    private MutableDecimal3f factorMD3f;
    private Decimal5f factorD5f;
    private MutableDecimal5f factorMD5f;
    private Decimal6f factorD6f;
    private MutableDecimal6f factorMD6f;
    private Decimal7f factorD7f;
    private MutableDecimal7f factorMD7f;
    private Decimal8f factorD8f;
    private MutableDecimal8f factorMD8f;
    private Decimal9f factorD9f;
    private MutableDecimal9f factorMD9f;
    private Decimal10f factorD10f;
    private MutableDecimal10f factorMD10f;
    private Decimal11f factorD11f;
    private MutableDecimal11f factorMD11f;
    private Decimal12f factorD12f;
    private MutableDecimal12f factorMD12f;
    private Decimal13f factorD13f;
    private MutableDecimal13f factorMD13f;
    private Decimal14f factorD14f;
    private MutableDecimal14f factorMD14f;

    @Setup(Level.Trial)
    public void setup() {
        // Base value: 1.234 (Decimal4f)
        Decimal4f baseValue = Decimal4f.valueOf(1234L);
        multipliable4f = new Multipliable4f(baseValue);

        // Setup factors
        factorD0f = Decimal0f.valueOf(10L);
        factorMD0f = new MutableDecimal0f(10L);

        factorD1f = Decimal1f.valueOf(20L);
        factorMD1f = new MutableDecimal1f(20L);

        factorD2f = Decimal2f.valueOf(30L);
        factorMD2f = new MutableDecimal2f(30L);

        factorD3f = Decimal3f.valueOf(40L);
        factorMD3f = new MutableDecimal3f(40L);

        factorD5f = Decimal5f.valueOf(500L);
        factorMD5f = new MutableDecimal5f(500L);

        factorD6f = Decimal6f.valueOf(6000L);
        factorMD6f = new MutableDecimal6f(6000L);

        factorD7f = Decimal7f.valueOf(70000L);
        factorMD7f = new MutableDecimal7f(70000L);

        factorD8f = Decimal8f.valueOf(800000L);
        factorMD8f = new MutableDecimal8f(800000L);

        factorD9f = Decimal9f.valueOf(9000000L);
        factorMD9f = new MutableDecimal9f(9000000L);

        factorD10f = Decimal10f.valueOf(100000000L);
        factorMD10f = new MutableDecimal10f(100000000L);

        factorD11f = Decimal11f.valueOf(1100000000L);
        factorMD11f = new MutableDecimal11f(1100000000L);

        factorD12f = Decimal12f.valueOf(12000000000L);
        factorMD12f = new MutableDecimal12f(12000000000L);

        factorD13f = Decimal13f.valueOf(130000000000L);
        factorMD13f = new MutableDecimal13f(130000000000L);

        factorD14f = Decimal14f.valueOf(1400000000000L);
        factorMD14f = new MutableDecimal14f(1400000000000L);
    }

    @Benchmark
    public Decimal8f benchmarkSquare() {
        return multipliable4f.square();
    }

    // --- by(Decimal<Scale4f> factor) -> Decimal8f ---
    @Benchmark
    public Decimal8f benchmarkByDecimalScale4f() {
        // Using a simple Decimal4f factor
        Decimal4f factor = Decimal4f.valueOf(1L);
        return multipliable4f.by(factor);
    }

    // --- by(Decimal0f factor) -> Decimal4f ---
    @Benchmark
    public Decimal4f benchmarkByDecimal0f() {
        return multipliable4f.by(factorD0f);
    }

    // --- by(MutableDecimal0f factor) -> Decimal4f ---
    @Benchmark
    public Decimal4f benchmarkByMutableDecimal0f() {
        return multipliable4f.by(factorMD0f);
    }

    // --- by(Decimal1f factor) -> Decimal5f ---
    @Benchmark
    public Decimal5f benchmarkByDecimal1f() {
        return multipliable4f.by(factorD1f);
    }

    // --- by(MutableDecimal1f factor) -> Decimal5f ---
    @Benchmark
    public Decimal5f benchmarkByMutableDecimal1f() {
        return multipliable4f.by(factorMD1f);
    }

    // --- by(Decimal2f factor) -> Decimal6f ---
    @Benchmark
    public Decimal6f benchmarkByDecimal2f() {
        return multipliable4f.by(factorD2f);
    }

    // --- by(MutableDecimal2f factor) -> Decimal6f ---
    @Benchmark
    public Decimal6f benchmarkByMutableDecimal2f() {
        return multipliable4f.by(factorMD2f);
    }

    // --- by(Decimal3f factor) -> Decimal7f ---
    @Benchmark
    public Decimal7f benchmarkByDecimal3f() {
        return multipliable4f.by(factorD3f);
    }

    // --- by(MutableDecimal3f factor) -> Decimal7f ---
    @Benchmark
    public Decimal7f benchmarkByMutableDecimal3f() {
        return multipliable4f.by(factorMD3f);
    }

    // --- by(Decimal5f factor) -> Decimal9f ---
    @Benchmark
    public Decimal9f benchmarkByDecimal5f() {
        return multipliable4f.by(factorD5f);
    }

    // --- by(MutableDecimal5f factor) -> Decimal9f ---
    @Benchmark
    public Decimal9f benchmarkByMutableDecimal5f() {
        return multipliable4f.by(factorMD5f);
    }

    // --- by(Decimal6f factor) -> Decimal10f ---
    @Benchmark
    public Decimal10f benchmarkByDecimal6f() {
        return multipliable4f.by(factorD6f);
    }

    // --- by(MutableDecimal6f factor) -> Decimal10f ---
    @Benchmark
    public Decimal10f benchmarkByMutableDecimal6f() {
        return multipliable4f.by(factorMD6f);
    }

    // --- by(Decimal7f factor) -> Decimal11f ---
    @Benchmark
    public Decimal11f benchmarkByDecimal7f() {
        return multipliable4f.by(factorD7f);
    }

    // --- by(MutableDecimal7f factor) -> Decimal11f ---
    @Benchmark
    public Decimal11f benchmarkByMutableDecimal7f() {
        return multipliable4f.by(factorMD7f);
    }

    // --- by(Decimal8f factor) -> Decimal12f ---
    @Benchmark
    public Decimal12f benchmarkByDecimal8f() {
        return multipliable4f.by(factorD8f);
    }

    // --- by(MutableDecimal8f factor) -> Decimal12f ---
    @Benchmark
    public Decimal12f benchmarkByMutableDecimal8f() {
        return multipliable4f.by(factorMD8f);
    }

    // --- by(Decimal9f factor) -> Decimal13f ---
    @Benchmark
    public Decimal13f benchmarkByDecimal9f() {
        return multipliable4f.by(factorD9f);
    }

    // --- by(MutableDecimal9f factor) -> Decimal13f ---
    @Benchmark
    public Decimal13f benchmarkByMutableDecimal9f() {
        return multipliable4f.by(factorMD9f);
    }

    // --- by(Decimal10f factor) -> Decimal14f ---
    @Benchmark
    public Decimal14f benchmarkByDecimal10f() {
        return multipliable4f.by(factorD10f);
    }

    // --- by(MutableDecimal10f factor) -> Decimal14f ---
    @Benchmark
    public Decimal14f benchmarkByMutableDecimal10f() {
        return multipliable4f.by(factorMD10f);
    }

    // --- by(Decimal11f factor) -> Decimal15f ---
    @Benchmark
    public Decimal15f benchmarkByDecimal11f() {
        return multipliable4f.by(factorD11f);
    }

    // --- by(MutableDecimal11f factor) -> Decimal15f ---
    @Benchmark
    public Decimal15f benchmarkByMutableDecimal11f() {
        return multipliable4f.by(factorMD11f);
    }

    // --- by(Decimal12f factor) -> Decimal16f ---
    @Benchmark
    public Decimal16f benchmarkByDecimal12f() {
        return multipliable4f.by(factorD12f);
    }

    // --- by(MutableDecimal12f factor) -> Decimal16f ---
    @Benchmark
    public Decimal16f benchmarkByMutableDecimal12f() {
        return multipliable4f.by(factorMD12f);
    }

    // --- by(Decimal13f factor) -> Decimal17f ---
    @Benchmark
    public Decimal17f benchmarkByDecimal13f() {
        return multipliable4f.by(factorD13f);
    }

    // --- by(MutableDecimal13f factor) -> Decimal17f ---
    @Benchmark
    public Decimal17f benchmarkByMutableDecimal13f() {
        return multipliable4f.by(factorMD13f);
    }

    // --- by(Decimal14f factor) -> Decimal18f ---
    @Benchmark
    public Decimal18f benchmarkByDecimal14f() {
        return multipliable4f.by(factorD14f);
    }

    // --- by(MutableDecimal14f factor) -> Decimal18f ---
    @Benchmark
    public Decimal18f benchmarkByMutableDecimal14f() {
        return multipliable4f.by(factorMD14f);
    }
}
