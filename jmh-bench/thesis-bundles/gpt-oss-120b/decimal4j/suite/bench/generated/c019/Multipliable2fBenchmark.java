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

    private Multipliable2f multipliable;

    // factors for each overload
    private Decimal2f factor2;
    private Decimal0f factor0;
    private MutableDecimal0f mutableFactor0;
    private Decimal1f factor1;
    private MutableDecimal1f mutableFactor1;
    private Decimal3f factor3;
    private MutableDecimal3f mutableFactor3;
    private Decimal4f factor4;
    private MutableDecimal4f mutableFactor4;
    private Decimal5f factor5;
    private MutableDecimal5f mutableFactor5;
    private Decimal6f factor6;
    private MutableDecimal6f mutableFactor6;
    private Decimal7f factor7;
    private MutableDecimal7f mutableFactor7;
    private Decimal8f factor8;
    private MutableDecimal8f mutableFactor8;
    private Decimal9f factor9;
    private MutableDecimal9f mutableFactor9;
    private Decimal10f factor10;
    private MutableDecimal10f mutableFactor10;
    private Decimal11f factor11;
    private MutableDecimal11f mutableFactor11;
    private Decimal12f factor12;
    private MutableDecimal12f mutableFactor12;
    private Decimal13f factor13;
    private MutableDecimal13f mutableFactor13;
    private Decimal14f factor14;
    private MutableDecimal14f mutableFactor14;
    private Decimal15f factor15;
    private MutableDecimal15f mutableFactor15;
    private Decimal16f factor16;
    private MutableDecimal16f mutableFactor16;

    @Setup(Level.Trial)
    public void setup() {
        // base value for the multipliable (scale 2)
        Decimal2f base = Decimal2f.valueOf(12345L); // 123.45
        multipliable = new Multipliable2f(base);

        // initialize factors
        factor2 = Decimal2f.valueOf(2L);
        factor0 = Decimal0f.ONE;
        mutableFactor0 = new MutableDecimal0f(1L);
        factor1 = Decimal1f.ONE;
        mutableFactor1 = new MutableDecimal1f(1L);
        factor3 = Decimal3f.ONE;
        mutableFactor3 = new MutableDecimal3f(1L);
        factor4 = Decimal4f.ONE;
        mutableFactor4 = new MutableDecimal4f(1L);
        factor5 = Decimal5f.ONE;
        mutableFactor5 = new MutableDecimal5f(1L);
        factor6 = Decimal6f.ONE;
        mutableFactor6 = new MutableDecimal6f(1L);
        factor7 = Decimal7f.ONE;
        mutableFactor7 = new MutableDecimal7f(1L);
        factor8 = Decimal8f.ONE;
        mutableFactor8 = new MutableDecimal8f(1L);
        factor9 = Decimal9f.ONE;
        mutableFactor9 = new MutableDecimal9f(1L);
        factor10 = Decimal10f.ONE;
        mutableFactor10 = new MutableDecimal10f(1L);
        factor11 = Decimal11f.ONE;
        mutableFactor11 = new MutableDecimal11f(1L);
        factor12 = Decimal12f.ONE;
        mutableFactor12 = new MutableDecimal12f(1L);
        factor13 = Decimal13f.ONE;
        mutableFactor13 = new MutableDecimal13f(1L);
        factor14 = Decimal14f.ONE;
        mutableFactor14 = new MutableDecimal14f(1L);
        factor15 = Decimal15f.ONE;
        mutableFactor15 = new MutableDecimal15f(1L);
        factor16 = Decimal16f.ONE;
        mutableFactor16 = new MutableDecimal16f(1L);
    }

    @Benchmark
    public Decimal4f benchSquare() {
        return multipliable.square();
    }

    @Benchmark
    public Decimal4f benchByDecimal2f() {
        return multipliable.by(factor2);
    }

    @Benchmark
    public Decimal2f benchByDecimal0f() {
        return multipliable.by(factor0);
    }

    @Benchmark
    public Decimal2f benchByMutableDecimal0f() {
        return multipliable.by(mutableFactor0);
    }

    @Benchmark
    public Decimal3f benchByDecimal1f() {
        return multipliable.by(factor1);
    }

    @Benchmark
    public Decimal3f benchByMutableDecimal1f() {
        return multipliable.by(mutableFactor1);
    }

    @Benchmark
    public Decimal5f benchByDecimal3f() {
        return multipliable.by(factor3);
    }

    @Benchmark
    public Decimal5f benchByMutableDecimal3f() {
        return multipliable.by(mutableFactor3);
    }

    @Benchmark
    public Decimal6f benchByDecimal4f() {
        return multipliable.by(factor4);
    }

    @Benchmark
    public Decimal6f benchByMutableDecimal4f() {
        return multipliable.by(mutableFactor4);
    }

    @Benchmark
    public Decimal7f benchByDecimal5f() {
        return multipliable.by(factor5);
    }

    @Benchmark
    public Decimal7f benchByMutableDecimal5f() {
        return multipliable.by(mutableFactor5);
    }

    @Benchmark
    public Decimal8f benchByDecimal6f() {
        return multipliable.by(factor6);
    }

    @Benchmark
    public Decimal8f benchByMutableDecimal6f() {
        return multipliable.by(mutableFactor6);
    }

    @Benchmark
    public Decimal9f benchByDecimal7f() {
        return multipliable.by(factor7);
    }

    @Benchmark
    public Decimal9f benchByMutableDecimal7f() {
        return multipliable.by(mutableFactor7);
    }

    @Benchmark
    public Decimal10f benchByDecimal8f() {
        return multipliable.by(factor8);
    }

    @Benchmark
    public Decimal10f benchByMutableDecimal8f() {
        return multipliable.by(mutableFactor8);
    }

    @Benchmark
    public Decimal11f benchByDecimal9f() {
        return multipliable.by(factor9);
    }

    @Benchmark
    public Decimal11f benchByMutableDecimal9f() {
        return multipliable.by(mutableFactor9);
    }

    @Benchmark
    public Decimal12f benchByDecimal10f() {
        return multipliable.by(factor10);
    }

    @Benchmark
    public Decimal12f benchByMutableDecimal10f() {
        return multipliable.by(mutableFactor10);
    }

    @Benchmark
    public Decimal13f benchByDecimal11f() {
        return multipliable.by(factor11);
    }

    @Benchmark
    public Decimal13f benchByMutableDecimal11f() {
        return multipliable.by(mutableFactor11);
    }

    @Benchmark
    public Decimal14f benchByDecimal12f() {
        return multipliable.by(factor12);
    }

    @Benchmark
    public Decimal14f benchByMutableDecimal12f() {
        return multipliable.by(mutableFactor12);
    }

    @Benchmark
    public Decimal15f benchByDecimal13f() {
        return multipliable.by(factor13);
    }

    @Benchmark
    public Decimal15f benchByMutableDecimal13f() {
        return multipliable.by(mutableFactor13);
    }

    @Benchmark
    public Decimal16f benchByDecimal14f() {
        return multipliable.by(factor14);
    }

    @Benchmark
    public Decimal16f benchByMutableDecimal14f() {
        return multipliable.by(mutableFactor14);
    }

    @Benchmark
    public Decimal17f benchByDecimal15f() {
        return multipliable.by(factor15);
    }

    @Benchmark
    public Decimal17f benchByMutableDecimal15f() {
        return multipliable.by(mutableFactor15);
    }

    @Benchmark
    public Decimal18f benchByDecimal16f() {
        return multipliable.by(factor16);
    }

    @Benchmark
    public Decimal18f benchByMutableDecimal16f() {
        return multipliable.by(mutableFactor16);
    }
}
