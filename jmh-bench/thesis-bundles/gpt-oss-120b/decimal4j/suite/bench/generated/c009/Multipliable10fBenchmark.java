package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable10f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.immutable.Decimal8f;
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
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.mutable.MutableDecimal8f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable10fBenchmark {

    private Multipliable10f multipliable;

    private Decimal0f decimal0f;
    private MutableDecimal0f mutableDecimal0f;

    private Decimal1f decimal1f;
    private MutableDecimal1f mutableDecimal1f;

    private Decimal2f decimal2f;
    private MutableDecimal2f mutableDecimal2f;

    private Decimal3f decimal3f;
    private MutableDecimal3f mutableDecimal3f;

    private Decimal4f decimal4f;
    private MutableDecimal4f mutableDecimal4f;

    private Decimal5f decimal5f;
    private MutableDecimal5f mutableDecimal5f;

    private Decimal6f decimal6f;
    private MutableDecimal6f mutableDecimal6f;

    private Decimal7f decimal7f;
    private MutableDecimal7f mutableDecimal7f;

    private Decimal8f decimal8f;
    private MutableDecimal8f mutableDecimal8f;

    @Setup(Level.Trial)
    public void setUp() {
        // Base value for the Multipliable10f (use ONE to avoid overflow)
        Decimal10f base = Decimal10f.ONE;
        multipliable = new Multipliable10f(base);

        decimal0f = Decimal0f.ONE;
        mutableDecimal0f = MutableDecimal0f.one();

        decimal1f = Decimal1f.ONE;
        mutableDecimal1f = MutableDecimal1f.one();

        decimal2f = Decimal2f.ONE;
        mutableDecimal2f = MutableDecimal2f.one();

        decimal3f = Decimal3f.ONE;
        mutableDecimal3f = MutableDecimal3f.one();

        decimal4f = Decimal4f.ONE;
        mutableDecimal4f = MutableDecimal4f.one();

        decimal5f = Decimal5f.ONE;
        mutableDecimal5f = MutableDecimal5f.one();

        decimal6f = Decimal6f.ONE;
        mutableDecimal6f = MutableDecimal6f.one();

        decimal7f = Decimal7f.ONE;
        mutableDecimal7f = MutableDecimal7f.one();

        decimal8f = Decimal8f.ONE;
        mutableDecimal8f = MutableDecimal8f.one();
    }

    @Benchmark
    public Decimal10f multiplyByDecimal0f() {
        return multipliable.by(decimal0f);
    }

    @Benchmark
    public Decimal10f multiplyByMutableDecimal0f() {
        return multipliable.by(mutableDecimal0f);
    }

    @Benchmark
    public Decimal11f multiplyByDecimal1f() {
        return multipliable.by(decimal1f);
    }

    @Benchmark
    public Decimal11f multiplyByMutableDecimal1f() {
        return multipliable.by(mutableDecimal1f);
    }

    @Benchmark
    public Decimal12f multiplyByDecimal2f() {
        return multipliable.by(decimal2f);
    }

    @Benchmark
    public Decimal12f multiplyByMutableDecimal2f() {
        return multipliable.by(mutableDecimal2f);
    }

    @Benchmark
    public Decimal13f multiplyByDecimal3f() {
        return multipliable.by(decimal3f);
    }

    @Benchmark
    public Decimal13f multiplyByMutableDecimal3f() {
        return multipliable.by(mutableDecimal3f);
    }

    @Benchmark
    public Decimal14f multiplyByDecimal4f() {
        return multipliable.by(decimal4f);
    }

    @Benchmark
    public Decimal14f multiplyByMutableDecimal4f() {
        return multipliable.by(mutableDecimal4f);
    }

    @Benchmark
    public Decimal15f multiplyByDecimal5f() {
        return multipliable.by(decimal5f);
    }

    @Benchmark
    public Decimal15f multiplyByMutableDecimal5f() {
        return multipliable.by(mutableDecimal5f);
    }

    @Benchmark
    public Decimal16f multiplyByDecimal6f() {
        return multipliable.by(decimal6f);
    }

    @Benchmark
    public Decimal16f multiplyByMutableDecimal6f() {
        return multipliable.by(mutableDecimal6f);
    }

    @Benchmark
    public Decimal17f multiplyByDecimal7f() {
        return multipliable.by(decimal7f);
    }

    @Benchmark
    public Decimal17f multiplyByMutableDecimal7f() {
        return multipliable.by(mutableDecimal7f);
    }

    @Benchmark
    public Decimal18f multiplyByDecimal8f() {
        return multipliable.by(decimal8f);
    }

    @Benchmark
    public Decimal18f multiplyByMutableDecimal8f() {
        return multipliable.by(mutableDecimal8f);
    }
}
