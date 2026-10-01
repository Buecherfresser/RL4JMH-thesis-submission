package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable3f;
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
public class Multipliable3fBenchmark {

    private Multipliable3f multipliable;

    // Factors for various multiplication methods
    private Decimal0f factorD0;
    private MutableDecimal0f factorMD0;
    private Decimal1f factorD1;
    private MutableDecimal1f factorMD1;
    private Decimal2f factorD2;
    private MutableDecimal2f factorMD2;
    private Decimal4f factorD4;
    private MutableDecimal4f factorMD4;
    private Decimal5f factorD5;
    private MutableDecimal5f factorMD5;
    private Decimal6f factorD6;
    private MutableDecimal6f factorMD6;
    private Decimal7f factorD7;
    private MutableDecimal7f factorMD7;
    private Decimal8f factorD8;
    private MutableDecimal8f factorMD8;
    private Decimal9f factorD9;
    private MutableDecimal9f factorMD9;
    private Decimal10f factorD10;
    private MutableDecimal10f factorMD10;
    private Decimal11f factorD11;
    private MutableDecimal11f factorMD11;
    private Decimal12f factorD12;
    private MutableDecimal12f factorMD12;
    private Decimal13f factorD13;
    private MutableDecimal13f factorMD13;
    private Decimal14f factorD14;
    private MutableDecimal14f factorMD14;
    private Decimal15f factorD15;
    private MutableDecimal15f factorMD15;
    private Decimal16f factorD16;
    private MutableDecimal16f factorMD16;
    private Decimal17f factorD17;
    private MutableDecimal17f factorMD17;
    private Decimal18f factorD18;
    private MutableDecimal18f factorMD18;

    @Setup
    public void setup() {
        // Base value for Multipliable3f (e.g., 1.000)
        Decimal3f baseValue = Decimal3f.valueOf(1L);
        multipliable = new Multipliable3f(baseValue);

        // Setup factors (using simple values to avoid overflow exceptions)
        factorD0 = Decimal0f.ONE;
        factorMD0 = MutableDecimal0f.one();

        factorD1 = Decimal1f.ONE;
        factorMD1 = MutableDecimal1f.one();

        factorD2 = Decimal2f.ONE;
        factorMD2 = MutableDecimal2f.one();

        factorD4 = Decimal4f.ONE;
        factorMD4 = MutableDecimal4f.one();

        factorD5 = Decimal5f.ONE;
        factorMD5 = MutableDecimal5f.one();

        factorD6 = Decimal6f.ONE;
        factorMD6 = MutableDecimal6f.one();

        factorD7 = Decimal7f.ONE;
        factorMD7 = MutableDecimal7f.one();

        factorD8 = Decimal8f.ONE;
        factorMD8 = MutableDecimal8f.one();

        factorD9 = Decimal9f.ONE;
        factorMD9 = MutableDecimal9f.one();

        factorD10 = Decimal10f.ONE;
        factorMD10 = MutableDecimal10f.one();

        factorD11 = Decimal11f.ONE;
        factorMD11 = MutableDecimal11f.one();

        factorD12 = Decimal12f.ONE;
        factorMD12 = MutableDecimal12f.one();

        factorD13 = Decimal13f.ONE;
        factorMD13 = MutableDecimal13f.one();

        factorD14 = Decimal14f.ONE;
        factorMD14 = MutableDecimal14f.one();

        factorD15 = Decimal15f.ONE;
        factorMD15 = MutableDecimal15f.one();
        
        // New factors for D16, D17, D18
        factorD16 = Decimal16f.ONE;
        factorMD16 = MutableDecimal16f.one();
        
        factorD17 = Decimal17f.ONE;
        factorMD17 = MutableDecimal17f.one();
        
        factorD18 = Decimal18f.ONE;
        factorMD18 = MutableDecimal18f.one();
    }

    // --- Benchmarks for square() ---

    @Benchmark
    public Decimal6f benchmarkSquare() {
        // Multipliable3f.square() returns Decimal6f
        return multipliable.square();
    }

    // --- Benchmarks for by(Decimal<Scale3f> factor) ---

    @Benchmark
    public Decimal6f benchmarkBySelfFactor() {
        // Multipliable3f.by(Decimal<Scale3f> factor) returns Decimal6f
        return multipliable.by(multipliable.getValue());
    }

    // --- Benchmarks for by(Decimal0f factor) ---

    @Benchmark
    public Decimal3f benchmarkByD0f() {
        // Multipliable3f.by(Decimal0f factor) returns Decimal3f
        return multipliable.by(factorD0);
    }

    @Benchmark
    public Decimal3f benchmarkByMD0f() {
        // Multipliable3f.by(MutableDecimal0f factor) returns Decimal3f
        return multipliable.by(factorMD0);
    }

    // --- Benchmarks for by(Decimal1f factor) ---

    @Benchmark
    public Decimal4f benchmarkByD1f() {
        // Multipliable3f.by(Decimal1f factor) returns Decimal4f
        return multipliable.by(factorD1);
    }

    @Benchmark
    public Decimal4f benchmarkByMD1f() {
        // Multipliable3f.by(MutableDecimal1f factor) returns Decimal4f
        return multipliable.by(factorMD1);
    }

    // --- Benchmarks for by(Decimal2f factor) ---

    @Benchmark
    public Decimal5f benchmarkByD2f() {
        // Multipliable3f.by(Decimal2f factor) returns Decimal5f
        return multipliable.by(factorD2);
    }

    @Benchmark
    public Decimal5f benchmarkByMD2f() {
        // Multipliable3f.by(MutableDecimal2f factor) returns Decimal5f
        return multipliable.by(factorMD2);
    }

