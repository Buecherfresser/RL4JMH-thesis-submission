package bench.generated.c026;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable9f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.immutable.Decimal10f;
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
import org.decimal4j.mutable.MutableDecimal8f;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable9fBenchmark {

    private Multipliable9f multipliable;
    private Multipliable9f otherMultipliable;

    private Decimal0f dec0;
    private MutableDecimal0f mutDec0;
    private Decimal1f dec1;
    private MutableDecimal1f mutDec1;
    private Decimal2f dec2;
    private MutableDecimal2f mutDec2;
    private Decimal3f dec3;
    private MutableDecimal3f mutDec3;
    private Decimal4f dec4;
    private MutableDecimal4f mutDec4;
    private Decimal5f dec5;
    private MutableDecimal5f mutDec5;
    private Decimal6f dec6;
    private MutableDecimal6f mutDec6;
    private Decimal7f dec7;
    private MutableDecimal7f mutDec7;
    private Decimal8f dec8;
    private MutableDecimal8f mutDec8;
    private Decimal9f dec9;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setup() {
        // Base value for the Multipliable9f (use a moderate value to avoid overflow)
        dec9 = Decimal9f.valueOf(123456789L);
        multipliable = new Multipliable9f(dec9);
        otherMultipliable = new Multipliable9f(Decimal9f.valueOf(987654321L));

        dec0 = Decimal0f.ONE;
        mutDec0 = MutableDecimal0f.one();
        dec1 = Decimal1f.ONE;
        mutDec1 = MutableDecimal1f.one();
        dec2 = Decimal2f.ONE;
        mutDec2 = MutableDecimal2f.one();
        dec3 = Decimal3f.ONE;
        mutDec3 = MutableDecimal3f.one();
        dec4 = Decimal4f.ONE;
        mutDec4 = MutableDecimal4f.one();
        dec5 = Decimal5f.ONE;
        mutDec5 = MutableDecimal5f.one();
        dec6 = Decimal6f.ONE;
        mutDec6 = MutableDecimal6f.one();
        dec7 = Decimal7f.ONE;
        mutDec7 = MutableDecimal7f.one();
        dec8 = Decimal8f.ONE;
        mutDec8 = MutableDecimal8f.one();
    }

    @Benchmark
    public Decimal18f square() {
        return multipliable.square();
    }

    @Benchmark
    public Decimal18f byDecimal9f() {
        return multipliable.by(dec9);
    }

    @Benchmark
    public Decimal9f byDecimal0f() {
        return multipliable.by(dec0);
    }

    @Benchmark
    public Decimal9f byMutableDecimal0f() {
        return multipliable.by(mutDec0);
    }

    @Benchmark
    public Decimal10f byDecimal1f() {
        return multipliable.by(dec1);
    }

    @Benchmark
    public Decimal10f byMutableDecimal1f() {
        return multipliable.by(mutDec1);
    }

    @Benchmark
    public Decimal11f byDecimal2f() {
        return multipliable.by(dec2);
    }

    @Benchmark
    public Decimal11f byMutableDecimal2f() {
        return multipliable.by(mutDec2);
    }

    @Benchmark
    public Decimal12f byDecimal3f() {
        return multipliable.by(dec3);
    }

    @Benchmark
    public Decimal12f byMutableDecimal3f() {
        return multipliable.by(mutDec3);
    }

    @Benchmark
    public Decimal13f byDecimal4f() {
        return multipliable.by(dec4);
    }

    @Benchmark
    public Decimal13f byMutableDecimal4f() {
        return multipliable.by(mutDec4);
    }

    @Benchmark
    public Decimal14f byDecimal5f() {
        return multipliable.by(dec5);
    }

    @Benchmark
    public Decimal14f byMutableDecimal5f() {
        return multipliable.by(mutDec5);
    }

    @Benchmark
    public Decimal15f byDecimal6f() {
        return multipliable.by(dec6);
    }

    @Benchmark
    public Decimal15f byMutableDecimal6f() {
        return multipliable.by(mutDec6);
    }

    @Benchmark
    public Decimal16f byDecimal7f() {
        return multipliable.by(dec7);
    }

    @Benchmark
    public Decimal16f byMutableDecimal7f() {
        return multipliable.by(mutDec7);
    }

    @Benchmark
    public Decimal17f byDecimal8f() {
        return multipliable.by(dec8);
    }

    @Benchmark
    public Decimal17f byMutableDecimal8f() {
        return multipliable.by(mutDec8);
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return multipliable.hashCode();
    }

    @Benchmark
    public boolean equalsBenchmark() {
        return multipliable.equals(otherMultipliable);
    }

    @Benchmark
    public String toStringBenchmark() {
        return multipliable.toString();
    }
}
