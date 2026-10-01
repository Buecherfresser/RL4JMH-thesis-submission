package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.exact.Multipliable8f;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.*;
import org.decimal4j.mutable.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable8fBenchmark {

    private Multipliable8f multipliable;

    private Decimal8f baseValue;

    private Decimal0f factor0;
    private MutableDecimal0f mutableFactor0;

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

    private Decimal9f factor9;
    private MutableDecimal9f mutableFactor9;

    private Decimal10f factor10;
    private MutableDecimal10f mutableFactor10;

    @Setup
    public void setup() {
        // Base value for the Multipliable8f (scale 8)
        baseValue = Decimal8f.valueOf(12345678L);
        multipliable = new Multipliable8f(baseValue);

        // Factors for each overload
        factor0 = Decimal0f.valueOf(2L);
        mutableFactor0 = MutableDecimal0f.one();

        factor1 = Decimal1f.valueOf(3L);
        mutableFactor1 = MutableDecimal1f.one();

        factor2 = Decimal2f.valueOf(4L);
        mutableFactor2 = MutableDecimal2f.one();

        factor3 = Decimal3f.valueOf(5L);
        mutableFactor3 = MutableDecimal3f.one();

        factor4 = Decimal4f.valueOf(6L);
        mutableFactor4 = MutableDecimal4f.one();

        factor5 = Decimal5f.valueOf(7L);
        mutableFactor5 = MutableDecimal5f.one();

        factor6 = Decimal6f.valueOf(8L);
        mutableFactor6 = MutableDecimal6f.one();

        factor7 = Decimal7f.valueOf(9L);
        mutableFactor7 = MutableDecimal7f.one();

        factor9 = Decimal9f.valueOf(11L);
        mutableFactor9 = MutableDecimal9f.one();

        factor10 = Decimal10f.valueOf(12L);
        mutableFactor10 = MutableDecimal10f.one();
    }

    @Benchmark
    public Decimal16f benchmarkSquare() {
        return multipliable.square();
    }

    @Benchmark
    public Decimal16f benchmarkByDecimal8f() {
        return multipliable.by(baseValue);
    }

    @Benchmark
    public Decimal8f benchmarkByDecimal0f() {
        return multipliable.by(factor0);
    }

    @Benchmark
    public Decimal8f benchmarkByMutableDecimal0f() {
        return multipliable.by(mutableFactor0);
    }

    @Benchmark
    public Decimal9f benchmarkByDecimal1f() {
        return multipliable.by(factor1);
    }

    @Benchmark
    public Decimal9f benchmarkByMutableDecimal1f() {
        return multipliable.by(mutableFactor1);
    }

    @Benchmark
    public Decimal10f benchmarkByDecimal2f() {
        return multipliable.by(factor2);
    }

    @Benchmark
    public Decimal10f benchmarkByMutableDecimal2f() {
        return multipliable.by(mutableFactor2);
    }

    @Benchmark
    public Decimal11f benchmarkByDecimal3f() {
        return multipliable.by(factor3);
    }

    @Benchmark
    public Decimal11f benchmarkByMutableDecimal3f() {
        return multipliable.by(mutableFactor3);
    }

    @Benchmark
    public Decimal12f benchmarkByDecimal4f() {
        return multipliable.by(factor4);
    }

    @Benchmark
    public Decimal12f benchmarkByMutableDecimal4f() {
        return multipliable.by(mutableFactor4);
    }

    @Benchmark
    public Decimal13f benchmarkByDecimal5f() {
        return multipliable.by(factor5);
    }

    @Benchmark
    public Decimal13f benchmarkByMutableDecimal5f() {
        return multipliable.by(mutableFactor5);
    }

    @Benchmark
    public Decimal14f benchmarkByDecimal6f() {
        return multipliable.by(factor6);
    }

    @Benchmark
    public Decimal14f benchmarkByMutableDecimal6f() {
        return multipliable.by(mutableFactor6);
    }

    @Benchmark
    public Decimal15f benchmarkByDecimal7f() {
        return multipliable.by(factor7);
    }

    @Benchmark
    public Decimal15f benchmarkByMutableDecimal7f() {
        return multipliable.by(mutableFactor7);
    }

    @Benchmark
    public Decimal17f benchmarkByDecimal9f() {
        return multipliable.by(factor9);
    }

    @Benchmark
    public Decimal17f benchmarkByMutableDecimal9f() {
        return multipliable.by(mutableFactor9);
    }

    @Benchmark
    public Decimal18f benchmarkByDecimal10f() {
        return multipliable.by(factor10);
    }

    @Benchmark
    public Decimal18f benchmarkByMutableDecimal10f() {
        return multipliable.by(mutableFactor10);
    }
}
