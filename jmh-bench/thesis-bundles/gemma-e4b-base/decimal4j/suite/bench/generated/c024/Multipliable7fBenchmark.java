package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable7f;
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
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.mutable.MutableDecimal11f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable7fBenchmark {

    private Multipliable7f multipliable7f;

    // Factors for testing multiplication methods
    private Decimal0f factorD0f;
    private MutableDecimal0f factorMD0f;
    private Decimal1f factorD1f;
    private MutableDecimal1f factorMD1f;
    private Decimal2f factorD2f;
    private MutableDecimal2f factorMD2f;
    private Decimal3f factorD3f;
    private MutableDecimal3f factorMD3f;
    private Decimal4f factorD4f;
    private MutableDecimal4f factorMD4f;
    private Decimal5f factorD5f;
    private MutableDecimal5f factorMD5f;
    private Decimal6f factorD6f;
    private MutableDecimal6f factorMD6f;
    private Decimal8f factorD8f;
    private MutableDecimal8f factorMD8f;
    private Decimal9f factorD9f;
    private MutableDecimal9f factorMD9f;
    private Decimal10f factorD10f;
    private MutableDecimal10f factorMD10f;
    private Decimal11f factorD11f;
    private MutableDecimal11f factorMD11f;

    // Factor for by(Decimal<Scale7f> factor)
    private Decimal7f factorD7f;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize base value (e.g., 1.234567)
        Decimal7f baseValue = Decimal7f.valueOf(1234567L);
        multipliable7f = new Multipliable7f(baseValue);

        // Initialize factors
        factorD0f = Decimal0f.valueOf(2L);
        factorMD0f = new MutableDecimal0f(2L);

        factorD1f = Decimal1f.valueOf(3L);
        factorMD1f = new MutableDecimal1f(3L);

        factorD2f = Decimal2f.valueOf(4L);
        factorMD2f = new MutableDecimal2f(4L);

        factorD3f = Decimal3f.valueOf(5L);
        factorMD3f = new MutableDecimal3f(5L);

        factorD4f = Decimal4f.valueOf(6L);
        factorMD4f = new MutableDecimal4f(6L);

        factorD5f = Decimal5f.valueOf(7L);
        factorMD5f = new MutableDecimal5f(7L);

        factorD6f = Decimal6f.valueOf(8L);
        factorMD6f = new MutableDecimal6f(8L);

        factorD8f = Decimal8f.valueOf(9L);
        factorMD8f = new MutableDecimal8f(9L);

        factorD9f = Decimal9f.valueOf(10L);
        factorMD9f = new MutableDecimal9f(10L);

        factorD10f = Decimal10f.valueOf(11L);
        factorMD10f = new MutableDecimal10f(11L);

        factorD11f = Decimal11f.valueOf(12L);
        factorMD11f = new MutableDecimal11f(12L);

        // Factor for by(Decimal<Scale7f> factor)
        factorD7f = Decimal7f.valueOf(13L);
    }

    @Benchmark
    public Decimal14f benchmarkSquare() {
        return multipliable7f.square();
    }

    @Benchmark
    public Decimal14f benchmarkByDecimal7f() {
        return multipliable7f.by(factorD7f);
    }

    @Benchmark
    public Decimal7f benchmarkByDecimal0f() {
        return multipliable7f.by(factorD0f);
    }

    @Benchmark
    public Decimal7f benchmarkByMutableDecimal0f() {
        return multipliable7f.by(factorMD0f);
    }

    @Benchmark
    public Decimal8f benchmarkByDecimal1f() {
        return multipliable7f.by(factorD1f);
    }

    @Benchmark
    public Decimal8f benchmarkByMutableDecimal1f() {
        return multipliable7f.by(factorMD1f);
    }

    @Benchmark
    public Decimal9f benchmarkByDecimal2f() {
        return multipliable7f.by(factorD2f);
    }

    @Benchmark
    public Decimal9f benchmarkByMutableDecimal2f() {
        return multipliable7f.by(factorMD2f);
    }

    @Benchmark
    public Decimal10f benchmarkByDecimal3f() {
        return multipliable7f.by(factorD3f);
    }

    @Benchmark
    public Decimal10f benchmarkByMutableDecimal3f() {
        return multipliable7f.by(factorMD3f);
    }

    @Benchmark
    public Decimal11f benchmarkByDecimal4f() {
        return multipliable7f.by(factorD4f);
    }

    @Benchmark
    public Decimal11f benchmarkByMutableDecimal4f() {
        return multipliable7f.by(factorMD4f);
    }

    @Benchmark
    public Decimal12f benchmarkByDecimal5f() {
        return multipliable7f.by(factorD5f);
    }

    @Benchmark
    public Decimal12f benchmarkByMutableDecimal5f() {
        return multipliable7f.by(factorMD5f);
    }

    @Benchmark
    public Decimal13f benchmarkByDecimal6f() {
        return multipliable7f.by(factorD6f);
    }

    @Benchmark
    public Decimal13f benchmarkByMutableDecimal6f() {
        return multipliable7f.by(factorMD6f);
    }

    @Benchmark
    public Decimal15f benchmarkByDecimal8f() {
        return multipliable7f.by(factorD8f);
    }

    @Benchmark
    public Decimal15f benchmarkByMutableDecimal8f() {
        return multipliable7f.by(factorMD8f);
    }

    @Benchmark
    public Decimal16f benchmarkByDecimal9f() {
        return multipliable7f.by(factorD9f);
    }

    @Benchmark
    public Decimal16f benchmarkByMutableDecimal9f() {
        return multipliable7f.by(factorMD9f);
    }

    @Benchmark
    public Decimal17f benchmarkByDecimal10f() {
        return multipliable7f.by(factorD10f);
    }

    @Benchmark
    public Decimal17f benchmarkByMutableDecimal10f() {
        return multipliable7f.by(factorMD10f);
    }

    @Benchmark
    public Decimal18f benchmarkByDecimal11f() {
        return multipliable7f.by(factorD11f);
    }

    @Benchmark
    public Decimal18f benchmarkByMutableDecimal11f() {
        return multipliable7f.by(factorMD11f);
    }
}
