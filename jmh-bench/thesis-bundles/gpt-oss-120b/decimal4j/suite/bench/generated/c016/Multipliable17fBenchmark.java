package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable17f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable17fBenchmark {

    private Multipliable17f multipliable;
    private Decimal0f decimal0Factor;
    private MutableDecimal0f mutable0Factor;
    private Decimal1f decimal1Factor;
    private MutableDecimal1f mutable1Factor;
    private Multipliable17f equalMultipliable;
    private Multipliable17f differentMultipliable;

    @Setup
    public void setup() {
        Decimal17f base = Decimal17f.valueOf(123456789L);
        multipliable = new Multipliable17f(base);
        decimal0Factor = Decimal0f.ONE;
        mutable0Factor = MutableDecimal0f.one();
        decimal1Factor = Decimal1f.ONE;
        mutable1Factor = MutableDecimal1f.one();
        equalMultipliable = new Multipliable17f(base);
        Decimal17f otherBase = Decimal17f.valueOf(987654321L);
        differentMultipliable = new Multipliable17f(otherBase);
    }

    @Benchmark
    public Decimal17f benchByDecimal0f() {
        return multipliable.by(decimal0Factor);
    }

    @Benchmark
    public Decimal17f benchByMutableDecimal0f() {
        return multipliable.by(mutable0Factor);
    }

    @Benchmark
    public Decimal18f benchByDecimal1f() {
        return multipliable.by(decimal1Factor);
    }

    @Benchmark
    public Decimal18f benchByMutableDecimal1f() {
        return multipliable.by(mutable1Factor);
    }

    @Benchmark
    public boolean benchEqualsSame() {
        return multipliable.equals(equalMultipliable);
    }

    @Benchmark
    public boolean benchEqualsDifferent() {
        return multipliable.equals(differentMultipliable);
    }

    @Benchmark
    public int benchHashCode() {
        return multipliable.hashCode();
    }

    @Benchmark
    public String benchToString() {
        return multipliable.toString();
    }
}
