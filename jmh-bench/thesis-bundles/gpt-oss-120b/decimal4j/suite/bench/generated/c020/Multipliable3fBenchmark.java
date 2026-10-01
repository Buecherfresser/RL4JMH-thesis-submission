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

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable3fBenchmark {

    private Multipliable3f multipliable;
    private Multipliable3f sameMultipliable;

    private Decimal0f dec0;
    private MutableDecimal0f mDec0;
    private Decimal1f dec1;
    private MutableDecimal1f mDec1;
    private Decimal2f dec2;
    private MutableDecimal2f mDec2;
    private Decimal4f dec4;
    private MutableDecimal4f mDec4;
    private Decimal5f dec5;
    private MutableDecimal5f mDec5;
    private Decimal6f dec6;
    private MutableDecimal6f mDec6;
    private Decimal7f dec7;
    private MutableDecimal7f mDec7;
    private Decimal8f dec8;
    private MutableDecimal8f mDec8;
    private Decimal9f dec9;
    private MutableDecimal9f mDec9;
    private Decimal10f dec10;
    private MutableDecimal10f mDec10;
    private Decimal11f dec11;
    private MutableDecimal11f mDec11;
    private Decimal12f dec12;
    private MutableDecimal12f mDec12;
    private Decimal13f dec13;
    private MutableDecimal13f mDec13;
    private Decimal14f dec14;
    private MutableDecimal14f mDec14;
    private Decimal15f dec15;
    private MutableDecimal15f mDec15;

    private Decimal3f dec3;

    @Setup(Level.Trial)
    public void setup() {
        dec0 = Decimal0f.valueOf(1);
        mDec0 = MutableDecimal0f.one();
        dec1 = Decimal1f.valueOf(1);
        mDec1 = MutableDecimal1f.one();
        dec2 = Decimal2f.valueOf(1);
        mDec2 = MutableDecimal2f.one();
        dec4 = Decimal4f.valueOf(1);
        mDec4 = MutableDecimal4f.one();
        dec5 = Decimal5f.valueOf(1);
        mDec5 = MutableDecimal5f.one();
        dec6 = Decimal6f.valueOf(1);
        mDec6 = MutableDecimal6f.one();
        dec7 = Decimal7f.valueOf(1);
        mDec7 = MutableDecimal7f.one();
        dec8 = Decimal8f.valueOf(1);
        mDec8 = MutableDecimal8f.one();
        dec9 = Decimal9f.valueOf(1);
        mDec9 = MutableDecimal9f.one();
        dec10 = Decimal10f.valueOf(1);
        mDec10 = MutableDecimal10f.one();
        dec11 = Decimal11f.valueOf(1);
        mDec11 = MutableDecimal11f.one();
        dec12 = Decimal12f.valueOf(1);
        mDec12 = MutableDecimal12f.one();
        dec13 = Decimal13f.valueOf(1);
        mDec13 = MutableDecimal13f.one();
        dec14 = Decimal14f.valueOf(1);
        mDec14 = MutableDecimal14f.one();
        dec15 = Decimal15f.valueOf(1);
        mDec15 = MutableDecimal15f.one();

        dec3 = Decimal3f.ONE;

        multipliable = new Multipliable3f(Decimal3f.ONE);
        sameMultipliable = new Multipliable3f(Decimal3f.ONE);
    }

    @Benchmark
    public Decimal6f square() {
        return multipliable.square();
    }

    @Benchmark
    public Decimal6f byDecimal3f() {
        return multipliable.by(dec3);
    }

    @Benchmark
    public Decimal3f byDecimal0f() {
        return multipliable.by(dec0);
    }

    @Benchmark
    public Decimal3f byMutableDecimal0f() {
        return multipliable.by(mDec0);
    }

    @Benchmark
    public Decimal4f byDecimal1f() {
        return multipliable.by(dec1);
    }

    @Benchmark
    public Decimal4f byMutableDecimal1f() {
        return multipliable.by(mDec1);
    }

    @Benchmark
    public Decimal5f byDecimal2f() {
        return multipliable.by(dec2);
    }

    @Benchmark
    public Decimal5f byMutableDecimal2f() {
        return multipliable.by(mDec2);
    }

    @Benchmark
    public Decimal7f byDecimal4f() {
        return multipliable.by(dec4);
    }

    @Benchmark
    public Decimal7f byMutableDecimal4f() {
        return multipliable.by(mDec4);
    }

    @Benchmark
    public Decimal8f byDecimal5f() {
        return multipliable.by(dec5);
    }

    @Benchmark
    public Decimal8f byMutableDecimal5f() {
        return multipliable.by(mDec5);
    }

    @Benchmark
    public Decimal9f byDecimal6f() {
        return multipliable.by(dec6);
    }

    @Benchmark
    public Decimal9f byMutableDecimal6f() {
        return multipliable.by(mDec6);
    }

    @Benchmark
    public Decimal10f byDecimal7f() {
        return multipliable.by(dec7);
    }

    @Benchmark
    public Decimal10f byMutableDecimal7f() {
        return multipliable.by(mDec7);
    }

    @Benchmark
    public Decimal11f byDecimal8f() {
        return multipliable.by(dec8);
    }

    @Benchmark
    public Decimal11f byMutableDecimal8f() {
        return multipliable.by(mDec8);
    }

    @Benchmark
    public Decimal12f byDecimal9f() {
        return multipliable.by(dec9);
    }

    @Benchmark
    public Decimal12f byMutableDecimal9f() {
        return multipliable.by(mDec9);
    }

    @Benchmark
    public Decimal13f byDecimal10f() {
        return multipliable.by(dec10);
    }

    @Benchmark
    public Decimal13f byMutableDecimal10f() {
        return multipliable.by(mDec10);
    }

    @Benchmark
    public Decimal14f byDecimal11f() {
        return multipliable.by(dec11);
    }

    @Benchmark
    public Decimal14f byMutableDecimal11f() {
        return multipliable.by(mDec11);
    }

    @Benchmark
    public Decimal15f byDecimal12f() {
        return multipliable.by(dec12);
    }

    @Benchmark
    public Decimal15f byMutableDecimal12f() {
        return multipliable.by(mDec12);
    }

    @Benchmark
    public Decimal16f byDecimal13f() {
        return multipliable.by(dec13);
    }

    @Benchmark
    public Decimal16f byMutableDecimal13f() {
        return multipliable.by(mDec13);
    }

    @Benchmark
    public Decimal17f byDecimal14f() {
        return multipliable.by(dec14);
    }

    @Benchmark
    public Decimal17f byMutableDecimal14f() {
        return multipliable.by(mDec14);
    }

    @Benchmark
    public Decimal18f byDecimal15f() {
        return multipliable.by(dec15);
    }

    @Benchmark
    public Decimal18f byMutableDecimal15f() {
        return multipliable.by(mDec15);
    }

    @Benchmark
    public int hashCodeBench() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean equalsBench() {
        return multipliable.equals(sameMultipliable);
    }

    @Benchmark
    public String toStringBench() {
        return multipliable.toString();
    }
}
