package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable15f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.scale.Scale15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable15fBenchmark {

    private Decimal15f value;
    private Multipliable15f multipliable;
    private Multipliable15f equalMultipliable;

    private Decimal0f factor0;
    private Decimal1f factor1;
    private Decimal2f factor2;
    private Decimal3f factor3;

    private MutableDecimal0f mutableFactor0;
    private MutableDecimal1f mutableFactor1;
    private MutableDecimal2f mutableFactor2;
    private MutableDecimal3f mutableFactor3;

    @Setup(Level.Trial)
    public void setUp() {
        value = Decimal15f.valueOf(1L);
        multipliable = new Multipliable15f(value);
        equalMultipliable = new Multipliable15f(Decimal15f.valueOf(1L));

        factor0 = Decimal0f.valueOf(2L);
        factor1 = Decimal1f.valueOf(2L);
        factor2 = Decimal2f.valueOf(2L);
        factor3 = Decimal3f.valueOf(2L);

        mutableFactor0 = new MutableDecimal0f(2L);
        mutableFactor1 = new MutableDecimal1f(2L);
        mutableFactor2 = new MutableDecimal2f(2L);
        mutableFactor3 = new MutableDecimal3f(2L);
    }

    @Benchmark
    public Multipliable15f create() {
        return new Multipliable15f(value);
    }

    @Benchmark
    public Decimal<Scale15f> getValue() {
        return multipliable.getValue();
    }

    @Benchmark
    public Decimal15f multiplyByImmutableScale0() {
        return multipliable.by(factor0);
    }

    @Benchmark
    public Decimal15f multiplyByMutableScale0() {
        return multipliable.by(mutableFactor0);
    }

    @Benchmark
    public Decimal16f multiplyByImmutableScale1() {
        return multipliable.by(factor1);
    }

    @Benchmark
    public Decimal16f multiplyByMutableScale1() {
        return multipliable.by(mutableFactor1);
    }

    @Benchmark
    public Decimal17f multiplyByImmutableScale2() {
        return multipliable.by(factor2);
    }

    @Benchmark
    public Decimal17f multiplyByMutableScale2() {
        return multipliable.by(mutableFactor2);
    }

    @Benchmark
    public Decimal18f multiplyByImmutableScale3() {
        return multipliable.by(factor3);
    }

    @Benchmark
    public Decimal18f multiplyByMutableScale3() {
        return multipliable.by(mutableFactor3);
    }

    @Benchmark
    public int hashCodeValue() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean equalsValue() {
        return multipliable.equals(equalMultipliable);
    }

    @Benchmark
    public String stringValue() {
        return multipliable.toString();
    }
}
