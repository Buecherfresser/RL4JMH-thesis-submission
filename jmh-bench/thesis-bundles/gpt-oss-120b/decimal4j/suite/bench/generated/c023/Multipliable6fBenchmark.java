package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable6f;
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
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.mutable.MutableDecimal12f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable6fBenchmark {

    private Multipliable6f multipliable;
    private Multipliable6f sameMultipliable;

    private Decimal6f factor6f;
    private Decimal0f factor0f;
    private MutableDecimal0f mutableFactor0f;
    private Decimal1f factor1f;
    private MutableDecimal1f mutableFactor1f;
    private Decimal2f factor2f;
    private MutableDecimal2f mutableFactor2f;
    private Decimal3f factor3f;
    private MutableDecimal3f mutableFactor3f;
    private Decimal4f factor4f;
    private MutableDecimal4f mutableFactor4f;
    private Decimal5f factor5f;
    private MutableDecimal5f mutableFactor5f;
    private Decimal7f factor7f;
    private MutableDecimal7f mutableFactor7f;
    private Decimal8f factor8f;
    private MutableDecimal8f mutableFactor8f;
    private Decimal9f factor9f;
    private MutableDecimal9f mutableFactor9f;
    private Decimal10f factor10f;
    private MutableDecimal10f mutableFactor10f;
    private Decimal11f factor11f;
    private MutableDecimal11f mutableFactor11f;
    private Decimal12f factor12f;
    private MutableDecimal12f mutableFactor12f;

    @Setup(Level.Trial)
    public void setUp() {
        // Base value for the Multipliable6f
        Decimal6f base = Decimal6f.ONE;
        this.multipliable = new Multipliable6f(base);
        this.sameMultipliable = new Multipliable6f(base);

        // Factors for each overload – using ONE to avoid overflow
        this.factor6f = Decimal6f.ONE;
        this.factor0f = Decimal0f.ONE;
        this.mutableFactor0f = MutableDecimal0f.one();
        this.factor1f = Decimal1f.ONE;
        this.mutableFactor1f = MutableDecimal1f.one();
        this.factor2f = Decimal2f.ONE;
        this.mutableFactor2f = MutableDecimal2f.one();
        this.factor3f = Decimal3f.ONE;
        this.mutableFactor3f = MutableDecimal3f.one();
        this.factor4f = Decimal4f.ONE;
        this.mutableFactor4f = MutableDecimal4f.one();
        this.factor5f = Decimal5f.ONE;
        this.mutableFactor5f = MutableDecimal5f.one();
        this.factor7f = Decimal7f.ONE;
        this.mutableFactor7f = MutableDecimal7f.one();
        this.factor8f = Decimal8f.ONE;
        this.mutableFactor8f = MutableDecimal8f.one();
        this.factor9f = Decimal9f.ONE;
        this.mutableFactor9f = MutableDecimal9f.one();
        this.factor10f = Decimal10f.ONE;
        this.mutableFactor10f = MutableDecimal10f.one();
        this.factor11f = Decimal11f.ONE;
        this.mutableFactor11f = MutableDecimal11f.one();
        this.factor12f = Decimal12f.ONE;
        this.mutableFactor12f = MutableDecimal12f.one();
    }

    @Benchmark
    public Decimal12f benchmarkSquare() {
        return multipliable.square();
    }

    @Benchmark
    public Decimal12f benchmarkByDecimal6f() {
        return multipliable.by(factor6f);
    }

    @Benchmark
    public Decimal6f benchmarkByDecimal0f() {
        return multipliable.by(factor0f);
    }

    @Benchmark
    public Decimal6f benchmarkByMutableDecimal0f() {
        return multipliable.by(mutableFactor0f);
    }

    @Benchmark
    public Decimal7f benchmarkByDecimal1f() {
        return multipliable.by(factor1f);
    }

    @Benchmark
    public Decimal7f benchmarkByMutableDecimal1f() {
        return multipliable.by(mutableFactor1f);
    }

    @Benchmark
    public Decimal8f benchmarkByDecimal2f() {
        return multipliable.by(factor2f);
    }

    @Benchmark
    public Decimal8f benchmarkByMutableDecimal2f() {
        return multipliable.by(mutableFactor2f);
    }

    @Benchmark
    public Decimal9f benchmarkByDecimal3f() {
        return multipliable.by(factor3f);
    }

    @Benchmark
    public Decimal9f benchmarkByMutableDecimal3f() {
        return multipliable.by(mutableFactor3f);
    }

    @Benchmark
    public Decimal10f benchmarkByDecimal4f() {
        return multipliable.by(factor4f);
    }

    @Benchmark
    public Decimal10f benchmarkByMutableDecimal4f() {
        return multipliable.by(mutableFactor4f);
    }

    @Benchmark
    public Decimal11f benchmarkByDecimal5f() {
        return multipliable.by(factor5f);
    }

    @Benchmark
    public Decimal11f benchmarkByMutableDecimal5f() {
        return multipliable.by(mutableFactor5f);
    }

    @Benchmark
    public Decimal13f benchmarkByDecimal7f() {
        return multipliable.by(factor7f);
    }

    @Benchmark
    public Decimal13f benchmarkByMutableDecimal7f() {
        return multipliable.by(mutableFactor7f);
    }

    @Benchmark
    public Decimal14f benchmarkByDecimal8f() {
        return multipliable.by(factor8f);
    }

    @Benchmark
    public Decimal14f benchmarkByMutableDecimal8f() {
        return multipliable.by(mutableFactor8f);
    }

    @Benchmark
    public Decimal15f benchmarkByDecimal9f() {
        return multipliable.by(factor9f);
    }

    @Benchmark
    public Decimal15f benchmarkByMutableDecimal9f() {
        return multipliable.by(mutableFactor9f);
    }

    @Benchmark
    public Decimal16f benchmarkByDecimal10f() {
        return multipliable.by(factor10f);
    }

    @Benchmark
    public Decimal16f benchmarkByMutableDecimal10f() {
        return multipliable.by(mutableFactor10f);
    }

    @Benchmark
    public Decimal17f benchmarkByDecimal11f() {
        return multipliable.by(factor11f);
    }

    @Benchmark
    public Decimal17f benchmarkByMutableDecimal11f() {
        return multipliable.by(mutableFactor11f);
    }

    @Benchmark
    public Decimal18f benchmarkByDecimal12f() {
        return multipliable.by(factor12f);
    }

    @Benchmark
    public Decimal18f benchmarkByMutableDecimal12f() {
        return multipliable.by(mutableFactor12f);
    }

    @Benchmark
    public int benchmarkHashCode() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean benchmarkEquals() {
        return multipliable.equals(sameMultipliable);
    }

    @Benchmark
    public String benchmarkToString() {
        return multipliable.toString();
    }
}
