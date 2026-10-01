package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable7f;
import org.decimal4j.api.Decimal;
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
import org.decimal4j.scale.Scale7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable7fBenchmark {

    private Multipliable7f multipliable;
    private Multipliable7f otherMultipliable;

    private Decimal7f dec7;
    private Decimal0f dec0;
    private Decimal1f dec1;
    private Decimal2f dec2;
    private Decimal3f dec3;
    private Decimal4f dec4;
    private Decimal5f dec5;
    private Decimal6f dec6;
    private Decimal8f dec8;
    private Decimal9f dec9;
    private Decimal10f dec10;
    private Decimal11f dec11;

    private MutableDecimal0f mdec0;
    private MutableDecimal1f mdec1;
    private MutableDecimal2f mdec2;
    private MutableDecimal3f mdec3;
    private MutableDecimal4f mdec4;
    private MutableDecimal5f mdec5;
    private MutableDecimal6f mdec6; // placeholder, will be corrected below
    private MutableDecimal8f mdec8;
    private MutableDecimal9f mdec9;
    private MutableDecimal10f mdec10;
    private MutableDecimal11f mdec11;

    @Setup(Level.Trial)
    public void setUp() {
        dec7 = Decimal7f.ONE;
        multipliable = new Multipliable7f(dec7);
        otherMultipliable = new Multipliable7f(Decimal7f.ONE);

        dec0 = Decimal0f.ONE;
        dec1 = Decimal1f.ONE;
        dec2 = Decimal2f.ONE;
        dec3 = Decimal3f.ONE;
        dec4 = Decimal4f.ONE;
        dec5 = Decimal5f.ONE;
        dec6 = Decimal6f.ONE;
        dec8 = Decimal8f.ONE;
        dec9 = Decimal9f.ONE;
        dec10 = Decimal10f.ONE;
        dec11 = Decimal11f.ONE;

        mdec0 = MutableDecimal0f.one();
        mdec1 = MutableDecimal1f.one();
        mdec2 = MutableDecimal2f.one();
        mdec3 = MutableDecimal3f.one();
        mdec4 = MutableDecimal4f.one();
        mdec5 = MutableDecimal5f.one();
        mdec6 = MutableDecimal6f.one();
        mdec8 = MutableDecimal8f.one();
        mdec9 = MutableDecimal9f.one();
        mdec10 = MutableDecimal10f.one();
        mdec11 = MutableDecimal11f.one();
    }

    @Benchmark
    public Decimal<Scale7f> benchGetValue() {
        return multipliable.getValue();
    }

    @Benchmark
    public Decimal14f benchSquare() {
        return multipliable.square();
    }

    @Benchmark
    public Decimal14f benchByDecimal7f() {
        return multipliable.by(dec7);
    }

    @Benchmark
    public Decimal14f benchByDecimal7fMultipliable() {
        return multipliable.by(multipliable.getValue());
    }

    @Benchmark
    public Decimal7f benchByDecimal0f() {
        return multipliable.by(dec0);
    }

    @Benchmark
    public Decimal7f benchByMutableDecimal0f() {
        return multipliable.by(mdec0);
    }

    @Benchmark
    public Decimal8f benchByDecimal1f() {
        return multipliable.by(dec1);
    }

    @Benchmark
    public Decimal8f benchByMutableDecimal1f() {
        return multipliable.by(mdec1);
    }

    @Benchmark
    public Decimal9f benchByDecimal2f() {
        return multipliable.by(dec2);
    }

    @Benchmark
    public Decimal9f benchByMutableDecimal2f() {
        return multipliable.by(mdec2);
    }

    @Benchmark
    public Decimal10f benchByDecimal3f() {
        return multipliable.by(dec3);
    }

    @Benchmark
    public Decimal10f benchByMutableDecimal3f() {
        return multipliable.by(mdec3);
    }

    @Benchmark
    public Decimal11f benchByDecimal4f() {
        return multipliable.by(dec4);
    }

    @Benchmark
    public Decimal11f benchByMutableDecimal4f() {
        return multipliable.by(mdec4);
    }

    @Benchmark
    public Decimal12f benchByDecimal5f() {
        return multipliable.by(dec5);
    }

    @Benchmark
    public Decimal12f benchByMutableDecimal5f() {
        return multipliable.by(mdec5);
    }

    @Benchmark
    public Decimal13f benchByDecimal6f() {
        return multipliable.by(dec6);
    }

    @Benchmark
    public Decimal13f benchByMutableDecimal6f() {
        return multipliable.by(mdec6);
    }

    @Benchmark
    public Decimal15f benchByDecimal8f() {
        return multipliable.by(dec8);
    }

    @Benchmark
    public Decimal15f benchByMutableDecimal8f() {
        return multipliable.by(mdec8);
    }

    @Benchmark
    public Decimal16f benchByDecimal9f() {
        return multipliable.by(dec9);
    }

    @Benchmark
    public Decimal16f benchByMutableDecimal9f() {
        return multipliable.by(mdec9);
    }

    @Benchmark
    public Decimal17f benchByDecimal10f() {
        return multipliable.by(dec10);
    }

    @Benchmark
    public Decimal17f benchByMutableDecimal10f() {
        return multipliable.by(mdec10);
    }

    @Benchmark
    public Decimal18f benchByDecimal11f() {
        return multipliable.by(dec11);
    }

    @Benchmark
    public Decimal18f benchByMutableDecimal11f() {
        return multipliable.by(mdec11);
    }

    @Benchmark
    public int benchHashCode() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean benchEquals() {
        return multipliable.equals(otherMultipliable);
    }

    @Benchmark
    public String benchToString() {
        return multipliable.toString();
    }
}
