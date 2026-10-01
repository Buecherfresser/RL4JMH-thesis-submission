package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable4f;
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

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable4fBenchmark {

    private Multipliable4f multipliable;

    private Decimal0f d0;
    private MutableDecimal0f md0;

    private Decimal1f d1;
    private MutableDecimal1f md1;

    private Decimal2f d2;
    private MutableDecimal2f md2;

    private Decimal3f d3;
    private MutableDecimal3f md3;

    private Decimal5f d5;
    private MutableDecimal5f md5;

    private Decimal6f d6;
    private MutableDecimal6f md6;

    private Decimal7f d7;
    private MutableDecimal7f md7;

    private Decimal8f d8;
    private MutableDecimal8f md8;

    private Decimal9f d9;
    private MutableDecimal9f md9;

    private Decimal10f d10;
    private MutableDecimal10f md10;

    private Decimal11f d11;
    private MutableDecimal11f md11;

    private Decimal12f d12;
    private MutableDecimal12f md12;

    private Decimal13f d13;
    private MutableDecimal13f md13;

    private Decimal14f d14;
    private MutableDecimal14f md14;

    @Setup(Level.Trial)
    public void setup() {
        Decimal4f base = Decimal4f.ONE;
        multipliable = new Multipliable4f(base);

        d0 = Decimal0f.ONE;
        md0 = MutableDecimal0f.one();

        d1 = Decimal1f.ONE;
        md1 = MutableDecimal1f.one();

        d2 = Decimal2f.ONE;
        md2 = MutableDecimal2f.one();

        d3 = Decimal3f.ONE;
        md3 = MutableDecimal3f.one();

        d5 = Decimal5f.ONE;
        md5 = MutableDecimal5f.one();

        d6 = Decimal6f.ONE;
        md6 = MutableDecimal6f.one();

        d7 = Decimal7f.ONE;
        md7 = MutableDecimal7f.one();

        d8 = Decimal8f.ONE;
        md8 = MutableDecimal8f.one();

        d9 = Decimal9f.ONE;
        md9 = MutableDecimal9f.one();

        d10 = Decimal10f.ONE;
        md10 = MutableDecimal10f.one();

        d11 = Decimal11f.ONE;
        md11 = MutableDecimal11f.one();

        d12 = Decimal12f.ONE;
        md12 = MutableDecimal12f.one();

        d13 = Decimal13f.ONE;
        md13 = MutableDecimal13f.one();

        d14 = Decimal14f.ONE;
        md14 = MutableDecimal14f.one();
    }

    @Benchmark
    public Decimal8f benchSquare() {
        return multipliable.square();
    }

    @Benchmark
    public Decimal8f benchByDecimal4f() {
        return multipliable.by(multipliable.getValue());
    }

    @Benchmark
    public Decimal4f benchByDecimal0f() {
        return multipliable.by(d0);
    }

    @Benchmark
    public Decimal4f benchByMutableDecimal0f() {
        return multipliable.by(md0);
    }

    @Benchmark
    public Decimal5f benchByDecimal1f() {
        return multipliable.by(d1);
    }

    @Benchmark
    public Decimal5f benchByMutableDecimal1f() {
        return multipliable.by(md1);
    }

    @Benchmark
    public Decimal6f benchByDecimal2f() {
        return multipliable.by(d2);
    }

    @Benchmark
    public Decimal6f benchByMutableDecimal2f() {
        return multipliable.by(md2);
    }

    @Benchmark
    public Decimal7f benchByDecimal3f() {
        return multipliable.by(d3);
    }

    @Benchmark
    public Decimal7f benchByMutableDecimal3f() {
        return multipliable.by(md3);
    }

    @Benchmark
    public Decimal9f benchByDecimal5f() {
        return multipliable.by(d5);
    }

    @Benchmark
    public Decimal9f benchByMutableDecimal5f() {
        return multipliable.by(md5);
    }

    @Benchmark
    public Decimal10f benchByDecimal6f() {
        return multipliable.by(d6);
    }

    @Benchmark
    public Decimal10f benchByMutableDecimal6f() {
        return multipliable.by(md6);
    }

    @Benchmark
    public Decimal11f benchByDecimal7f() {
        return multipliable.by(d7);
    }

    @Benchmark
    public Decimal11f benchByMutableDecimal7f() {
        return multipliable.by(md7);
    }

    @Benchmark
    public Decimal12f benchByDecimal8f() {
        return multipliable.by(d8);
    }

    @Benchmark
    public Decimal12f benchByMutableDecimal8f() {
        return multipliable.by(md8);
    }

    @Benchmark
    public Decimal13f benchByDecimal9f() {
        return multipliable.by(d9);
    }

    @Benchmark
    public Decimal13f benchByMutableDecimal9f() {
        return multipliable.by(md9);
    }

    @Benchmark
    public Decimal14f benchByDecimal10f() {
        return multipliable.by(d10);
    }

    @Benchmark
    public Decimal14f benchByMutableDecimal10f() {
        return multipliable.by(md10);
    }

    @Benchmark
    public Decimal15f benchByDecimal11f() {
        return multipliable.by(d11);
    }

    @Benchmark
    public Decimal15f benchByMutableDecimal11f() {
        return multipliable.by(md11);
    }

    @Benchmark
    public Decimal16f benchByDecimal12f() {
        return multipliable.by(d12);
    }

    @Benchmark
    public Decimal16f benchByMutableDecimal12f() {
        return multipliable.by(md12);
    }

    @Benchmark
    public Decimal17f benchByDecimal13f() {
        return multipliable.by(d13);
    }

    @Benchmark
    public Decimal17f benchByMutableDecimal13f() {
        return multipliable.by(md13);
    }

    @Benchmark
    public Decimal18f benchByDecimal14f() {
        return multipliable.by(d14);
    }

    @Benchmark
    public Decimal18f benchByMutableDecimal14f() {
        return multipliable.by(md14);
    }
}
