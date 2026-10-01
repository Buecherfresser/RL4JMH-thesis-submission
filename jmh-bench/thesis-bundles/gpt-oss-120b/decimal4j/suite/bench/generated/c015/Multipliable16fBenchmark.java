package bench.generated.c015;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
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
    private Decimal0f dec0;
    private MutableDecimal0f mut0;
    private Decimal1f dec1;
    private MutableDecimal1f mut1;
    private Decimal2f dec2;
    private MutableDecimal2f mut2;

    @Setup
    public void setup() {
        // Base value for the Multipliable (use ONE to avoid overflow)
        Decimal16f base = Decimal16f.ONE;
        this.multipliable = new Multipliable16f(base);

        // Factors for each overload
        this.dec0 = Decimal0f.ONE;
        this.mut0 = MutableDecimal0f.one();
        this.dec1 = Decimal1f.ONE;
        this.mut1 = MutableDecimal1f.one();
        this.dec2 = Decimal2f.ONE;
        this.mut2 = MutableDecimal2f.one();
    }

    @Benchmark
    public Decimal16f multiplyByDecimal0f() {
        return multipliable.by(dec0);
    }

    @Benchmark
    public Decimal16f multiplyByMutableDecimal0f() {
        return multipliable.by(mut0);
    }

    @Benchmark
    public Decimal17f multiplyByDecimal1f() {
        return multipliable.by(dec1);
    }

    @Benchmark
    public Decimal17f multiplyByMutableDecimal1f() {
        return multipliable.by(mut1);
    }

    @Benchmark
    public Decimal18f multiplyByDecimal2f() {
        return multipliable.by(dec2);
    }

    @Benchmark
    public Decimal18f multiplyByMutableDecimal2f() {
        return multipliable.by(mut2);
    }
}
