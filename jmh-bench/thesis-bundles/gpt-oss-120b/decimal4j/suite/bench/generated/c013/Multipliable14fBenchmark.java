package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable14f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
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

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable14fBenchmark {

    private Multipliable14f multipliable;

    private Decimal0f dec0;
    private MutableDecimal0f mdec0;
    private Decimal1f dec1;
    private MutableDecimal1f mdec1;
    private Decimal2f dec2;
    private MutableDecimal2f mdec2;
    private Decimal3f dec3;
    private MutableDecimal3f mdec3;
    private Decimal4f dec4;
    private MutableDecimal4f mdec4;

    @Setup(Level.Trial)
    public void setUp() {
        Decimal14f base = Decimal14f.ONE;
        multipliable = new Multipliable14f(base);

        dec0 = Decimal0f.valueOf(1L);
        mdec0 = MutableDecimal0f.one();

        dec1 = Decimal1f.valueOf(1L);
        mdec1 = MutableDecimal1f.one();

        dec2 = Decimal2f.valueOf(1L);
        mdec2 = MutableDecimal2f.one();

        dec3 = Decimal3f.valueOf(1L);
        mdec3 = MutableDecimal3f.one();

        dec4 = Decimal4f.valueOf(1L);
        mdec4 = MutableDecimal4f.one();
    }

    @Benchmark
    public Decimal14f benchByDecimal0f() {
        return multipliable.by(dec0);
    }

    @Benchmark
    public Decimal14f benchByMutableDecimal0f() {
        return multipliable.by(mdec0);
    }

    @Benchmark
    public Decimal15f benchByDecimal1f() {
        return multipliable.by(dec1);
    }

    @Benchmark
    public Decimal15f benchByMutableDecimal1f() {
        return multipliable.by(mdec1);
    }

    @Benchmark
    public Decimal16f benchByDecimal2f() {
        return multipliable.by(dec2);
    }

    @Benchmark
    public Decimal16f benchByMutableDecimal2f() {
        return multipliable.by(mdec2);
    }

    @Benchmark
    public Decimal17f benchByDecimal3f() {
        return multipliable.by(dec3);
    }

    @Benchmark
    public Decimal17f benchByMutableDecimal3f() {
        return multipliable.by(mdec3);
    }

    @Benchmark
    public Decimal18f benchByDecimal4f() {
        return multipliable.by(dec4);
    }

    @Benchmark
    public Decimal18f benchByMutableDecimal4f() {
        return multipliable.by(mdec4);
    }
}
