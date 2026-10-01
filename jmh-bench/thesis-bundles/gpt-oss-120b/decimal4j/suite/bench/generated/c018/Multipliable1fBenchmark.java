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

    private Multipliable1f multipliable;

    private Decimal0f factor0;
    private MutableDecimal0f mutableFactor0;
    private Decimal2f factor2;
    private MutableDecimal2f mutableFactor2;
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
    private Decimal17f factor17;
    private MutableDecimal17f mutableFactor17;

    @Setup(Level.Trial)
    public void setup() {
        Decimal1f base = Decimal1f.valueOf(12345L); // 1234.5 with scale 1
        multipliable = new Multipliable1f(base);

        factor0 = Decimal0f.valueOf(1L);
        mutableFactor0 = MutableDecimal0f.unscaled(1L);
        factor2 = Decimal2f.valueOf(1L);
        mutableFactor2 = MutableDecimal2f.unscaled(1L);
        factor3 = Decimal3f.valueOf(1L);
        mutableFactor3 = MutableDecimal3f.unscaled(1L);
        factor4 = Decimal4f.valueOf(1L);
        mutableFactor4 = MutableDecimal4f.unscaled(1L);
        factor5 = Decimal5f.valueOf(1L);
        mutableFactor5 = MutableDecimal5f.unscaled(1L);
        factor6 = Decimal6f.valueOf(1L);
        mutableFactor6 = MutableDecimal6f.unscaled(1L);
        factor7 = Decimal7f.valueOf(1L);
        mutableFactor7 = MutableDecimal7f.unscaled(1L);
        factor8 = Decimal8f.valueOf(1L);
        mutableFactor8 = MutableDecimal8f.unscaled(1L);
        factor9 = Decimal9f.valueOf(1L);
        mutableFactor9 = MutableDecimal9f.unscaled(1L);
        factor10 = Decimal10f.valueOf(1L);
        mutableFactor10 = MutableDecimal10f.unscaled(1L);
        factor11 = Decimal11f.valueOf(1L);
        mutableFactor11 = MutableDecimal11f.unscaled(1L);
        factor12 = Decimal12f.valueOf(1L);
        mutableFactor12 = MutableDecimal12f.unscaled(1L);
        factor13 = Decimal13f.valueOf(1L);
        mutableFactor13 = MutableDecimal13f.unscaled(1L);
        factor14 = Decimal14f.valueOf(1L);
        mutableFactor14 = MutableDecimal14f.unscaled(1L);
        factor15 = Decimal15f.valueOf(1L);
        mutableFactor15 = MutableDecimal15f.unscaled(1L);
        factor16 = Decimal16f.valueOf(1L);
        mutableFactor16 = MutableDecimal16f.unscaled(1L);
        factor17 = Decimal17f.valueOf(1L);
        mutableFactor17 = MutableDecimal17f.unscaled(1L);
    }

    @Benchmark
    public Decimal2f benchmarkSquare() {
        return multipliable.square();
    }

    @Benchmark
    public Decimal2f benchmarkByDecimal1f() {
        Decimal1f factor = Decimal1f.valueOf(1L);
        return multipliable.by(factor);
    }

    @Benchmark
    public Decimal1f benchmarkByDecimal0f() {
        return multipliable.by(factor0);
    }

    @Benchmark
    public Decimal1f benchmarkByMutableDecimal0f() {
        return multipliable.by(mutableFactor0);
    }

    @Benchmark
    public Decimal3f benchmarkByDecimal2f() {
        return multipliable.by(factor2);
    }

    @Benchmark
    public Decimal3f benchmarkByMutableDecimal2f() {
        return multipliable.by(mutableFactor2);
    }

    @Benchmark
    public Decimal4f benchmarkByDecimal3f() {
        return multipliable.by(factor3);
    }

    @Benchmark
    public Decimal4f benchmarkByMutableDecimal3f() {
        return multipliable.by(mutableFactor3);
    }

    @Benchmark
    public Decimal5f benchmarkByDecimal4f() {
        return multipliable.by(factor4);
    }

    @Benchmark
    public Decimal5f benchmarkByMutableDecimal4f() {
        return multipliable.by(mutableFactor4);
    }

    @Benchmark
    public Decimal6f benchmarkByDecimal5f() {
        return multipliable.by(factor5);
    }

    @Benchmark
    public Decimal6f benchmarkByMutableDecimal5f() {
        return multipliable.by(mutableFactor5);
    }

    @Benchmark
    public Decimal7f benchmarkByDecimal6f() {
        return multipliable.by(factor6);
    }

    @Benchmark
    public Decimal7f benchmarkByMutableDecimal6f() {
        return multipliable.by(mutableFactor6);
    }

    @Benchmark
    public Decimal8f benchmarkByDecimal7f() {
        return multipliable.by(factor7);
    }

    @Benchmark
    public Decimal8f benchmarkByMutableDecimal7f() {
        return multipliable.by(mutableFactor7);
    }

    @Benchmark
    public Decimal9f benchmarkByDecimal8f() {
        return multipliable.by(factor8);
    }

    @Benchmark
    public Decimal9f benchmarkByMutableDecimal8f() {
        return multipliable.by(mutableFactor8);
    }

    @Benchmark
    public Decimal10f benchmarkByDecimal9f() {
        return multipliable.by(factor9);
    }

    @Benchmark
    public Decimal10f benchmarkByMutableDecimal9f() {
        return multipliable.by(mutableFactor9);
    }

    @Benchmark
    public Decimal11f benchmarkByDecimal10f() {
        return multipliable.by(factor10);
    }

    @Benchmark
    public Decimal11f benchmarkByMutableDecimal10f() {
        return multipliable.by(mutableFactor10);
    }

    @Benchmark
    public Decimal12f benchmarkByDecimal11f() {
        return multipliable.by(factor11);
    }

    @Benchmark
    public Decimal12f benchmarkByMutableDecimal11f() {
        return multipliable.by(mutableFactor11);
    }

    @Benchmark
    public Decimal13f benchmarkByDecimal12f() {
        return multipliable.by(factor12);
    }

    @Benchmark
    public Decimal13f benchmarkByMutableDecimal12f() {
        return multipliable.by(mutableFactor12);
    }

    @Benchmark
    public Decimal14f benchmarkByDecimal13f() {
        return multipliable.by(factor13);
    }

    @Benchmark
    public Decimal14f benchmarkByMutableDecimal13f() {
        return multipliable.by(mutableFactor13);
    }

    @Benchmark
    public Decimal15f benchmarkByDecimal14f() {
        return multipliable.by(factor14);
    }

    @Benchmark
    public Decimal15f benchmarkByMutableDecimal14f() {
        return multipliable.by(mutableFactor14);
    }

    @Benchmark
    public Decimal16f benchmarkByDecimal15f() {
        return multipliable.by(factor15);
    }

    @Benchmark
    public Decimal16f benchmarkByMutableDecimal15f() {
        return multipliable.by(mutableFactor15);
    }

    @Benchmark
    public Decimal17f benchmarkByDecimal16f() {
        return multipliable.by(factor16);
    }

    @Benchmark
    public Decimal17f benchmarkByMutableDecimal16f() {
        return multipliable.by(mutableFactor16);
    }

    @Benchmark
    public Decimal18f benchmarkByDecimal17f() {
        return multipliable.by(factor17);
    }

    @Benchmark
    public Decimal18f benchmarkByMutableDecimal17f() {
        return multipliable.by(mutableFactor17);
    }

    @Benchmark
    public int benchmarkHashCode() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean benchmarkEqualsSame() {
        return multipliable.equals(multipliable);
    }

    @Benchmark
    public boolean benchmarkEqualsDifferent() {
        Multipliable1f other = new Multipliable1f(Decimal1f.valueOf(999L));
        return multipliable.equals(other);
    }

    @Benchmark
    public String benchmarkToString() {
        return multipliable.toString();
    }
}
