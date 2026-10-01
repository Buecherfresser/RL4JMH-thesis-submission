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
    private Multipliable11f otherMultipliable; // same value for equals

    private Decimal0f dec0;
    private Decimal1f dec1;
    private Decimal2f dec2;
    private Decimal3f dec3;
    private Decimal4f dec4;
    private Decimal5f dec5;
    private Decimal6f dec6;
    private Decimal7f dec7;

    private MutableDecimal0f mut0;
    private MutableDecimal1f mut1;
    private MutableDecimal2f mut2;
    private MutableDecimal3f mut3;
    private MutableDecimal4f mut4;
    private MutableDecimal5f mut5;
    private MutableDecimal6f mut6;
    private MutableDecimal7f mut7;

    @Setup(Level.Trial)
    public void setup() {
        Decimal11f base = Decimal11f.valueOf("1.5");
        multipliable = new Multipliable11f(base);
        otherMultipliable = new Multipliable11f(base);

        dec0 = Decimal0f.valueOf(2);
        dec1 = Decimal1f.valueOf("2.0");
        dec2 = Decimal2f.valueOf("2.00");
        dec3 = Decimal3f.valueOf("2.000");
        dec4 = Decimal4f.valueOf("2.0000");
        dec5 = Decimal5f.valueOf("2.00000");
        dec6 = Decimal6f.valueOf("2.000000");
        dec7 = Decimal7f.valueOf("2.0000000");

        mut0 = new MutableDecimal0f(2);
        mut1 = new MutableDecimal1f("2.0");
        mut2 = new MutableDecimal2f("2.00");
        mut3 = new MutableDecimal3f("2.000");
        mut4 = new MutableDecimal4f("2.0000");
        mut5 = new MutableDecimal5f("2.00000");
        mut6 = new MutableDecimal6f("2.000000");
        mut7 = new MutableDecimal7f("2.0000000");
    }

    // --- by() with immutable factors ---

    @Benchmark
    public Decimal11f byDecimal0f() {
        return multipliable.by(dec0);
    }

    @Benchmark
    public Decimal12f byDecimal1f() {
        return multipliable.by(dec1);
    }

    @Benchmark
    public Decimal13f byDecimal2f() {
        return multipliable.by(dec2);
    }

    @Benchmark
    public Decimal14f byDecimal3f() {
        return multipliable.by(dec3);
    }

    @Benchmark
    public Decimal15f byDecimal4f() {
        return multipliable.by(dec4);
    }

    @Benchmark
    public Decimal16f byDecimal5f() {
        return multipliable.by(dec5);
    }

    @Benchmark
    public Decimal17f byDecimal6f() {
        return multipliable.by(dec6);
    }

    @Benchmark
    public Decimal18f byDecimal7f() {
        return multipliable.by(dec7);
    }

    // --- by() with mutable factors ---

    @Benchmark
    public Decimal11f byMutableDecimal0f() {
        return multipliable.by(mut0);
    }

    @Benchmark
    public Decimal12f byMutableDecimal1f() {
        return multipliable.by(mut1);
    }

    @Benchmark
    public Decimal13f byMutableDecimal2f() {
        return multipliable.by(mut2);
    }

    @Benchmark
    public Decimal14f byMutableDecimal3f() {
        return multipliable.by(mut3);
    }

    @Benchmark
    public Decimal15f byMutableDecimal4f() {
        return multipliable.by(mut4);
    }

    @Benchmark
    public Decimal16f byMutableDecimal5f() {
        return multipliable.by(mut5);
    }

    @Benchmark
    public Decimal17f byMutableDecimal6f() {
        return multipliable.by(mut6);
    }

    @Benchmark
    public Decimal18f byMutableDecimal7f() {
        return multipliable.by(mut7);
    }

    // --- other public methods ---

    @Benchmark
    public org.decimal4j.api.Decimal<org.decimal4j.scale.Scale11f> getValue() {
        return multipliable.getValue();
    }

    @Benchmark
    public int hashCode() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean equals() {
        return multipliable.equals(otherMultipliable);
    }

    @Benchmark
    public String toString() {
        return multipliable.toString();
    }
}
