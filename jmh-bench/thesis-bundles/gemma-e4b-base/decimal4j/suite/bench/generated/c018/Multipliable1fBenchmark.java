package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable1f;
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

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable1fBenchmark {

    private Multipliable1f multipliable1f;

    // Factors for multiplication methods
    private Decimal0f factorD0;
    private MutableDecimal0f factorM0;
    private Decimal2f factorD2;
    private MutableDecimal2f factorM2;
    private Decimal3f factorD3;
    private MutableDecimal3f factorM3;
    private Decimal4f factorD4;
    private MutableDecimal4f factorM4;
    private Decimal5f factorD5;
    private MutableDecimal5f factorM5;
    private Decimal6f factorD6;
    private MutableDecimal6f factorM6;
    private Decimal7f factorD7;
    private MutableDecimal7f factorM7;
    private Decimal8f factorD8;
    private MutableDecimal8f factorM8;
    private Decimal9f factorD9;
    private MutableDecimal9f factorM9;
    private Decimal10f factorD10;
    private MutableDecimal10f factorM10;
    private Decimal11f factorD11;
    private MutableDecimal11f factorM11;
    private Decimal12f factorD12;
    private MutableDecimal12f factorM12;
    private Decimal13f factorD13;
    private MutableDecimal13f factorM13;
    private Decimal14f factorD14;
    private MutableDecimal14f factorM14;
    private Decimal15f factorD15;
    private MutableDecimal15f factorM15;
    private Decimal16f factorD16;
    private MutableDecimal16f factorM16;
    private Decimal17f factorD17;
    private MutableDecimal17f factorM17;

    @Setup(Level.Trial)
    public void setup() {
        // Base value for Multipliable1f (using a simple Decimal1f)
        Decimal1f baseValue = Decimal1f.ONE;
        multipliable1f = new Multipliable1f(baseValue);

        // Initialize factors (using simple values like 2 or 1)
        factorD0 = Decimal0f.TWO;
        factorM0 = new MutableDecimal0f(2);

        factorD2 = Decimal2f.FIVE;
        factorM2 = new MutableDecimal2f(5);

        factorD3 = Decimal3f.ONE;
        factorM3 = new MutableDecimal3f(1);

        factorD4 = Decimal4f.TWO;
        factorM4 = new MutableDecimal4f(2);

        factorD5 = Decimal5f.TEN;
        factorM5 = new MutableDecimal5f(10);

        factorD6 = Decimal6f.ONE;
        factorM6 = new MutableDecimal6f(1);

        factorD7 = Decimal7f.TWO;
        factorM7 = new MutableDecimal7f(2);

        factorD8 = Decimal8f.ONE;
        factorM8 = new MutableDecimal8f(1);

        factorD9 = Decimal9f.TWO;
        factorM9 = new MutableDecimal9f(2);

        factorD10 = Decimal10f.ONE;
        factorM10 = new MutableDecimal10f(1);

        factorD11 = Decimal11f.TWO;
        factorM11 = new MutableDecimal11f(2);

        factorD12 = Decimal12f.ONE;
        factorM12 = new MutableDecimal12f(1);

        factorD13 = Decimal13f.TWO;
        factorM13 = new MutableDecimal13f(2);

        factorD14 = Decimal14f.ONE;
        factorM14 = new MutableDecimal14f(1);

        factorD15 = Decimal15f.TWO;
        factorM15 = new MutableDecimal15f(2);

        factorD16 = Decimal16f.ONE;
        factorM16 = new MutableDecimal16f(1);

        factorD17 = Decimal17f.TWO;
        factorM17 = new MutableDecimal17f(2);
    }

    // --- Square operation ---

    @Benchmark
    public Decimal2f benchmarkSquare() {
        return multipliable1f.square();
    }

    // --- Multiplication by Decimal2f ---

    @Benchmark
    public Decimal3f benchmarkByDecimal2f() {
        return multipliable1f.by(factorD2);
    }

    // --- Multiplication by Decimal0f ---

    @Benchmark
    public Decimal1f benchmarkByDecimal0f() {
        return multipliable1f.by(factorD0);
    }

    // --- Multiplication by MutableDecimal0f ---

    @Benchmark
    public Decimal1f benchmarkByMutableDecimal0f() {
        return multipliable1f.by(factorM0);
    }

    // --- Multiplication by Decimal2f ---

    @Benchmark
    public Decimal3f benchmarkByDecimal2f_D3() {
        return multipliable1f.by(factorD2);
    }

    // --- Multiplication by MutableDecimal2f ---

    @Benchmark
    public Decimal3f benchmarkByMutableDecimal2f_M3() {
        return multipliable1f.by(factorM2);
    }

    // --- Multiplication by Decimal3f ---

    @Benchmark
    public Decimal4f benchmarkByDecimal3f() {
        return multipliable1f.by(factorD3);
    }

    // --- Multiplication by MutableDecimal3f ---

    @Benchmark
    public Decimal4f benchmarkByMutableDecimal3f_M4() {
        return multipliable1f.by(factorM3);
    }

    // --- Multiplication by Decimal4f ---

    @Benchmark
    public Decimal5f benchmarkByDecimal4f() {
        return multipliable1f.by(factorD4);
    }

    // --- Multiplication by MutableDecimal4f ---

    @Benchmark
    public Decimal5f benchmarkByMutableDecimal4f_M5() {
        return multipliable1f.by(factorM4);
    }

    // --- Multiplication by Decimal5f ---

    @Benchmark
    public Decimal6f benchmarkByDecimal5f() {
        return multipliable1f.by(factorD5);
    }

    // --- Multiplication by MutableDecimal5f ---

    @Benchmark
    public Decimal6f benchmarkByMutableDecimal5f_M6() {
        return multipliable1f.by(factorM5);
    }

    // --- Multiplication by Decimal6f ---

    @Benchmark
    public Decimal7f benchmarkByDecimal6f() {
        return multipliable1f.by(factorD6);
    }

    // --- Multiplication by MutableDecimal6f ---

    @Benchmark
    public Decimal7f benchmarkByMutableDecimal6f_M7() {
        return multipliable1f.by(factorM6);
    }

    // --- Multiplication by Decimal7f ---

    @Benchmark
    public Decimal8f benchmarkByDecimal7f() {
        return multipliable1f.by(factorD7);
    }

    // --- Multiplication by MutableDecimal7f ---

    @Benchmark
    public Decimal8f benchmarkByMutableDecimal7f_M8() {
        return multipliable1f.by(factorM7);
    }

    // --- Multiplication by Decimal8f ---

    @Benchmark
    public Decimal9f benchmarkByDecimal8f() {
        return multipliable1f.by(factorD8);
    }

    // --- Multiplication by MutableDecimal8f ---

    @Benchmark
    public Decimal9f benchmarkByMutableDecimal8f_M9() {
        return multipliable1f.by(factorM8);
    }

    // --- Multiplication by Decimal9f ---

    @Benchmark
    public Decimal10f benchmarkByDecimal9f() {
        return multipliable1f.by(factorD9);
    }

    // --- Multiplication by MutableDecimal9f ---

    @Benchmark
    public Decimal10f benchmarkByMutableDecimal9f_M10() {
        return multipliable1f.by(factorM9);
    }

    // --- Multiplication by Decimal10f ---

    @Benchmark
    public Decimal11f benchmarkByDecimal10f() {
        return multipliable1f.by(factorD10);
    }

    // --- Multiplication by MutableDecimal10f ---

    @Benchmark
    public Decimal11f benchmarkByMutableDecimal10f_M11() {
        return multipliable1f.by(factorM10);
    }

    // --- Multiplication by Decimal11f ---

    @Benchmark
    public Decimal12f benchmarkByDecimal11f() {
        return multipliable1f.by(factorD11);
    }

    // --- Multiplication by MutableDecimal11f ---

    @Benchmark
    public Decimal12f benchmarkByMutableDecimal11f_M12() {
        return multipliable1f.by(factorM11);
    }

    // --- Multiplication by Decimal12f ---

    @Benchmark
    public Decimal13f benchmarkByDecimal12f() {
        return multipliable1f.by(factorD12);
    }

    // --- Multiplication by MutableDecimal12f ---

    @Benchmark
    public Decimal13f benchmarkByMutableDecimal12f_M13() {
        return multipliable1f.by(factorM12);
    }

    // --- Multiplication by Decimal13f ---

    @Benchmark
    public Decimal14f benchmarkByDecimal13f() {
        return multipliable1f.by(factorD13);
    }

    // --- Multiplication by MutableDecimal13f ---

    @Benchmark
    public Decimal14f benchmarkByMutableDecimal13f_M14() {
        return multipliable1f.by(factorM13);
    }

    // --- Multiplication by Decimal14f ---

    @Benchmark
    public Decimal15f benchmarkByDecimal14f() {
        return multipliable1f.by(factorD14);
    }

    // --- Multiplication by MutableDecimal14f ---

    @Benchmark
    public Decimal15f benchmarkByMutableDecimal14f_M15() {
        return multipliable1f.by(factorM14);
    }

    // --- Multiplication by Decimal15f ---

    @Benchmark
    public Decimal16f benchmarkByDecimal15f() {
        return multipliable1f.by(factorD15);
    }

    // --- Multiplication by MutableDecimal15f ---

    @Benchmark
    public Decimal16f benchmarkByMutableDecimal15f_M16() {
        return multipliable1f.by(factorM15);
    }

    // --- Multiplication by Decimal16f ---

    @Benchmark
    public Decimal17f benchmarkByDecimal16f() {
        return multipliable1f.by(factorD16);
    }

    // --- Multiplication by MutableDecimal16f ---

    @Benchmark
    public Decimal17f benchmarkByMutableDecimal16f_M17() {
        return multipliable1f.by(factorM16);
    }

    // --- Multiplication by Decimal17f ---

    @Benchmark
    public Decimal18f benchmarkByDecimal17f() {
        return multipliable1f.by(factorD17);
    }

    // --- Multiplication by MutableDecimal17f ---

    @Benchmark
    public Decimal18f benchmarkByMutableDecimal17f_M18() {
        return multipliable1f.by(factorM17);
    }
}