    // --- Benchmarks for by(Decimal4f factor) ---

    @Benchmark
    public Decimal7f benchmarkByD4f() {
        // Multipliable3f.by(Decimal4f factor) returns Decimal7f
        return multipliable.by(factorD4);
    }

    @Benchmark
    public Decimal7f benchmarkByMD4f() {
        // Multipliable3f.by(MutableDecimal4f factor) returns Decimal7f
        return multipliable.by(factorMD4);
    }

    // --- Benchmarks for by(Decimal5f factor) ---

    @Benchmark
    public Decimal8f benchmarkByD5f() {
        // Multipliable3f.by(Decimal5f factor) returns Decimal8f
        return multipliable.by(factorD5);
    }

    @Benchmark
    public Decimal8f benchmarkByMD5f() {
        // Multipliable3f.by(MutableDecimal5f factor) returns Decimal8f
        return multipliable.by(factorMD5);
    }

    // --- Benchmarks for by(Decimal6f factor) ---

    @Benchmark
    public Decimal9f benchmarkByD6f() {
        // Multipliable3f.by(Decimal6f factor) returns Decimal9f
        return multipliable.by(factorD6);
    }

    @Benchmark
    public Decimal9f benchmarkByMD6f() {
        // Multipliable3f.by(MutableDecimal6f factor) returns Decimal9f
        return multipliable.by(factorMD6);
    }

    // --- Benchmarks for by(Decimal7f factor) ---

    @Benchmark
    public Decimal10f benchmarkByD7f() {
        // Multipliable3f.by(Decimal7f factor) returns Decimal10f
        return multipliable.by(factorD7);
    }

    @Benchmark
    public Decimal10f benchmarkByMD7f() {
        // Multipliable3f.by(MutableDecimal7f factor) returns Decimal10f
        return multipliable.by(factorMD7);
    }

    // --- Benchmarks for by(Decimal8f factor) ---

    @Benchmark
    public Decimal11f benchmarkByD8f() {
        // Multipliable3f.by(Decimal8f factor) returns Decimal11f
        return multipliable.by(factorD8);
    }

    @Benchmark
    public Decimal11f benchmarkByMD8f() {
        // Multipliable3f.by(MutableDecimal8f factor) returns Decimal11f
        return multipliable.by(factorMD8);
    }

    // --- Benchmarks for by(Decimal9f factor) ---

    @Benchmark
    public Decimal12f benchmarkByD9f() {
        // Multipliable3f.by(Decimal9f factor) returns Decimal12f
        return multipliable.by(factorD9);
    }

    @Benchmark
    public Decimal12f benchmarkByMD9f() {
        // Multipliable3f.by(MutableDecimal9f factor) returns Decimal12f
        return multipliable.by(factorMD9);
    }

    // --- Benchmarks for by(Decimal10f factor) ---

    @Benchmark
    public Decimal13f benchmarkByD10f() {
        // Multipliable3f.by(Decimal10f factor) returns Decimal13f
        return multipliable.by(factorD10);
    }

    @Benchmark
    public Decimal13f benchmarkByMD10f() {
        // Multipliable3f.by(MutableDecimal10f factor) returns Decimal13f
        return multipliable.by(factorMD10);
    }

    // --- Benchmarks for by(Decimal11f factor) ---

    @Benchmark
    public Decimal14f benchmarkByD11f() {
        // Multipliable3f.by(Decimal11f factor) returns Decimal14f
        return multipliable.by(factorD11);
    }

    @Benchmark
    public Decimal14f benchmarkByMD11f() {
        // Multipliable3f.by(MutableDecimal11f factor) returns Decimal14f
        return multipliable.by(factorMD11);
    }

    // --- Benchmarks for by(Decimal12f factor) ---

    @Benchmark
    public Decimal15f benchmarkByD12f() {
        // Multipliable3f.by(Decimal12f factor) returns Decimal15f
        return multipliable.by(factorD12);
    }

    @Benchmark
    public Decimal15f benchmarkByMD12f() {
        // Multipliable3f.by(MutableDecimal12f factor) returns Decimal15f
        return multipliable.by(factorMD12);
    }

    // --- Benchmarks for by(Decimal13f factor) ---

    @Benchmark
    public Decimal16f benchmarkByD13f() {
        // Multipliable3f.by(Decimal13f factor) returns Decimal16f
        return multipliable.by(factorD13);
    }

    @Benchmark
    public Decimal16f benchmarkByMD13f() {
        // Multipliable3f.by(MutableDecimal13f factor) returns Decimal16f
        return multipliable.by(factorMD13);
    }

    // --- Benchmarks for by(Decimal14f factor) ---

    @Benchmark
    public Decimal17f benchmarkByD14f() {
        // Multipliable3f.by(Decimal14f factor) returns Decimal17f
        return multipliable.by(factorD14);
    }

    @Benchmark
    public Decimal17f benchmarkByMD14f() {
        // Multipliable3f.by(MutableDecimal14f factor) returns Decimal17f
        return multipliable.by(factorMD14);
    }

    // --- Benchmarks for by(Decimal15f factor) ---

    @Benchmark
    public Decimal18f benchmarkByD15f() {
        // Multipliable3f.by(Decimal15f factor) returns Decimal18f
        return multipliable.by(factorD15);
    }

    @Benchmark
    public Decimal18f benchmarkByMD15f() {
        // Multipliable3f.by(MutableDecimal15f factor) returns Decimal18f
        return multipliable.by(factorMD15);
    }
}
