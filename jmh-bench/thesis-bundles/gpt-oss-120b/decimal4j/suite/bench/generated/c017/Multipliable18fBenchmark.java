package bench.generated.c017;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable18f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.mutable.MutableDecimal0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable18fBenchmark {

    private Multipliable18f multipliable;
    private Decimal0f decimal0Factor;
    private MutableDecimal0f mutableDecimal0Factor;
    private Multipliable18f sameMultipliable;
    private Multipliable18f differentMultipliable;

    @Setup
    public void setup() {
        // Base value for the Multipliable18f (scale 18)
        Decimal18f base = Decimal18f.ONE;
        this.multipliable = new Multipliable18f(base);

        // Factors of scale 0
        this.decimal0Factor = Decimal0f.FIVE;
        this.mutableDecimal0Factor = new MutableDecimal0f(5L);

        // Objects for equals benchmarks
        this.sameMultipliable = new Multipliable18f(base);
        this.differentMultipliable = new Multipliable18f(Decimal18f.ZERO);
    }

    @Benchmark
    public Decimal18f multiplyByDecimal0f() {
        return multipliable.by(decimal0Factor);
    }

    @Benchmark
    public Decimal18f multiplyByMutableDecimal0f() {
        return multipliable.by(mutableDecimal0Factor);
    }

    @Benchmark
    public Decimal18f getValue() {
        return (Decimal18f) multipliable.getValue();
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean equalsSame() {
        return multipliable.equals(sameMultipliable);
    }

    @Benchmark
    public boolean equalsDifferent() {
        return multipliable.equals(differentMultipliable);
    }

    @Benchmark
    public String toStringBenchmark() {
        return multipliable.toString();
    }
}
