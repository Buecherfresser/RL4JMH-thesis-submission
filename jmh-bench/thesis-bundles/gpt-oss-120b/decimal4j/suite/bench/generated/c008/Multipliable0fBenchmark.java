package bench.generated.c008;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
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

    private Multipliable0f multipliable;

    private Decimal0f factor0;
    private Decimal1f factor1;
    private MutableDecimal1f mutableFactor1;
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
    private Decimal18f factor18;
    private MutableDecimal18f mutableFactor18;

    @Setup
    public void setup() {
        Decimal0f base = Decimal0f.ONE;
        multipliable = new Multipliable0f(base);

        factor0 = Decimal0f.ONE;
        factor1 = Decimal1f.ONE;
        mutableFactor1 = MutableDecimal1f.one();
        factor2 = Decimal2f.ONE;
        mutableFactor2 = MutableDecimal2f.one();
        factor3 = Decimal3f.ONE;
        mutableFactor3 = MutableDecimal3f.one();
        factor4 = Decimal4f.ONE;
        mutableFactor4 = MutableDecimal4f.one();
        factor5 = Decimal5f.ONE;
        mutableFactor5 = MutableDecimal5f.one();
        factor6 = Decimal6f.ONE;
        mutableFactor6 = MutableDecimal6f.one();
        factor7 = Decimal7f.ONE;
        mutableFactor7 = MutableDecimal7f.one();
        factor8 = Decimal8f.ONE;
        mutableFactor8 = MutableDecimal8f.one();
        factor9 = Decimal9f.ONE;
        mutableFactor9 = MutableDecimal9f.one();
        factor10 = Decimal10f.ONE;
        mutableFactor10 = MutableDecimal10f.one();
        factor11 = Decimal11f.ONE;
        mutableFactor11 = MutableDecimal11f.one();
        factor12 = Decimal12f.ONE;
        mutableFactor12 = MutableDecimal12f.one();
        factor13 = Decimal13f.ONE;
        mutableFactor13 = MutableDecimal13f.one();
        factor14 = Decimal14f.ONE;
        mutableFactor14 = MutableDecimal14f.one();
        factor15 = Decimal15f.ONE;
        mutableFactor15 = MutableDecimal15f.one();
        factor16 = Decimal16f.ONE;
        mutableFactor16 = MutableDecimal16f.one();
        factor17 = Decimal17f.ONE;
        mutableFactor17 = MutableDecimal17f.one();
        factor18 = Decimal18f.ONE;
        mutableFactor18 = MutableDecimal18f.one();
    }

    @Benchmark
    public Decimal0f square() {
        return multipliable.square();
    }

    @Benchmark
    public Decimal0f byDecimal0f() {
        return multipliable.by(factor0);
    }

    @Benchmark
    public Decimal1f byDecimal1f() {
        return multipliable.by(factor1);
    }

    @Benchmark
    public Decimal1f byMutableDecimal1f() {
        return multipliable.by(mutableFactor1);
    }

    @Benchmark
    public Decimal2f byDecimal2f() {
        return multipliable.by(factor2);
    }

    @Benchmark
    public Decimal2f byMutableDecimal2f() {
        return multipliable.by(mutableFactor2);
    }

    @Benchmark
    public Decimal3f byDecimal3f() {
        return multipliable.by(factor3);
    }

    @Benchmark
    public Decimal3f byMutableDecimal3f() {
        return multipliable.by(mutableFactor3);
    }

    @Benchmark
    public Decimal4f byDecimal4f() {
        return multipliable.by(factor4);
    }

    @Benchmark
    public Decimal4f byMutableDecimal4f() {
        return multipliable.by(mutableFactor4);
    }

    @Benchmark
    public Decimal5f byDecimal5f() {
        return multipliable.by(factor5);
    }

    @Benchmark
    public Decimal5f byMutableDecimal5f() {
        return multipliable.by(mutableFactor5);
    }

    @Benchmark
    public Decimal6f byDecimal6f() {
        return multipliable.by(factor6);
    }

    @Benchmark
    public Decimal6f byMutableDecimal6f() {
        return multipliable.by(mutableFactor6);
    }

    @Benchmark
    public Decimal7f byDecimal7f() {
        return multipliable.by(factor7);
    }

    @Benchmark
    public Decimal7f byMutableDecimal7f() {
        return multipliable.by(mutableFactor7);
    }

    @Benchmark
    public Decimal8f byDecimal8f() {
        return multipliable.by(factor8);
    }

    @Benchmark
    public Decimal8f byMutableDecimal8f() {
        return multipliable.by(mutableFactor8);
    }

    @Benchmark
    public Decimal9f byDecimal9f() {
        return multipliable.by(factor9);
    }

    @Benchmark
    public Decimal9f byMutableDecimal9f() {
        return multipliable.by(mutableFactor9);
    }

    @Benchmark
    public Decimal10f byDecimal10f() {
        return multipliable.by(factor10);
    }

    @Benchmark
    public Decimal10f byMutableDecimal10f() {
        return multipliable.by(mutableFactor10);
    }

    @Benchmark
    public Decimal11f byDecimal11f() {
        return multipliable.by(factor11);
    }

    @Benchmark
    public Decimal11f byMutableDecimal11f() {
        return multipliable.by(mutableFactor11);
    }

    @Benchmark
    public Decimal12f byDecimal12f() {
        return multipliable.by(factor12);
    }

    @Benchmark
    public Decimal12f byMutableDecimal12f() {
        return multipliable.by(mutableFactor12);
    }

    @Benchmark
    public Decimal13f byDecimal13f() {
        return multipliable.by(factor13);
    }

    @Benchmark
    public Decimal13f byMutableDecimal13f() {
        return multipliable.by(mutableFactor13);
    }

    @Benchmark
    public Decimal14f byDecimal14f() {
        return multipliable.by(factor14);
    }

    @Benchmark
    public Decimal14f byMutableDecimal14f() {
        return multipliable.by(mutableFactor14);
    }

    @Benchmark
    public Decimal15f byDecimal15f() {
        return multipliable.by(factor15);
    }

    @Benchmark
    public Decimal15f byMutableDecimal15f() {
        return multipliable.by(mutableFactor15);
    }

    @Benchmark
    public Decimal16f byDecimal16f() {
        return multipliable.by(factor16);
    }

    @Benchmark
    public Decimal16f byMutableDecimal16f() {
        return multipliable.by(mutableFactor16);
    }

    @Benchmark
    public Decimal17f byDecimal17f() {
        return multipliable.by(factor17);
    }

    @Benchmark
    public Decimal17f byMutableDecimal17f() {
        return multipliable.by(mutableFactor17);
    }

    @Benchmark
    public Decimal18f byDecimal18f() {
        return multipliable.by(factor18);
    }

    @Benchmark
    public Decimal18f byMutableDecimal18f() {
        return multipliable.by(mutableFactor18);
    }

    @Benchmark
    public int hashCodeBench() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean equalsBench() {
        return multipliable.equals(multipliable);
    }

    @Benchmark
    public String toStringBench() {
        return multipliable.toString();
    }
}
