package bench.generated.c023;

import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable6f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.immutable.Decimal12f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.mutable.MutableDecimal12f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.scale.Scale6f;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable6fBenchmark {

    private Multipliable6f multipliable;
    private Multipliable6f other;
    private Decimal6f factor6;
    private Decimal0f factor0;
    private Decimal1f factor1;
    private Decimal2f factor2;
    private Decimal3f factor3;
    private Decimal4f factor4;
    private Decimal5f factor5;
    private Decimal7f factor7;
    private Decimal8f factor8;
    private Decimal9f factor9;
    private Decimal10f factor10;
    private Decimal11f factor11;
    private Decimal12f factor12;
    private MutableDecimal0f mutableFactor0;
    private MutableDecimal1f mutableFactor1;
    private MutableDecimal2f mutableFactor2;
    private MutableDecimal3f mutableFactor3;
    private MutableDecimal4f mutableFactor4;
    private MutableDecimal5f mutableFactor5;
    private MutableDecimal7f mutableFactor7;
    private MutableDecimal8f mutableFactor8;
    private MutableDecimal9f mutableFactor9;
    private MutableDecimal10f mutableFactor10;
    private MutableDecimal11f mutableFactor11;
    private MutableDecimal12f mutableFactor12;

    @Setup(Level.Trial)
    public void setup() {
        factor6 = Decimal6f.valueOf("2.5");
        multipliable = new Multipliable6f(factor6);
        other = new Multipliable6f(Decimal6f.valueOf("2.5"));
        factor0 = Decimal0f.valueOf("1.5");
        factor1 = Decimal1f.valueOf("1.5");
        factor2 = Decimal2f.valueOf("1.5");
        factor3 = Decimal3f.valueOf("1.5");
        factor4 = Decimal4f.valueOf("1.5");
        factor5 = Decimal5f.valueOf("1.5");
        factor7 = Decimal7f.valueOf("1.5");
        factor8 = Decimal8f.valueOf("1.5");
        factor9 = Decimal9f.valueOf("1.5");
        factor10 = Decimal10f.valueOf("1.5");
        factor11 = Decimal11f.valueOf("1.5");
        factor12 = Decimal12f.valueOf("1.5");
        mutableFactor0 = new MutableDecimal0f("1.5");
        mutableFactor1 = new MutableDecimal1f("1.5");
        mutableFactor2 = new MutableDecimal2f("1.5");
        mutableFactor3 = new MutableDecimal3f("1.5");
        mutableFactor4 = new MutableDecimal4f("1.5");
        mutableFactor5 = new MutableDecimal5f("1.5");
        mutableFactor7 = new MutableDecimal7f("1.5");
        mutableFactor8 = new MutableDecimal8f("1.5");
        mutableFactor9 = new MutableDecimal9f("1.5");
        mutableFactor10 = new MutableDecimal10f("1.5");
        mutableFactor11 = new MutableDecimal11f("1.5");
        mutableFactor12 = new MutableDecimal12f("1.5");
    }

    @Benchmark
    public Multipliable6f constructor() {
        return new Multipliable6f(factor6);
    }

    @Benchmark
    public Decimal<Scale6f> getValue() {
        return multipliable.getValue();
    }

    @Benchmark
    public Decimal12f square() {
        return multipliable.square();
    }

    @Benchmark
    public Decimal12f byDecimal6f() {
        return multipliable.by(factor6);
    }

    @Benchmark
    public Decimal6f byDecimal0f() {
        return multipliable.by(factor0);
    }

    @Benchmark
    public Decimal6f byMutableDecimal0f() {
        return multipliable.by(mutableFactor0);
    }

    @Benchmark
    public Decimal7f byDecimal1f() {
        return multipliable.by(factor1);
    }

    @Benchmark
    public Decimal7f byMutableDecimal1f() {
        return multipliable.by(mutableFactor1);
    }

    @Benchmark
    public Decimal8f byDecimal2f() {
        return multipliable.by(factor2);
    }

    @Benchmark
    public Decimal8f byMutableDecimal2f() {
        return multipliable.by(mutableFactor2);
    }

    @Benchmark
    public Decimal9f byDecimal3f() {
        return multipliable.by(factor3);
    }

    @Benchmark
    public Decimal9f byMutableDecimal3f() {
        return multipliable.by(mutableFactor3);
    }

    @Benchmark
    public Decimal10f byDecimal4f() {
        return multipliable.by(factor4);
    }

    @Benchmark
    public Decimal10f byMutableDecimal4f() {
        return multipliable.by(mutableFactor4);
    }

    @Benchmark
    public Decimal11f byDecimal5f() {
        return multipliable.by(factor5);
    }

    @Benchmark
    public Decimal11f byMutableDecimal5f() {
        return multipliable.by(mutableFactor5);
    }

    @Benchmark
    public Decimal13f byDecimal7f() {
        return multipliable.by(factor7);
    }

    @Benchmark
    public Decimal13f byMutableDecimal7f() {
        return multipliable.by(mutableFactor7);
    }

    @Benchmark
    public Decimal14f byDecimal8f() {
        return multipliable.by(factor8);
    }

    @Benchmark
    public Decimal14f byMutableDecimal8f() {
        return multipliable.by(mutableFactor8);
    }

    @Benchmark
    public Decimal15f byDecimal9f() {
        return multipliable.by(factor9);
    }

    @Benchmark
    public Decimal15f byMutableDecimal9f() {
        return multipliable.by(mutableFactor9);
    }

    @Benchmark
    public Decimal16f byDecimal10f() {
        return multipliable.by(factor10);
    }

    @Benchmark
    public Decimal16f byMutableDecimal10f() {
        return multipliable.by(mutableFactor10);
    }

    @Benchmark
    public Decimal17f byDecimal11f() {
        return multipliable.by(factor11);
    }

    @Benchmark
    public Decimal17f byMutableDecimal11f() {
        return multipliable.by(mutableFactor11);
    }

    @Benchmark
    public Decimal18f byDecimal12f() {
        return multipliable.by(factor12);
    }

    @Benchmark
    public Decimal18f byMutableDecimal12f() {
        return multipliable.by(mutableFactor12);
    }

    @Benchmark
    public boolean equalsBenchmark() {
        return multipliable.equals(other);
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return multipliable.hashCode();
    }

    @Benchmark
    public String toStringBenchmark() {
        return multipliable.toString();
    }
}
