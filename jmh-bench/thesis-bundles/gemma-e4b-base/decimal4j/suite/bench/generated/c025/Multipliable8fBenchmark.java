package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.exact.Multipliable8f;
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
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.scale.Scale8f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Multipliable8fBenchmark {

    private Multipliable8f multipliable8f;
    private Decimal<Scale8f> baseDecimal;

    // Factors for multiplication
    private Decimal<Scale8f> factorDecimal8f;
    private Decimal0f factorDecimal0f;
    private MutableDecimal0f factorMutableDecimal0f;
    private Decimal1f factorDecimal1f;
    private MutableDecimal1f factorMutableDecimal1f;
    private Decimal2f factorDecimal2f;
    private MutableDecimal2f factorMutableDecimal2f;
    private Decimal3f factorDecimal3f;
    private MutableDecimal3f factorMutableDecimal3f;
    private Decimal4f factorDecimal4f;
    private MutableDecimal4f factorMutableDecimal4f;
    private Decimal5f factorDecimal5f;
    private MutableDecimal5f factorMutableDecimal5f;
    private Decimal6f factorDecimal6f;
    private MutableDecimal6f factorMutableDecimal6f;
    private Decimal7f factorDecimal7f;
    private MutableDecimal7f factorMutableDecimal7f;
    private MutableDecimal9f factorMutableDecimal9f;
    private Decimal9f factorDecimal9f;
    private MutableDecimal10f factorMutableDecimal10f;
    private Decimal10f factorDecimal10f;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup base Decimal<Scale8f>
        // Using a simple value for testing multiplication
        baseDecimal = Decimal8f.valueOf(123456789L);

        // 2. Setup Multipliable8f instance
        multipliable8f = new Multipliable8f(baseDecimal);

        // 3. Setup factors
        // Decimal<Scale8f> factor
        factorDecimal8f = Decimal8f.valueOf(10L);

        // Decimal0f factors
        factorDecimal0f = Decimal0f.valueOf(5L);
        factorMutableDecimal0f = new MutableDecimal0f(5L);

        // Decimal1f factors
        factorDecimal1f = Decimal1f.valueOf(2L);
        factorMutableDecimal1f = new MutableDecimal1f(2L);

        // Decimal2f factors
        factorDecimal2f = Decimal2f.valueOf(3L);
        factorMutableDecimal2f = new MutableDecimal2f(3L);

        // Decimal3f factors
        factorDecimal3f = Decimal3f.valueOf(4L);
        factorMutableDecimal3f = new MutableDecimal3f(4L);

        // Decimal4f factors
        factorDecimal4f = Decimal4f.valueOf(5L);
        factorMutableDecimal4f = new MutableDecimal4f(5L);

        // Decimal5f factors
        factorDecimal5f = Decimal5f.valueOf(6L);
        factorMutableDecimal5f = new MutableDecimal5f(6L);

        // Decimal6f factors
        factorDecimal6f = Decimal6f.valueOf(7L);
        factorMutableDecimal6f = new MutableDecimal6f(7L);

        // Decimal7f factors
        factorDecimal7f = Decimal7f.valueOf(8L);
        factorMutableDecimal7f = new MutableDecimal7f(8L);

        // Decimal9f factors
        factorDecimal9f = Decimal9f.valueOf(9L);
        factorMutableDecimal9f = new MutableDecimal9f(9L);

        // Decimal10f factors
        factorDecimal10f = Decimal10f.valueOf(10L);
        factorMutableDecimal10f = new MutableDecimal10f(10L);
    }

    @Benchmark
    public Decimal16f benchmarkSquare(Blackhole bh) {
        Decimal16f result = multipliable8f.square();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkByDecimal8f(Blackhole bh) {
        Decimal16f result = multipliable8f.by(factorDecimal8f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f benchmarkByDecimal0f(Blackhole bh) {
        Decimal8f result = multipliable8f.by(factorDecimal0f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f benchmarkByMutableDecimal0f(Blackhole bh) {
        Decimal8f result = multipliable8f.by(factorMutableDecimal0f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal9f benchmarkByDecimal1f(Blackhole bh) {
        Decimal9f result = multipliable8f.by(factorDecimal1f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal9f benchmarkByMutableDecimal1f(Blackhole bh) {
        Decimal9f result = multipliable8f.by(factorMutableDecimal1f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f benchmarkByDecimal2f(Blackhole bh) {
        Decimal10f result = multipliable8f.by(factorDecimal2f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f benchmarkByMutableDecimal2f(Blackhole bh) {
        Decimal10f result = multipliable8f.by(factorMutableDecimal2f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkByDecimal3f(Blackhole bh) {
        Decimal11f result = multipliable8f.by(factorDecimal3f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkByMutableDecimal3f(Blackhole bh) {
        Decimal11f result = multipliable8f.by(factorMutableDecimal3f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f benchmarkByDecimal4f(Blackhole bh) {
        Decimal12f result = multipliable8f.by(factorDecimal4f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f benchmarkByMutableDecimal4f(Blackhole bh) {
        Decimal12f result = multipliable8f.by(factorMutableDecimal4f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal13f benchmarkByDecimal5f(Blackhole bh) {
        Decimal13f result = multipliable8f.by(factorDecimal5f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal13f benchmarkByMutableDecimal5f(Blackhole bh) {
        Decimal13f result = multipliable8f.by(factorMutableDecimal5f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkByDecimal6f(Blackhole bh) {
        Decimal14f result = multipliable8f.by(factorDecimal6f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkByMutableDecimal6f(Blackhole bh) {
        Decimal14f result = multipliable8f.by(factorMutableDecimal6f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkByDecimal7f(Blackhole bh) {
        Decimal15f result = multipliable8f.by(factorDecimal7f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkByMutableDecimal7f(Blackhole bh) {
        Decimal15f result = multipliable8f.by(factorMutableDecimal7f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f benchmarkByDecimal9f(Blackhole bh) {
        Decimal17f result = multipliable8f.by(factorDecimal9f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f benchmarkByMutableDecimal9f(Blackhole bh) {
        Decimal17f result = multipliable8f.by(factorMutableDecimal9f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkByDecimal10f(Blackhole bh) {
        Decimal18f result = multipliable8f.by(factorDecimal10f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkByMutableDecimal10f(Blackhole bh) {
        Decimal18f result = multipliable8f.by(factorMutableDecimal10f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int benchmarkHashCode(Blackhole bh) {
        int hash = multipliable8f.hashCode();
        bh.consume(hash);
        return hash;
    }

    @Benchmark
    public boolean benchmarkEquals(Blackhole bh) {
        // Compare against itself
        boolean result = multipliable8f.equals(multipliable8f);
        bh.consume(result);
        return result;
    }
}
