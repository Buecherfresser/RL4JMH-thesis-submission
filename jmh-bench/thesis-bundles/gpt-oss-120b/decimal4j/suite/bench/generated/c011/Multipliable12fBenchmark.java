package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable12f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
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
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale12f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable12fBenchmark {

    private Multipliable12f multipliable;
    private Multipliable12f equalMultipliable;
    private Multipliable12f differentMultipliable;

    private Decimal0f d0;
    private MutableDecimal0f md0;
    private Decimal1f d1;
    private MutableDecimal1f md1;
    private Decimal2f d2;
    private MutableDecimal2f md2;
    private Decimal3f d3;
    private MutableDecimal3f md3;
    private Decimal4f d4;
    private MutableDecimal4f md4;
    private Decimal5f d5;
    private MutableDecimal5f md5;
    private Decimal6f d6;
    private MutableDecimal6f md6;

    @Setup(Level.Trial)
    public void setUp() {
        Decimal12f base = Decimal12f.ONE;
        multipliable = new Multipliable12f(base);
        equalMultipliable = new Multipliable12f(base);
        differentMultipliable = new Multipliable12f(Decimal12f.TEN);

        d0 = Decimal0f.ONE;
        md0 = MutableDecimal0f.one();
        d1 = Decimal1f.ONE;
        md1 = MutableDecimal1f.one();
        d2 = Decimal2f.ONE;
        md2 = MutableDecimal2f.one();
        d3 = Decimal3f.ONE;
        md3 = MutableDecimal3f.one();
        d4 = Decimal4f.ONE;
        md4 = MutableDecimal4f.one();
        d5 = Decimal5f.ONE;
        md5 = MutableDecimal5f.one();
        d6 = Decimal6f.ONE;
        md6 = MutableDecimal6f.one();
    }

    @Benchmark
    public Decimal12f benchByDecimal0f() {
        return multipliable.by(d0);
    }

    @Benchmark
    public Decimal12f benchByMutableDecimal0f() {
        return multipliable.by(md0);
    }

    @Benchmark
    public Decimal13f benchByDecimal1f() {
        return multipliable.by(d1);
    }

    @Benchmark
    public Decimal13f benchByMutableDecimal1f() {
        return multipliable.by(md1);
    }

    @Benchmark
    public Decimal14f benchByDecimal2f() {
        return multipliable.by(d2);
    }

    @Benchmark
    public Decimal14f benchByMutableDecimal2f() {
        return multipliable.by(md2);
    }

    @Benchmark
    public Decimal15f benchByDecimal3f() {
        return multipliable.by(d3);
    }

    @Benchmark
    public Decimal15f benchByMutableDecimal3f() {
        return multipliable.by(md3);
    }

    @Benchmark
    public Decimal16f benchByDecimal4f() {
        return multipliable.by(d4);
    }

    @Benchmark
    public Decimal16f benchByMutableDecimal4f() {
        return multipliable.by(md4);
    }

    @Benchmark
    public Decimal17f benchByDecimal5f() {
        return multipliable.by(d5);
    }

    @Benchmark
    public Decimal17f benchByMutableDecimal5f() {
        return multipliable.by(md5);
    }

    @Benchmark
    public Decimal18f benchByDecimal6f() {
        return multipliable.by(d6);
    }

    @Benchmark
    public Decimal18f benchByMutableDecimal6f() {
        return multipliable.by(md6);
    }

    @Benchmark
    public Decimal<Scale12f> benchGetValue() {
        return multipliable.getValue();
    }

    @Benchmark
    public int benchHashCode() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean benchEqualsSame() {
        return multipliable.equals(multipliable);
    }

    @Benchmark
    public boolean benchEqualsEqual() {
        return multipliable.equals(equalMultipliable);
    }

    @Benchmark
    public boolean benchEqualsDifferent() {
        return multipliable.equals(differentMultipliable);
    }

    @Benchmark
    public String benchToString() {
        return multipliable.toString();
    }
}
