package bench.generated.c012;

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
import org.decimal4j.exact.Multipliable13f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable13fBenchmark {

    private Multipliable13f multipliable;
    private Multipliable13f equalMultipliable;

    private Decimal0f dec0;
    private MutableDecimal0f mut0;
    private Decimal1f dec1;
    private MutableDecimal1f mut1;
    private Decimal2f dec2;
    private MutableDecimal2f mut2;
    private Decimal3f dec3;
    private MutableDecimal3f mut3;
    private Decimal4f dec4;
    private MutableDecimal4f mut4;
    private Decimal5f dec5;
    private MutableDecimal5f mut5;

    @Setup
    public void setup() {
        Decimal13f base = Decimal13f.ONE; // non‑zero base value
        this.multipliable = new Multipliable13f(base);
        this.equalMultipliable = new Multipliable13f(base);

        this.dec0 = Decimal0f.ONE;
        this.mut0 = MutableDecimal0f.one();
        this.dec1 = Decimal1f.ONE;
        this.mut1 = MutableDecimal1f.one();
        this.dec2 = Decimal2f.ONE;
        this.mut2 = MutableDecimal2f.one();
        this.dec3 = Decimal3f.ONE;
        this.mut3 = MutableDecimal3f.one();
        this.dec4 = Decimal4f.ONE;
        this.mut4 = MutableDecimal4f.one();
        this.dec5 = Decimal5f.ONE;
        this.mut5 = MutableDecimal5f.one();
    }

    @Benchmark
    public Decimal13f benchByDecimal0f() {
        return multipliable.by(dec0);
    }

    @Benchmark
    public Decimal13f benchByMutableDecimal0f() {
        return multipliable.by(mut0);
    }

    @Benchmark
    public Decimal14f benchByDecimal1f() {
        return multipliable.by(dec1);
    }

    @Benchmark
    public Decimal14f benchByMutableDecimal1f() {
        return multipliable.by(mut1);
    }

    @Benchmark
    public Decimal15f benchByDecimal2f() {
        return multipliable.by(dec2);
    }

    @Benchmark
    public Decimal15f benchByMutableDecimal2f() {
        return multipliable.by(mut2);
    }

    @Benchmark
    public Decimal16f benchByDecimal3f() {
        return multipliable.by(dec3);
    }

    @Benchmark
    public Decimal16f benchByMutableDecimal3f() {
        return multipliable.by(mut3);
    }

    @Benchmark
    public Decimal17f benchByDecimal4f() {
        return multipliable.by(dec4);
    }

    @Benchmark
    public Decimal17f benchByMutableDecimal4f() {
        return multipliable.by(mut4);
    }

    @Benchmark
    public Decimal18f benchByDecimal5f() {
        return multipliable.by(dec5);
    }

    @Benchmark
    public Decimal18f benchByMutableDecimal5f() {
        return multipliable.by(mut5);
    }

    @Benchmark
    public boolean benchEquals() {
        return multipliable.equals(equalMultipliable);
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
