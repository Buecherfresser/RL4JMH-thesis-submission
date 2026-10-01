package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable6f;
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
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.mutable.MutableDecimal12f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable6fBenchmark {

    private Multipliable6f baseMultipliable;

    // Factors for Decimal0f
    private Decimal0f factorD0;
    private MutableDecimal0f factorMD0;

    // Factors for Decimal1f
    private Decimal1f factorD1;
    private MutableDecimal1f factorMD1;

    // Factors for Decimal2f
    private Decimal2f factorD2;
    private MutableDecimal2f factorMD2;

    // Factors for Decimal3f
    private Decimal3f factorD3;
    private MutableDecimal3f factorMD3;

    // Factors for Decimal4f
    private Decimal4f factorD4;
    private MutableDecimal4f factorMD4;

    // Factors for Decimal5f
    private Decimal5f factorD5;
    private MutableDecimal5f factorMD5;

    // Factors for Decimal7f
    private Decimal7f factorD7;
    private MutableDecimal7f factorMD7;

    // Factors for Decimal8f
    private Decimal8f factorD8;
    private MutableDecimal8f factorMD8;

    // Factors for Decimal9f
    private Decimal9f factorD9;
    private MutableDecimal9f factorMD9;

    // Factors for Decimal10f
    private Decimal10f factorD10;
    private MutableDecimal10f factorMD10;

    // Factors for Decimal11f
    private Decimal11f factorD11;
    private MutableDecimal11f factorMD11;

    // Factors for Decimal12f
    private Decimal12f factorD12;
    private MutableDecimal12f factorMD12;


    @Setup(Level.Trial)
    public void setup() {
        // Initialize base Multipliable6f instance (using a simple value, e.g., 1.0)
        Decimal6f baseValue = Decimal6f.ONE;
        baseMultipliable = new Multipliable6f(baseValue);

        // Initialize factors (using simple values, e.g., 2.0)
        
        // D0 factors
        factorD0 = Decimal0f.TWO;
        factorMD0 = new MutableDecimal0f(Decimal0f.TWO);

        // D1 factors
        factorD1 = Decimal1f.TWO;
        factorMD1 = new MutableDecimal1f(Decimal1f.TWO);

        // D2 factors
        factorD2 = Decimal2f.TWO;
        factorMD2 = new MutableDecimal2f(Decimal2f.TWO);

        // D3 factors
        factorD3 = Decimal3f.TWO;
        factorMD3 = new MutableDecimal3f(Decimal3f.TWO);

        // D4 factors
        factorD4 = Decimal4f.TWO;
        factorMD4 = new MutableDecimal4f(Decimal4f.TWO);

        // D5 factors
        factorD5 = Decimal5f.TWO;
        factorMD5 = new MutableDecimal5f(Decimal5f.TWO);

        // D7 factors
        factorD7 = Decimal7f.TWO;
        factorMD7 = new MutableDecimal7f(Decimal7f.TWO);

        // D8 factors
        factorD8 = Decimal8f.TWO;
        factorMD8 = new MutableDecimal8f(Decimal8f.TWO);

        // D9 factors
        factorD9 = Decimal9f.TWO;
        factorMD9 = new MutableDecimal9f(Decimal9f.TWO);

        // D10 factors
        factorD10 = Decimal10f.TWO;
        factorMD10 = new MutableDecimal10f(Decimal10f.TWO);

        // D11 factors
        factorD11 = Decimal11f.TWO;
        factorMD11 = new MutableDecimal11f(Decimal11f.TWO);

        // D12 factors
        factorD12 = Decimal12f.TWO;
        factorMD12 = new MutableDecimal12f(Decimal12f.TWO);
    }

    // --- Benchmarks for square() ---

    @Benchmark
    public Decimal12f benchmarkSquare() {
        return baseMultipliable.square();
    }

    // --- Benchmarks for by(Decimal<Scale6f> factor) ---

    @Benchmark
    public Decimal12f benchmarkByDecimal6f() {
        return baseMultipliable.by(Decimal6f.ONE);
    }

    // --- Benchmarks for by(Decimal0f factor) ---

    @Benchmark
    public Decimal6f benchmarkByD0Immutable() {
        return baseMultipliable.by(factorD0);
    }

    @Benchmark
    public Decimal6f benchmarkByD0Mutable() {
        return baseMultipliable.by(factorMD0);
    }

    // --- Benchmarks for by(Decimal1f factor) ---

    @Benchmark
    public Decimal7f benchmarkByD1Immutable() {
        return baseMultipliable.by(factorD1);
    }

    @Benchmark
    public Decimal7f benchmarkByD1Mutable() {
        return baseMultipliable.by(factorMD1);
    }

    // --- Benchmarks for by(Decimal2f factor) ---

    @Benchmark
    public Decimal8f benchmarkByD2Immutable() {
        return baseMultipliable.by(factorD2);
    }

    @Benchmark
    public Decimal8f benchmarkByD2Mutable() {
        return baseMultipliable.by(factorMD2);
    }

    // --- Benchmarks for by(Decimal3f factor) ---

    @Benchmark
    public Decimal9f benchmarkByD3Immutable() {
        return baseMultipliable.by(factorD3);
    }

    @Benchmark
    public Decimal9f benchmarkByD3Mutable() {
        return baseMultipliable.by(factorMD3);
    }

    // --- Benchmarks for by(Decimal4f factor) ---

    @Benchmark
    public Decimal10f benchmarkByD4Immutable() {
        return baseMultipliable.by(factorD4);
    }

    @Benchmark
    public Decimal10f benchmarkByD4Mutable() {
        return baseMultipliable.by(factorMD4);
    }

    // --- Benchmarks for by(Decimal5f factor) ---

    @Benchmark
    public Decimal11f benchmarkByD5Immutable() {
        return baseMultipliable.by(factorD5);
    }

    @Benchmark
    public Decimal11f benchmarkByD5Mutable() {
        return baseMultipliable.by(factorMD5);
    }

    // --- Benchmarks for by(Decimal7f factor) ---

    @Benchmark
    public Decimal13f benchmarkByD7Immutable() {
        return baseMultipliable.by(factorD7);
    }

    @Benchmark
    public Decimal13f benchmarkByD7Mutable() {
        return baseMultipliable.by(factorMD7);
    }

    // --- Benchmarks for by(Decimal8f factor) ---

    @Benchmark
    public Decimal14f benchmarkByD8Immutable() {
        return baseMultipliable.by(factorD8);
    }

    @Benchmark
    public Decimal14f benchmarkByD8Mutable() {
        return baseMultipliable.by(factorMD8);
    }

    // --- Benchmarks for by(Decimal9f factor) ---

    @Benchmark
    public Decimal15f benchmarkByD9Immutable() {
        return baseMultipliable.by(factorD9);
    }

    @Benchmark
    public Decimal15f benchmarkByD9Mutable() {
        return baseMultipliable.by(factorMD9);
    }

    // --- Benchmarks for by(Decimal10f factor) ---

    @Benchmark
    public Decimal16f benchmarkByD10Immutable() {
        return baseMultipliable.by(factorD10);
    }

    @Benchmark
    public Decimal16f benchmarkByD10Mutable() {
        return baseMultipliable.by(factorMD10);
    }

    // --- Benchmarks for by(Decimal11f factor) ---

    @Benchmark
    public Decimal17f benchmarkByD11Immutable() {
        return baseMultipliable.by(factorD11);
    }

    @Benchmark
    public Decimal17f benchmarkByD11Mutable() {
        return baseMultipliable.by(factorMD11);
    }

    // --- Benchmarks for by(Decimal12f factor) ---

    @Benchmark
    public Decimal18f benchmarkByD12Immutable() {
        return baseMultipliable.by(factorD12);
    }

    @Benchmark
    public Decimal18f benchmarkByD12Mutable() {
        return baseMultipliable.by(factorMD12);
    }
}
