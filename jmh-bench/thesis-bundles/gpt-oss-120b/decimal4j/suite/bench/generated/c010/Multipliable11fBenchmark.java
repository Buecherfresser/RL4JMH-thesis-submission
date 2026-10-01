package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable11f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal7f;
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
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.mutable.MutableDecimal7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable11fBenchmark {

    private Multipliable11f multipliable;
    private Multipliable11f sameMultipliable;

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
    private Decimal6f dec6;
    private MutableDecimal6f mut6;
    private Decimal7f dec7;
    private MutableDecimal7f mut7;

    @Setup(Level.Trial)
    public void setup() {
        // Base value for the Multipliable (scale 11)
        Decimal11f base = Decimal11f.ONE;
        this.multipliable = new Multipliable11f(base);
        this.sameMultipliable = new Multipliable11f(base);

        // Factors for each scale – using the constant ONE for simplicity
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
        this.dec6 = Decimal6f.ONE;
        this.mut6 = MutableDecimal6f.one();
        this.dec7 = Decimal7f.ONE;
        this.mut7 = MutableDecimal7f.one();
    }

    @Benchmark
    public Decimal11f multiplyByDecimal0f() {
        return multipliable.by(dec0);
    }

    @Benchmark
    public Decimal11f multiplyByMutableDecimal0f() {
        return multipliable.by(mut0);
    }

    @Benchmark
    public Decimal12f multiplyByDecimal1f() {
        return multipliable.by(dec1);
    }

    @Benchmark
    public Decimal12f multiplyByMutableDecimal1f() {
        return multipliable.by(mut1);
    }

    @Benchmark
    public Decimal13f multiplyByDecimal2f() {
        return multipliable.by(dec2);
    }

    @Benchmark
    public Decimal13f multiplyByMutableDecimal2f() {
        return multipliable.by(mut2);
    }

    @Benchmark
    public Decimal14f multiplyByDecimal3f() {
        return multipliable.by(dec3);
    }

    @Benchmark
    public Decimal14f multiplyByMutableDecimal3f() {
        return multipliable.by(mut3);
    }

    @Benchmark
    public Decimal15f multiplyByDecimal4f() {
        return multipliable.by(dec4);
    }

    @Benchmark
    public Decimal15f multiplyByMutableDecimal4f() {
        return multipliable.by(mut4);
    }

    @Benchmark
    public Decimal16f multiplyByDecimal5f() {
        return multipliable.by(dec5);
    }

    @Benchmark
    public Decimal16f multiplyByMutableDecimal5f() {
        return multipliable.by(mut5);
    }

    @Benchmark
    public Decimal17f multiplyByDecimal6f() {
        return multipliable.by(dec6);
    }

    @Benchmark
    public Decimal17f multiplyByMutableDecimal6f() {
        return multipliable.by(mut6);
    }

    @Benchmark
    public Decimal18f multiplyByDecimal7f() {
        return multipliable.by(dec7);
    }

    @Benchmark
    public Decimal18f multiplyByMutableDecimal7f() {
        return multipliable.by(mut7);
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean equalsBenchmark() {
        return multipliable.equals(sameMultipliable);
    }

    @Benchmark
    public String toStringBenchmark() {
        return multipliable.toString();
    }
}
