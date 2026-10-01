package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable15f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable15fBenchmark {

    private Multipliable15f multipliable;
    private Decimal0f dec0;
    private MutableDecimal0f mdec0;
    private Decimal1f dec1;
    private MutableDecimal1f mdec1;
    private Decimal2f dec2;
    private MutableDecimal2f mdec2;
    private Decimal3f dec3;
    private MutableDecimal3f mdec3;
    private Multipliable15f equalMultipliable;

    @Setup
    public void setup() {
        Decimal15f base = Decimal15f.ONE;
        multipliable = new Multipliable15f(base);
        dec0 = Decimal0f.ONE;
        mdec0 = MutableDecimal0f.one();
        dec1 = Decimal1f.ONE;
        mdec1 = MutableDecimal1f.one();
        dec2 = Decimal2f.ONE;
        mdec2 = MutableDecimal2f.one();
        dec3 = Decimal3f.ONE;
        mdec3 = MutableDecimal3f.one();
        equalMultipliable = new Multipliable15f(base);
    }

    @Benchmark
    public Decimal15f byDecimal0f() {
        return multipliable.by(dec0);
    }

    @Benchmark
    public Decimal15f byMutableDecimal0f() {
        return multipliable.by(mdec0);
    }

    @Benchmark
    public Decimal16f byDecimal1f() {
        return multipliable.by(dec1);
    }

    @Benchmark
    public Decimal16f byMutableDecimal1f() {
        return multipliable.by(mdec1);
    }

    @Benchmark
    public Decimal17f byDecimal2f() {
        return multipliable.by(dec2);
    }

    @Benchmark
    public Decimal17f byMutableDecimal2f() {
        return multipliable.by(mdec2);
    }

    @Benchmark
    public Decimal18f byDecimal3f() {
        return multipliable.by(dec3);
    }

    @Benchmark
    public Decimal18f byMutableDecimal3f() {
        return multipliable.by(mdec3);
    }

    @Benchmark
    public boolean equalsBenchmark() {
        return multipliable.equals(equalMultipliable);
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
