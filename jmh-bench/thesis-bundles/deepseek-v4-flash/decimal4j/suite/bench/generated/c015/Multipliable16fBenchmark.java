package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.exact.Multipliable16f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable16fBenchmark {

    private Multipliable16f multipliable;
    private Multipliable16f sameMultipliable; // for equals
    private Multipliable16f diffMultipliable;  // for equals (different value)

    private Decimal0f immFactor0;
    private Decimal1f immFactor1;
    private Decimal2f immFactor2;
    private MutableDecimal0f mutFactor0;
    private MutableDecimal1f mutFactor1;
    private MutableDecimal2f mutFactor2;

    @Setup(Level.Trial)
    public void setUp() {
        Decimal16f value = Decimal16f.valueOf("1.2345678901234567");
        multipliable = value.multiplyExact();
        sameMultipliable = Decimal16f.valueOf("1.2345678901234567").multiplyExact();
        diffMultipliable = Decimal16f.valueOf("9.8765432109876543").multiplyExact();

        immFactor0 = Decimal0f.valueOf(3);
        immFactor1 = Decimal1f.valueOf("2.5");
        immFactor2 = Decimal2f.valueOf("1.75");

        mutFactor0 = new MutableDecimal0f(immFactor0);
        mutFactor1 = new MutableDecimal1f(immFactor1);
        mutFactor2 = new MutableDecimal2f(immFactor2);
    }

    @Benchmark
    public Decimal16f byImmutable0f() {
        return multipliable.by(immFactor0);
    }

    @Benchmark
    public Decimal17f byImmutable1f() {
        return multipliable.by(immFactor1);
    }

    @Benchmark
    public Decimal18f byImmutable2f() {
        return multipliable.by(immFactor2);
    }

    @Benchmark
    public Decimal16f byMutable0f() {
        return multipliable.by(mutFactor0);
    }

    @Benchmark
    public Decimal17f byMutable1f() {
        return multipliable.by(mutFactor1);
    }

    @Benchmark
    public Decimal18f byMutable2f() {
        return multipliable.by(mutFactor2);
    }

    @Benchmark
    public org.decimal4j.api.Decimal<org.decimal4j.scale.Scale16f> getValue() {
        return multipliable.getValue();
    }

    @Benchmark
    public boolean equalsSame() {
        return multipliable.equals(sameMultipliable);
    }

    @Benchmark
    public boolean equalsDifferent() {
        return multipliable.equals(diffMultipliable);
    }

    @Benchmark
    public int hashCodeBench() {
        return multipliable.hashCode();
    }

    @Benchmark
    public String toStringBench() {
        return multipliable.toString();
    }
}
