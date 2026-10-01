package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable14f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable14fBenchmark {

    private Multipliable14f multipliable14f;

    // Factors for Decimal0f
    private Decimal0f factorD0f;
    private MutableDecimal0f factorMD0f;

    // Factors for Decimal1f
    private Decimal1f factorD1f;
    private MutableDecimal1f factorMD1f;

    // Factors for Decimal2f
    private Decimal2f factorD2f;
    private MutableDecimal2f factorMD2f;

    // Factors for Decimal3f
    private Decimal3f factorD3f;
    private MutableDecimal3f factorMD3f;

    // Factors for Decimal4f
    private Decimal4f factorD4f;
    private MutableDecimal4f factorMD4f;

    @Setup
    public void setup() {
        // 1. Setup the base value for Multipliable14f
        // Using a simple, non-zero value for testing
        Decimal14f baseValue = Decimal14f.valueOf("123.4567890123456");
        multipliable14f = new Multipliable14f(baseValue);

        // 2. Setup factors
        // Decimal0f factors
        factorD0f = Decimal0f.valueOf("2");
        factorMD0f = new MutableDecimal0f("2");

        // Decimal1f factors
        factorD1f = Decimal1f.valueOf("3");
        factorMD1f = new MutableDecimal1f("3");

        // Decimal2f factors
        factorD2f = Decimal2f.valueOf("4");
        factorMD2f = new MutableDecimal2f("4");

        // Decimal3f factors
        factorD3f = Decimal3f.valueOf("5");
        factorMD3f = new MutableDecimal3f("5");

        // Decimal4f factors
        factorD4f = Decimal4f.valueOf("6");
        factorMD4f = new MutableDecimal4f("6");
    }

    @Benchmark
    public Decimal14f by_Decimal0f_immutable() {
        return multipliable14f.by(factorD0f);
    }

    @Benchmark
    public Decimal14f by_Decimal0f_mutable() {
        return multipliable14f.by(factorMD0f);
    }

    @Benchmark
    public Decimal15f by_Decimal1f_immutable() {
        return multipliable14f.by(factorD1f);
    }

    @Benchmark
    public Decimal15f by_Decimal1f_mutable() {
        return multipliable14f.by(factorMD1f);
    }

    @Benchmark
    public Decimal16f by_Decimal2f_immutable() {
        return multipliable14f.by(factorD2f);
    }

    @Benchmark
    public Decimal16f by_Decimal2f_mutable() {
        return multipliable14f.by(factorMD2f);
    }

    @Benchmark
    public Decimal17f by_Decimal3f_immutable() {
        return multipliable14f.by(factorD3f);
    }

    @Benchmark
    public Decimal17f by_Decimal3f_mutable() {
        return multipliable14f.by(factorMD3f);
    }

    @Benchmark
    public Decimal18f by_Decimal4f_immutable() {
        return multipliable14f.by(factorD4f);
    }

    @Benchmark
    public Decimal18f by_Decimal4f_mutable() {
        return multipliable14f.by(factorMD4f);
    }
}
