package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable5f;
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
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.mutable.MutableDecimal12f;
import org.decimal4j.mutable.MutableDecimal13f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable5fBenchmark {

    private Multipliable5f subject;

    // Factors for multiplication
    private Decimal0f factorD0;
    private MutableDecimal0f factorMD0;
    private Decimal1f factorD1;
    private MutableDecimal1f factorMD1;
    private Decimal2f factorD2;
    private MutableDecimal2f factorMD2;
    private Decimal3f factorD3;
    private MutableDecimal3f factorMD3;
    private Decimal4f factorD4;
    private MutableDecimal4f factorMD4;
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

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the subject (Multipliable5f)
        // We use a simple Decimal5f value for the subject
        Decimal5f baseValue = Decimal5f.valueOf(12345L);
        subject = new Multipliable5f(baseValue);

        // Initialize factors (using simple, non-zero values)
        factorD0 = Decimal0f.valueOf(2L);
        factorMD0 = new MutableDecimal0f(2L);

        factorD1 = Decimal1f.valueOf(3L);
        factorMD1 = new MutableDecimal1f(3L);

        factorD2 = Decimal2f.valueOf(4L);
        factorMD2 = new MutableDecimal2f(4L);

        factorD3 = Decimal3f.valueOf(5L);
        factorMD3 = new MutableDecimal3f(5L);

        factorD4 = Decimal4f.valueOf(6L);
        factorMD4 = new MutableDecimal4f(6L);

        factorD6 = Decimal6f.valueOf(7L);
        factorMD6 = new MutableDecimal6f(7L);

        factorD7 = Decimal7f.valueOf(8L);
        factorMD7 = new MutableDecimal7f(8L);

        factorD8 = Decimal8f.valueOf(9L);
        factorMD8 = new MutableDecimal8f(9L);

        factorD9 = Decimal9f.valueOf(10L);
        factorMD9 = new MutableDecimal9f(10L);

        factorD10 = Decimal10f.valueOf(11L);
        factorMD10 = new MutableDecimal10f(11L);

        factorD11 = Decimal11f.valueOf(12L);
        factorMD11 = new MutableDecimal11f(12L);

        factorD12 = Decimal12f.valueOf(13L);
        factorMD12 = new MutableDecimal12f(13L);

        factorD13 = Decimal13f.valueOf(14L);
        factorMD13 = new MutableDecimal13f(14L);
    }

    // --- Benchmarks for square() ---

    @Benchmark
    public Decimal10f benchmarkSquare() {
        // subject.square() returns Decimal10f
        return subject.square();
    }

    // --- Benchmarks for by(Decimal<Scale5f> factor) ---

    @Benchmark
    public Decimal10f benchmarkByDecimal5f() {
        // subject.by(Decimal<Scale5f> factor) returns Decimal10f
        return subject.by(Decimal5f.valueOf(1L));
    }

    // --- Benchmarks for by(MutableDecimal0f factor) ---

    @Benchmark
    public Decimal5f benchmarkByMutableDecimal0f() {
        // subject.by(MutableDecimal0f factor) returns Decimal5f
        return subject.by(factorMD0);
    }

    // --- Benchmarks for by(Decimal0f factor) ---

    @Benchmark
    public Decimal5f benchmarkByDecimal0f() {
        // subject.by(Decimal0f factor) returns Decimal5f
        return subject.by(factorD0);
    }

    // --- Benchmarks for by(Decimal1f factor) ---

    @Benchmark
    public Decimal6f benchmarkByDecimal1f() {
        // subject.by(Decimal1f factor) returns Decimal6f
        return subject.by(factorD1);
    }

    // --- Benchmarks for by(MutableDecimal1f factor) ---

    @Benchmark
    public Decimal6f benchmarkByMutableDecimal1f() {
        // subject.by(MutableDecimal1f factor) returns Decimal6f
        return subject.by(factorMD1);
    }

    // --- Benchmarks for by(Decimal2f factor) ---

    @Benchmark
    public Decimal7f benchmarkByDecimal2f() {
        // subject.by(Decimal2f factor) returns Decimal7f
        return subject.by(factorD2);
    }

    // --- Benchmarks for by(MutableDecimal2f factor) ---

    @Benchmark
    public Decimal7f benchmarkByMutableDecimal2f() {
        // subject.by(MutableDecimal2f factor) returns Decimal7f
        return subject.by(factorMD2);
    }

    // --- Benchmarks for by(Decimal3f factor) ---

    @Benchmark
    public Decimal8f benchmarkByDecimal3f() {
        // subject.by(Decimal3f factor) returns Decimal8f
        return subject.by(factorD3);
    }

    // --- Benchmarks for by(MutableDecimal3f factor) ---

    @Benchmark
    public Decimal8f benchmarkByMutableDecimal3f() {
        // subject.by(MutableDecimal3f factor) returns Decimal8f
        return subject.by(factorMD3);
    }

    // --- Benchmarks for by(Decimal4f factor) ---

    @Benchmark
    public Decimal9f benchmarkByDecimal4f() {
        // subject.by(Decimal4f factor) returns Decimal9f
        return subject.by(factorD4);
    }

    // --- Benchmarks for by(MutableDecimal4f factor) ---

    @Benchmark
    public Decimal9f benchmarkByMutableDecimal4f() {
        // subject.by(MutableDecimal4f factor) returns Decimal9f
        return subject.by(factorMD4);
    }

    // --- Benchmarks for by(Decimal6f factor) ---

    @Benchmark
    public Decimal11f benchmarkByDecimal6f() {
        // subject.by(Decimal6f factor) returns Decimal11f
        return subject.by(factorD6);
    }

    // --- Benchmarks for by(MutableDecimal6f factor) ---

    @Benchmark
    public Decimal11f benchmarkByMutableDecimal6f() {
        // subject.by(MutableDecimal6f factor) returns Decimal11f
        return subject.by(factorMD6);
    }

    // --- Benchmarks for by(Decimal7f factor) ---

    @Benchmark
    public Decimal12f benchmarkByDecimal7f() {
        // subject.by(Decimal7f factor) returns Decimal12f
        return subject.by(factorD7);
    }

    // --- Benchmarks for by(MutableDecimal7f factor) ---

    @Benchmark
    public Decimal12f benchmarkByMutableDecimal7f() {
        // subject.by(MutableDecimal7f factor) returns Decimal12f
        return subject.by(factorMD7);
    }

    // --- Benchmarks for by(Decimal8f factor) ---

    @Benchmark
    public Decimal13f benchmarkByDecimal8f() {
        // subject.by(Decimal8f factor) returns Decimal13f
        return subject.by(factorD8);
    }

    // --- Benchmarks for by(MutableDecimal8f factor) ---

    @Benchmark
    public Decimal13f benchmarkByMutableDecimal8f() {
        // subject.by(MutableDecimal8f factor) returns Decimal13f
        return subject.by(factorMD8);
    }

    // --- Benchmarks for by(Decimal9f factor) ---

    @Benchmark
    public Decimal14f benchmarkByDecimal9f() {
        // subject.by(Decimal9f factor) returns Decimal14f
        return subject.by(factorD9);
    }

    // --- Benchmarks for by(MutableDecimal9f factor) ---

    @Benchmark
    public Decimal14f benchmarkByMutableDecimal9f() {
        // subject.by(MutableDecimal9f factor) returns Decimal14f
        return subject.by(factorMD9);
    }

    // --- Benchmarks for by(Decimal10f factor) ---

    @Benchmark
    public Decimal15f benchmarkByDecimal10f() {
        // subject.by(Decimal10f factor) returns Decimal15f
        return subject.by(factorD10);
    }

    // --- Benchmarks for by(MutableDecimal10f factor) ---

    @Benchmark
    public Decimal15f benchmarkByMutableDecimal10f() {
        // subject.by(MutableDecimal10f factor) returns Decimal15f
        return subject.by(factorMD10);
    }

    // --- Benchmarks for by(Decimal11f factor) ---

    @Benchmark
    public Decimal16f benchmarkByDecimal11f() {
        // subject.by(Decimal11f factor) returns Decimal16f
        return subject.by(factorD11);
    }

    // --- Benchmarks for by(MutableDecimal11f factor) ---

    @Benchmark
    public Decimal16f benchmarkByMutableDecimal11f() {
        // subject.by(MutableDecimal11f factor) returns Decimal16f
        return subject.by(factorMD11);
    }

    // --- Benchmarks for by(Decimal12f factor) ---

    @Benchmark
    public Decimal17f benchmarkByDecimal12f() {
        // subject.by(Decimal12f factor) returns Decimal17f
        return subject.by(factorD12);
    }

    // --- Benchmarks for by(MutableDecimal12f factor) ---

    @Benchmark
    public Decimal17f benchmarkByMutableDecimal12f() {
        // subject.by(MutableDecimal12f factor) returns Decimal17f
        return subject.by(factorMD12);
    }

    // --- Benchmarks for by(Decimal13f factor) ---

    @Benchmark
    public Decimal18f benchmarkByDecimal13f() {
        // subject.by(Decimal13f factor) returns Decimal18f
        return subject.by(factorD13);
    }

    // --- Benchmarks for by(MutableDecimal13f factor) ---

    @Benchmark
    public Decimal18f benchmarkByMutableDecimal13f() {
        // subject.by(MutableDecimal13f factor) returns Decimal18f
        return subject.by(factorMD13);
    }
}
